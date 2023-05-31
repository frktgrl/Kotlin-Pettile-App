package com.example.apppettileapp.fragment

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.StaggeredGridLayoutManager
import com.example.apppettileapp.adapter.LikeDataRecyclerAdapter
import com.example.apppettileapp.databinding.FragmentLikesDataBinding
import com.example.apppettileapp.model.Post
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query


class LikesDataFragment : Fragment() {

    private lateinit var auth : FirebaseAuth
    private lateinit var db : FirebaseFirestore
    private lateinit var binding: FragmentLikesDataBinding

    val postArrayList : ArrayList<Post> = ArrayList()
    var adapter : LikeDataRecyclerAdapter? = null


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {

        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        binding = FragmentLikesDataBinding.inflate(layoutInflater)
        val view = binding.root


        val spanCount = 2 // Sütun sayısı

        val GridLayoutManager = GridLayoutManager(context,spanCount)

        binding.likeDataRecyclerView.layoutManager = GridLayoutManager

        adapter = LikeDataRecyclerAdapter(postArrayList)
        binding.likeDataRecyclerView.adapter = adapter


        auth = FirebaseAuth.getInstance()
        db = FirebaseFirestore.getInstance()

        getDataFromFirestore()

        return view
    }

    fun getDataFromFirestore() {

        val currentUser = FirebaseAuth.getInstance().currentUser
        val currentUserId = currentUser?.uid

        db.collection("Posts").orderBy("date",
            Query.Direction.DESCENDING).addSnapshotListener { snapshot, exception ->
            if (exception != null) {
                Toast.makeText(context,exception.localizedMessage, Toast.LENGTH_LONG).show()
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

                            if (downloadUrl != null && like != null && currentUserId != null) {
                                // Kullanıcının favori postlarını bulma
                                for (likeScan in like) {

                                    if (likeScan == currentUserId){

                                        val post = Post(comment, downloadUrl, userId, like, save,postId)
                                        postArrayList.add(post)
                                    }
                                }
                            }

                        }
                        adapter!!.notifyDataSetChanged()

                    }
                }

            }
        }

    }


}