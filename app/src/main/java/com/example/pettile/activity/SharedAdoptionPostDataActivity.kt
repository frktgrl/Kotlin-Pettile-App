package com.example.pettile.activity

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.recyclerview.widget.GridLayoutManager
import com.example.pettile.adapter.SharedAdoptionPostRecyclerAdapter
import com.example.pettile.databinding.ActivitySharedAdoptionPostDataBinding
import com.example.pettile.model.AdoptionPost
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query

class SharedAdoptionPostDataActivity : AppCompatActivity() {

    private lateinit var auth : FirebaseAuth
    private lateinit var db : FirebaseFirestore
    private lateinit var binding: ActivitySharedAdoptionPostDataBinding

    val postArrayList : ArrayList<AdoptionPost> = ArrayList()
    var adapter : SharedAdoptionPostRecyclerAdapter? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding= ActivitySharedAdoptionPostDataBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)

        val spanCount = 2 // Sütun sayısı

        val GridLayoutManager = GridLayoutManager(applicationContext,spanCount)

        binding.sharedAdoptionRecyclerView.layoutManager = GridLayoutManager

        adapter = SharedAdoptionPostRecyclerAdapter(postArrayList)
        binding.sharedAdoptionRecyclerView.adapter = adapter


        auth = FirebaseAuth.getInstance()
        db = FirebaseFirestore.getInstance()

        getDataFromFirestore()
    }
    fun getDataFromFirestore() {

        db.collection("AdoptionPosts").whereEqualTo("userId", "${auth.currentUser?.uid}")
            .orderBy("date", Query.Direction.DESCENDING).
            addSnapshotListener { snapshot, exception ->
                if (exception != null) {
                   // Toast.makeText(applicationContext, exception.localizedMessage, Toast.LENGTH_LONG).show()
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
    fun backImageClicked (view : View) {

        val intent = Intent(applicationContext, SaveActivity::class.java)
        startActivity(intent)
        finish()

    }

}