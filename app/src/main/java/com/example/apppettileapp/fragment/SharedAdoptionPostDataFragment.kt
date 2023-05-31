package com.example.apppettileapp.fragment

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.recyclerview.widget.GridLayoutManager
import com.example.apppettileapp.adapter.SharedAdoptionPostRecyclerAdapter
import com.example.apppettileapp.adapter.SharedPostRecyclerAdapter
import com.example.apppettileapp.databinding.FragmentSharedAdoptionPostDataBinding
import com.example.apppettileapp.databinding.FragmentSharedPostDataBinding
import com.example.apppettileapp.model.AdoptionPost
import com.example.apppettileapp.model.Post
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query


class SharedAdoptionPostDataFragment : Fragment() {

    private lateinit var auth : FirebaseAuth
    private lateinit var db : FirebaseFirestore
    private lateinit var binding: FragmentSharedAdoptionPostDataBinding

    val postArrayList : ArrayList<AdoptionPost> = ArrayList()
    var adapter : SharedAdoptionPostRecyclerAdapter? = null


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {

        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentSharedAdoptionPostDataBinding.inflate(layoutInflater)
        val view = binding.root


        val spanCount = 2 // Sütun sayısı

        val GridLayoutManager = GridLayoutManager(context,spanCount)

        binding.sharedAdoptionRecyclerView.layoutManager = GridLayoutManager

        adapter = SharedAdoptionPostRecyclerAdapter(postArrayList)
        binding.sharedAdoptionRecyclerView.adapter = adapter


        auth = FirebaseAuth.getInstance()
        db = FirebaseFirestore.getInstance()

        getDataFromFirestore()

        return view
    }

    fun getDataFromFirestore() {

        db.collection("AdoptionPosts").whereEqualTo("userId", "${auth.currentUser?.uid}")
            .orderBy("date", Query.Direction.DESCENDING).
            addSnapshotListener { snapshot, exception ->
                if (exception != null) {
                    Toast.makeText(context, exception.localizedMessage, Toast.LENGTH_LONG).show()
                } else {

                    if (snapshot != null) {
                        if (!snapshot.isEmpty) {

                            postArrayList.clear()

                            val documents = snapshot.documents
                            for (document in documents) {
                                val title = document.get("title") as String
                                val name = document.get("name") as String
                                val location = document.get("location") as String
                                val downloadUrl = document.get("downloadUrl") as String
                                val userId = document.get("userId") as String
                                val favorite = document.get("favorite") as List<String> // recomment alanını dizi olarak okuyun
                                val adoptionPostId = document.get("adoptionPostId") as String
                                val post = AdoptionPost(downloadUrl,title,name,location, userId,favorite,adoptionPostId)
                                postArrayList.add(post)
                            }
                            adapter!!.notifyDataSetChanged()

                        }
                    }

                }
            }

    }

}