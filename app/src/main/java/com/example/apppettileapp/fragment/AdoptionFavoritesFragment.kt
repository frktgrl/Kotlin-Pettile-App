package com.example.apppettileapp.fragment

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.apppettileapp.adapter.AdoptionFavoriteRecyclerAdapter
import com.example.apppettileapp.databinding.FragmentAdoptionFavoritesBinding
import com.example.apppettileapp.model.AdoptionFavorite
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query


class AdoptionFavoritesFragment : Fragment() {

    private lateinit var binding: FragmentAdoptionFavoritesBinding
    private lateinit var auth : FirebaseAuth
    private lateinit var db : FirebaseFirestore

    val postArrayList : ArrayList<AdoptionFavorite> = ArrayList()
    var adapter : AdoptionFavoriteRecyclerAdapter? = null


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding= FragmentAdoptionFavoritesBinding.inflate(layoutInflater)
        val view = binding.root


        //yanyana cardview için gridlayoutmanager kullan
        binding.adoptionFavoriteRecyclerView.layoutManager = LinearLayoutManager(activity)

        adapter = AdoptionFavoriteRecyclerAdapter(postArrayList)
        binding.adoptionFavoriteRecyclerView.adapter = adapter


        auth = FirebaseAuth.getInstance()
        db = FirebaseFirestore.getInstance()

        getDataFromFirestore()


        return view
    }
    fun getDataFromFirestore() {
        val currentUser = FirebaseAuth.getInstance().currentUser
        val currentUserId = currentUser?.uid

        db.collection("AdoptionPosts")
            .orderBy("date", Query.Direction.DESCENDING)
            .addSnapshotListener { snapshot, exception ->
                if (exception != null) {
                    Toast.makeText(context, exception.localizedMessage, Toast.LENGTH_LONG).show()
                } else {
                    if (snapshot != null) {
                        postArrayList.clear()

                        for (document in snapshot.documents) {

                            val title = document.get("title") as String
                            val name = document.get("name") as String
                            val location = document.get("location") as String
                            val downloadUrl = document.get("downloadUrl") as String
                            val userId = document.get("userId") as String
                            val like = document.get("like") as List<String> // like alanını dizi olarak okuyun
                            val favorite = document.get("favorite") as? List<Map<String, Any>>

                            if (downloadUrl != null && userId != null && favorite != null && currentUserId != null) {
                                // Kullanıcının favori postlarını bulma
                                for (favMap in favorite) {
                                    if (favMap["userId"] == currentUserId) {
                                        val post = AdoptionFavorite(downloadUrl,title,name,location, userId,like, favorite)
                                        postArrayList.add(post)
                                        break
                                    }
                                }
                            }
                        }

                        adapter?.notifyDataSetChanged()
                    }
                }
            }
    }



}