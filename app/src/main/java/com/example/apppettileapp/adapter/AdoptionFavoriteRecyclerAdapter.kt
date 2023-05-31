package com.example.apppettileapp.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.navigation.Navigation
import androidx.recyclerview.widget.RecyclerView
import com.example.apppettileapp.R
import com.example.apppettileapp.databinding.RecyclerRowAdoptionFavoriteBinding
import com.example.apppettileapp.fragment.AdoptionFavoritesFragmentDirections
import com.example.apppettileapp.fragment.AdoptionFragmentDirections
import com.example.apppettileapp.model.AdoptionFavorite
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore
import com.squareup.picasso.Picasso

class AdoptionFavoriteRecyclerAdapter (private var postArrayList : ArrayList<AdoptionFavorite>) : RecyclerView.Adapter<AdoptionFavoriteRecyclerAdapter.PostHolder>() {

    private lateinit var auth: FirebaseAuth
    private lateinit var db: FirebaseFirestore

    class PostHolder(val binding: RecyclerRowAdoptionFavoriteBinding) : RecyclerView.ViewHolder(binding.root) {

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PostHolder {
        val binding = RecyclerRowAdoptionFavoriteBinding.inflate(LayoutInflater.from(parent.context),parent,false)
        return PostHolder(binding)
    }

    override fun getItemCount(): Int {
        return postArrayList.size

    }

    override fun onBindViewHolder(holder: PostHolder, position: Int) {
        holder.binding.titleText.text = postArrayList.get(position).title // başlık
        holder.binding.nameText.text = postArrayList.get(position).name // başlık
        holder.binding.locationText.text = postArrayList.get(position).location // başlık
        Picasso.get().load(postArrayList[position].downloadUrl).into(holder.binding.petView) //görsel


        auth = FirebaseAuth.getInstance()
        db = FirebaseFirestore.getInstance()

        // posttan profile gitmek için
        db.collection("Users")
            .get()
            .addOnSuccessListener { documents ->
                for (document in documents) {
                    val userId = document.get("userId") as String
                    if (userId == postArrayList[position].userId) {
                        val username = document.get("username") as String
                        val downloadUrl = document.get("downloadUrl") as String
                        val name = document.get("name") as String
                        val followers = document.get("followers").toString()
                        val following = document.get("following").toString()

                        val action =
                            AdoptionFragmentDirections.actionAdoptionFragmentToProfileViewFragment(
                                name = name,
                                username = username,
                                downloadUrl = downloadUrl,
                                userId = userId,
                                followers = followers,
                                following = following
                            )

                        // Kullanıcı kendisi ise profil sayfasına değilse görüntülemeye
                        if (userId != "${auth.currentUser?.uid}") {
                            holder.binding.cardView.setOnClickListener {
                                val navController = Navigation.findNavController(holder.binding.cardView)
                                navController.navigate(action)
                            }

                        } else {
                            val action =
                                AdoptionFragmentDirections.actionAdoptionFragmentToProfileFragment()
                            holder.binding.cardView.setOnClickListener {
                                val navController = Navigation.findNavController(holder.binding.cardView)
                                navController.navigate(action)
                            }

                        }
                    }
                }
            }
            .addOnFailureListener { exception ->
                // hata durumu
            }

        holder.binding.saveButton.setOnClickListener {

            val adoptionPostId = postArrayList[position].adoptionPostId
            val currentUserId = auth.currentUser!!.uid

            // Firebase Firestore işlemleri
            val db = FirebaseFirestore.getInstance()
            val postsRef = db.collection("AdoptionPosts")

            postsRef.whereEqualTo("adoptionPostId", adoptionPostId)
                .get()
                .addOnSuccessListener { querySnapshot ->
                    for (document in querySnapshot.documents) {
                        val postRef = postsRef.document(document.id)
                        val favorites = document.get("favorite") as ArrayList<String>?

                        if (favorites != null && favorites.contains(currentUserId)) {
                            postRef.update("favorite", FieldValue.arrayRemove(currentUserId))
                                .addOnSuccessListener {
                                    println("Favorite Removed")
                                }
                                .addOnFailureListener { e ->
                                    // Hata durumunda yapılacak işlemler
                                }
                        }
                    }
                }
                .addOnFailureListener { e ->
                    // Hata durumunda yapılacak işlemler
                }



        }
    }


}