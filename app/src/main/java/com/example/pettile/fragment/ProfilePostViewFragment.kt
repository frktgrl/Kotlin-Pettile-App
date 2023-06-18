package com.example.pettile.fragment

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.pettile.adapter.ProfilePostViewRecyclerAdapter
import com.example.pettile.databinding.FragmentProfilePostViewBinding
import com.example.pettile.model.Post
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query


class ProfilePostViewFragment() : Fragment() {


    private lateinit var auth: FirebaseAuth
    private lateinit var db: FirebaseFirestore
    private lateinit var binding: FragmentProfilePostViewBinding

    val postArrayList: ArrayList<Post> = ArrayList()
    var adapter: ProfilePostViewRecyclerAdapter? = null



    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentProfilePostViewBinding.inflate(layoutInflater)
        val view = binding.root

        binding.profileViewRecyclerView.layoutManager = LinearLayoutManager(activity)

        adapter = ProfilePostViewRecyclerAdapter(postArrayList)
        binding.profileViewRecyclerView.adapter = adapter

        auth = FirebaseAuth.getInstance()
        db = FirebaseFirestore.getInstance()

        getDataFromFireStore()


        return view
    }



    private fun getDataFromFireStore() {
        db.collection("Posts")
            .whereEqualTo("userId", arguments?.getString("userId"))
            .orderBy("date", Query.Direction.DESCENDING)
            .addSnapshotListener { snapshot, exception ->
                if (exception != null) {
                   // Toast.makeText(context, exception.localizedMessage, Toast.LENGTH_LONG).show()
                } else {
                    if (snapshot != null && !snapshot.isEmpty) {
                        postArrayList.clear()
                        val documents = snapshot.documents
                        for (document in documents) {
                            val comment = document.get("comment") as String
                            val downloadUrl = document.get("downloadUrl") as String
                            val userId = document.get("userId") as String
                            val like = document.get("like") as List<String> // like alanını dizi olarak okuyun
                            val save = document.get("save") as List<String> // recomment alanını dizi olarak okuyun
                            val postId = document.get("postId") as String

                            val post = Post(comment, downloadUrl, userId, like, save, postId)
                            postArrayList.add(post)
                        }
                        adapter?.notifyDataSetChanged()
                    }
                }
            }
    }

    //gelen verileri almak için
    companion object {
        fun newInstanceForPostView(name: String?, username: String?, userId: String?): ProfilePostViewFragment {
            val fragment = ProfilePostViewFragment()
            val args = Bundle()
            args.putString("name", name)
            args.putString("username", username)
            args.putString("userId", userId)
            fragment.arguments = args
            return fragment
        }
    }

}