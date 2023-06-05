package com.example.pettile.activity

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.pettile.adapter.AdoptionFavoriteRecyclerAdapter
import com.example.pettile.databinding.ActivitySaveBinding
import com.example.pettile.databinding.ActivitySaveDataBinding
import com.example.pettile.model.AdoptionFavorite
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query

class SaveDataActivity : AppCompatActivity() {

    private lateinit var binding: ActivitySaveDataBinding
    private lateinit var auth : FirebaseAuth
    private lateinit var db : FirebaseFirestore

    val postArrayList : ArrayList<AdoptionFavorite> = ArrayList()
    var adapter : AdoptionFavoriteRecyclerAdapter? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding= ActivitySaveDataBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)


        //yanyana cardview için gridlayoutmanager kullan
        binding.saveDataRecyclerView.layoutManager = LinearLayoutManager(applicationContext)

        adapter = AdoptionFavoriteRecyclerAdapter(postArrayList)
        binding.saveDataRecyclerView.adapter = adapter


        auth = FirebaseAuth.getInstance()
        db = FirebaseFirestore.getInstance()

        getDataFromFirestore()


    }

    fun getDataFromFirestore() {
        val currentUser = FirebaseAuth.getInstance().currentUser
        val currentUserId = currentUser?.uid

        db.collection("AdoptionPosts")
            .orderBy("date", Query.Direction.DESCENDING)
            .addSnapshotListener { snapshot, exception ->
                if (exception != null) {
                    Toast.makeText(applicationContext, exception.localizedMessage, Toast.LENGTH_LONG).show()
                } else {
                    if (snapshot != null) {
                        postArrayList.clear()

                        for (document in snapshot.documents) {

                            val title = document.get("title") as String
                            val name = document.get("name") as String
                            val location = document.get("location") as String
                            val downloadUrl = document.get("downloadUrl") as String
                            val userId = document.get("userId") as String
                            val favorite = document.get("favorite") as List<String>
                            val adoptionPostId = document.get("adoptionPostId") as String

                            if (downloadUrl != null && favorite != null && currentUserId != null) {
                                // Kullanıcının favori postlarını bulma
                                for (favoriteScan in favorite) {

                                    if (favoriteScan == currentUserId){

                                        val post = AdoptionFavorite(downloadUrl,title,name,location,userId,favorite,adoptionPostId)
                                        postArrayList.add(post)
                                    }
                                }
                            }
                        }

                        adapter?.notifyDataSetChanged()
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