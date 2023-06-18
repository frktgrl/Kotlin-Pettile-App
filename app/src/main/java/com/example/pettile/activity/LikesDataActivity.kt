package com.example.pettile.activity

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.recyclerview.widget.GridLayoutManager
import com.example.pettile.adapter.LikeDataRecyclerAdapter
import com.example.pettile.databinding.ActivityLikesDataBinding
import com.example.pettile.model.Post
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query

class LikesDataActivity : AppCompatActivity() {

    private lateinit var auth : FirebaseAuth
    private lateinit var db : FirebaseFirestore
    private lateinit var binding: ActivityLikesDataBinding

    val postArrayList : ArrayList<Post> = ArrayList()
    var adapter : LikeDataRecyclerAdapter? = null


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityLikesDataBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)


        val spanCount = 2 // Sütun sayısı

        val GridLayoutManager = GridLayoutManager(applicationContext,spanCount)

        binding.likeDataRecyclerView.layoutManager = GridLayoutManager

        adapter = LikeDataRecyclerAdapter(postArrayList)
        binding.likeDataRecyclerView.adapter = adapter


        auth = FirebaseAuth.getInstance()
        db = FirebaseFirestore.getInstance()

        getDataFromFirestore()





    }

    fun getDataFromFirestore() {

        val currentUser = FirebaseAuth.getInstance().currentUser
        val currentUserId = currentUser?.uid

        db.collection("Posts").orderBy("date",
            Query.Direction.DESCENDING).addSnapshotListener { snapshot, exception ->
            if (exception != null) {
               // Toast.makeText(applicationContext,exception.localizedMessage, Toast.LENGTH_LONG).show()
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
    fun backImageClicked (view : View) {

        val intent = Intent(applicationContext, SaveActivity::class.java)
        startActivity(intent)
        finish()

    }




}