package com.example.apppettileapp.adapter

import android.content.ContentValues
import android.content.ContentValues.TAG
import android.content.Context
import android.content.SharedPreferences
import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.navigation.Navigation
import androidx.recyclerview.widget.RecyclerView
import com.example.apppettileapp.R
import com.example.apppettileapp.databinding.RecyclerRowHomeBinding
import com.example.apppettileapp.fragment.HomeFragmentDirections
import com.example.apppettileapp.model.Post
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore
import com.squareup.picasso.Picasso

class FeedRecyclerAdapter(private val postList: ArrayList<Post>) :
    RecyclerView.Adapter<FeedRecyclerAdapter.PostHolder>() {

    private lateinit var auth: FirebaseAuth
    private lateinit var db: FirebaseFirestore
    private lateinit var context: Context

    class PostHolder(val binding: RecyclerRowHomeBinding) : RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PostHolder {
        context = parent.context
        val binding =
            RecyclerRowHomeBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return PostHolder(binding)
    }

    override fun getItemCount(): Int {
        return postList.size
    }

    override fun onBindViewHolder(holder: PostHolder, position: Int) {

        holder.binding.recyclerCommentText.text = postList[position].comment
        Picasso.get().load(postList[position].downloadUrl).into(holder.binding.recyclerImageView)

        auth = FirebaseAuth.getInstance()
        db = FirebaseFirestore.getInstance()

        // postta username ve profil foto için
        db.collection("Users")
            .get()
            .addOnSuccessListener { documents ->
                for (document in documents) {
                    val userId = document.get("userId") as String
                    if (userId == postList[position].userId) {
                        val username = document.get("username") as String
                        val downloadUrl = document.get("downloadUrl") as String
                        val name = document.get("name") as String
                        val followers = document.get("followers").toString()
                        val following = document.get("following").toString()
                        holder.binding.recyclerEmailText.text = username
                        Picasso.get().load(downloadUrl).into(holder.binding.profileImage)

                        for (likeScan in postList[position].like) {

                            if (likeScan == userId){

                                holder.binding.likeButton.setImageResource(R.drawable.ic_likes_active)
                            }
                        }
                        for (saveScan in postList[position].save) {

                            if (saveScan == userId){

                                holder.binding.saveButton.setImageResource(R.drawable.save_flag_active)
                            }
                        }



                        val action =
                            HomeFragmentDirections.actionHomeFragmentToProfileViewFragment(
                                name = name,
                                username = username,
                                downloadUrl = downloadUrl,
                                userId = userId,
                                followers = followers,
                                following = following
                            )

                        // Kullanıcı kendisi ise profil sayfasına değilse görüntülemeye
                        if (userId != "${auth.currentUser?.uid}") {
                            holder.binding.profileImage.setOnClickListener {
                                Navigation.findNavController(holder.itemView).navigate(action)
                            }
                            holder.binding.recyclerEmailText.setOnClickListener {
                                Navigation.findNavController(holder.itemView).navigate(action)
                            }
                        } else {
                            val action =
                                HomeFragmentDirections.actionHomeFragmentToProfileFragment()
                            holder.binding.profileImage.setOnClickListener {
                                Navigation.findNavController(holder.itemView).navigate(action)
                            }
                            holder.binding.recyclerEmailText.setOnClickListener {
                                Navigation.findNavController(holder.itemView).navigate(action)
                            }
                        }
                    }
                }
            }
            .addOnFailureListener { exception ->
                // hata durumu
            }


        // likeButton onClickListener'ında
        holder.binding.likeButton.setOnClickListener {

            val postId = postList[position].postId
            val currentUserId = auth.currentUser!!.uid

            // Firebase Firestore işlemleri
            val db = FirebaseFirestore.getInstance()
            val postsRef = db.collection("Posts")

            postsRef.whereEqualTo("postId", postId)
                .get()
                .addOnSuccessListener { querySnapshot ->
                    for (document in querySnapshot.documents) {
                        val postRef = postsRef.document(document.id)
                        val likes = document.get("like") as ArrayList<String>?

                        if (likes != null && likes.contains(currentUserId)) {
                            postRef.update("like", FieldValue.arrayRemove(currentUserId))
                                .addOnSuccessListener {
                                    println("Like Removed")
                                    // Burada butonun eski haline dönmesini sağlayabilirsiniz
                                    holder.binding.likeButton.setImageResource(R.drawable.ic_likes)
                                }
                                .addOnFailureListener { e ->
                                    // Hata durumunda yapılacak işlemler
                                }
                        } else {
                            postRef.update("like", FieldValue.arrayUnion(currentUserId))
                                .addOnSuccessListener {
                                    println("Like Added")
                                    // Burada butonun yeni haline dönmesini sağlayabilirsiniz
                                    // Örneğin: saveButton.setImageResource(R.drawable.like_active)
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



            holder.binding.saveButton.setOnClickListener {
                val postId = postList[position].postId
                val currentUserId = auth.currentUser!!.uid

                // Firebase Firestore işlemleri
                val db = FirebaseFirestore.getInstance()
                val postsRef = db.collection("Posts")

                postsRef.whereEqualTo("postId", postId)
                    .get()
                    .addOnSuccessListener { querySnapshot ->
                        for (document in querySnapshot.documents) {
                            val postRef = postsRef.document(document.id)
                            val saves = document.get("save") as ArrayList<String>?

                            if (saves != null && saves.contains(currentUserId)) {
                                postRef.update("save", FieldValue.arrayRemove(currentUserId))
                                    .addOnSuccessListener {
                                        println("Save Removed")
                                        // Burada butonun eski haline dönmesini sağlayabilirsiniz
                                        holder.binding.saveButton.setImageResource(R.drawable.save_flag)
                                    }
                                    .addOnFailureListener { e ->
                                        // Hata durumunda yapılacak işlemler
                                    }
                            } else {
                                postRef.update("save", FieldValue.arrayUnion(currentUserId))
                                    .addOnSuccessListener {
                                        println("Save Added")
                                        // Burada butonun yeni haline dönmesini sağlayabilirsiniz
                                        holder.binding.saveButton.setImageResource(R.drawable.save_flag_active)
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








