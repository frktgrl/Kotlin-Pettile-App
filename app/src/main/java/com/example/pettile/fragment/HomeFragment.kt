package com.example.pettile.fragment

import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.pettile.activity.PostCreateActivity
import com.example.pettile.adapter.FeedRecyclerAdapter
import com.example.pettile.databinding.FragmentHomeBinding
import com.example.pettile.model.Post
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query


class HomeFragment : Fragment() {


    private lateinit var auth : FirebaseAuth
    private lateinit var db : FirebaseFirestore
    private lateinit var binding: FragmentHomeBinding

    val postArrayList : ArrayList<Post> = ArrayList()
    var adapter : FeedRecyclerAdapter? = null


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment

        binding = FragmentHomeBinding.inflate(layoutInflater)
        val view = binding.root


        binding.feedRecyclerView.layoutManager = LinearLayoutManager(activity)

        adapter = FeedRecyclerAdapter(postArrayList)
        binding.feedRecyclerView.adapter = adapter


        auth = FirebaseAuth.getInstance()
        db = FirebaseFirestore.getInstance()

        getDataFromFirestore()
        postClicked (view)
        return view


    }


    fun getDataFromFirestore() {

        db.collection("Posts").orderBy("date",
            Query.Direction.DESCENDING).addSnapshotListener { snapshot, exception ->
            if (exception != null) {
                // Toast.makeText(context,exception.localizedMessage, Toast.LENGTH_LONG).show()
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

    fun postClicked (view: View) {

        binding.postImage.setOnClickListener {
            println("basıldı")
            val intent = Intent(activity, PostCreateActivity::class.java)
            startActivity(intent)
        }
    }

}