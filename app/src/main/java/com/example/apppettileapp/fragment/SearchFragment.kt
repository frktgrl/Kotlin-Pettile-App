package com.example.apppettileapp.fragment

import android.content.Context
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.apppettileapp.adapter.SearchRecyclerAdapter
import com.example.apppettileapp.databinding.FragmentSearchBinding
import com.example.apppettileapp.model.UserSearch
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore



class SearchFragment : Fragment() {


    private lateinit var binding: FragmentSearchBinding
    private lateinit var auth : FirebaseAuth
    private lateinit var db : FirebaseFirestore

    val userArrayList : ArrayList<UserSearch> = ArrayList()
    var adapter : SearchRecyclerAdapter? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        binding = FragmentSearchBinding.inflate(layoutInflater)
        val view = binding.root

        auth = FirebaseAuth.getInstance()
        db = FirebaseFirestore.getInstance()

        binding.searchRecyclerView.layoutManager = LinearLayoutManager(activity)

        adapter = SearchRecyclerAdapter(userArrayList)
        binding.searchRecyclerView.adapter = adapter



        binding.searchInput.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
                // Burada yapılacak bir işlem yok
            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                val searchText = s.toString().trim()
                getDataFromFirestoreUser(searchText,context!!)
            }

            override fun afterTextChanged(s: Editable?) {
                // Burada yapılacak bir işlem yok
            }
        })

        return view
    }

    fun getDataFromFirestoreUser(str: String, context: Context) {
        db.collection("Users")
            .orderBy("username")
            .startAt(str)
            .endAt(str + "\uf8ff")
            .addSnapshotListener { snapshot, exception ->
                if (exception != null) {
                    Toast.makeText(context, exception.localizedMessage, Toast.LENGTH_LONG).show()
                } else {
                    if (snapshot != null) {
                        if (!snapshot.isEmpty) {
                            userArrayList.clear()
                            val documents = snapshot.documents
                            for (document in documents) {
                                val name = document.get("name") as String
                                val username = document.get("username") as String
                                val userEmail = document.get("userEmail") as String
                                val downloadUrl = document.get("downloadUrl") as String
                                val userId = document.get("userId") as String
                                val followers = document.get("followers").toString()
                                val following = document.get("following").toString()
                                val userSearch = UserSearch(name, username,userEmail ,downloadUrl,userId,followers,following)
                                userArrayList.add(userSearch)
                            }
                            adapter!!.notifyDataSetChanged()
                        }
                    }
                }
            }
    }


}