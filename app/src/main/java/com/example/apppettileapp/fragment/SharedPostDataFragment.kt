package com.example.apppettileapp.fragment

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.recyclerview.widget.GridLayoutManager
import com.example.apppettileapp.adapter.SharedPostRecyclerAdapter
import com.example.apppettileapp.databinding.FragmentSharedPostDataBinding
import com.example.apppettileapp.model.Post
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query


class SharedPostDataFragment : Fragment() {

    private lateinit var auth : FirebaseAuth
    private lateinit var db : FirebaseFirestore
    private lateinit var binding: FragmentSharedPostDataBinding

    val postArrayList : ArrayList<Post> = ArrayList()
    var adapter : SharedPostRecyclerAdapter? = null


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {

        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentSharedPostDataBinding.inflate(layoutInflater)
        val view = binding.root


        val spanCount = 2 // Sütun sayısı

        val GridLayoutManager = GridLayoutManager(context,spanCount)

        binding.sharedPostRecyclerView.layoutManager = GridLayoutManager

        adapter = SharedPostRecyclerAdapter(postArrayList)
        binding.sharedPostRecyclerView.adapter = adapter


        auth = FirebaseAuth.getInstance()
        db = FirebaseFirestore.getInstance()

        getDataFromFirestore()

        return view
    }

    fun getDataFromFirestore() {

        db.collection("Posts").whereEqualTo("userId", "${auth.currentUser?.uid}")
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
                                val downloadUrl = document.get("downloadUrl") as String
                                val userId = document.get("userId") as String
                                val like = document.get("like") as List<String> // like alanını dizi olarak okuyun
                                val save = document.get("save") as List<String> // recomment alanını dizi olarak okuyun
                                val postId = document.get("postId") as String

                                val post = Post(comment, downloadUrl, userId, like, save,postId)
                                postArrayList.add(post)
                            }
                            adapter!!.notifyDataSetChanged()

                        }
                    }

                }
            }

    }

}