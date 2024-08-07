package com.example.pettile.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.pettile.R
import com.example.pettile.databinding.RecyclerRowUserProfileBinding
import com.example.pettile.model.Post
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore
import com.squareup.picasso.Picasso

class ProfilePostRecyclerAdapter (private val postList : ArrayList<Post>) : RecyclerView.Adapter<ProfilePostRecyclerAdapter.PostHolder>() {

    private lateinit var auth: FirebaseAuth
    private lateinit var db: FirebaseFirestore


    class PostHolder(val binding: RecyclerRowUserProfileBinding) :
        RecyclerView.ViewHolder(binding.root) {

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PostHolder {
        val binding = RecyclerRowUserProfileBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return PostHolder(binding)
    }

    override fun getItemCount(): Int {
        return postList.size

    }

    override fun onBindViewHolder(holder: PostHolder, position: Int) {
        holder.binding.recyclerCommentText.text = postList.get(position).comment
        Picasso.get().load(postList[position].downloadUrl).into(holder.binding.recyclerImageView)

        auth = FirebaseAuth.getInstance()
        db = FirebaseFirestore.getInstance()

        for (likeScan in postList[position].like) {

            if (likeScan == auth.currentUser!!.uid){

                holder.binding.likeButton.setImageResource(R.drawable.ic_likes_active)
            }
        }
        for (saveScan in postList[position].save) {

            if (saveScan == auth.currentUser!!.uid){

                holder.binding.saveButton.setImageResource(R.drawable.save_flag_active)
            }
        }


        db.collection("Users")
            .get()
            .addOnSuccessListener { documents ->
                for (document in documents) {

                    val userId = document.get("userId") as String

                    if (userId == postList.get(position).userId) {

                        val username = document.get("username") as String
                        val downloadUrl = document.get("downloadUrl") as String
                        val name = document.get("name") as String
                        val userId = document.get("userId") as String
                        holder.binding.recyclerEmailText.text = username
                        Picasso.get().load(downloadUrl).into(holder.binding.profileImage)


                    }

                }
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

                                    holder.binding.likeButton.setImageResource(R.drawable.ic_likes_active)
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