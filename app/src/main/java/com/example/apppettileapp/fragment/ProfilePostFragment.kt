package com.example.apppettileapp.fragment

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.apppettileapp.adapter.UserProfileRecyclerAdapter
import com.example.apppettileapp.databinding.FragmentProfilePostBinding
import com.example.apppettileapp.model.Post
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query


class ProfilePostFragment : Fragment() {

    private lateinit var auth: FirebaseAuth
    private lateinit var db: FirebaseFirestore
    private lateinit var binding: FragmentProfilePostBinding

    val postArrayList: ArrayList<Post> = ArrayList()
    var adapter: UserProfileRecyclerAdapter? = null


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentProfilePostBinding.inflate(layoutInflater)
        val view = binding.root

        binding.userProfileRecyclerView.layoutManager = LinearLayoutManager(activity)

        adapter = UserProfileRecyclerAdapter(postArrayList)
        binding.userProfileRecyclerView.adapter = adapter

        auth = FirebaseAuth.getInstance()
        db = FirebaseFirestore.getInstance()

        getDataFromFireStore()

        return view
    }



    fun getDataFromFireStore() {

        db.collection("Posts").whereEqualTo("userEmail", "${auth.currentUser?.email.toString()}")
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
                                val comment = document.get("comment") as String
                                val useremail = document.get("userEmail") as String
                                val downloadUrl = document.get("downloadUrl") as String
                                //val timestamp = document.get("date") as Timestamp
                                //val date = timestamp.toDate()

                                println(comment)
                                println(useremail)
                                println(downloadUrl)

                                val post = Post(useremail, comment, downloadUrl)
                                postArrayList.add(post)
                            }
                            adapter!!.notifyDataSetChanged()

                        }
                    }

                }
            }


    }

}