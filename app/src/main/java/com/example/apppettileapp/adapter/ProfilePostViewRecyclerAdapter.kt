package com.example.apppettileapp.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.apppettileapp.R
import com.example.apppettileapp.databinding.RecyclerRowPostViewBinding
import com.example.apppettileapp.databinding.RecyclerRowUserProfileBinding
import com.example.apppettileapp.model.Post
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore
import com.squareup.picasso.Picasso

class ProfilePostViewRecyclerAdapter (private val postArrayList : ArrayList<Post>) : RecyclerView.Adapter<ProfilePostViewRecyclerAdapter.PostHolder>() {

    private lateinit var auth: FirebaseAuth
    private lateinit var db: FirebaseFirestore


    class PostHolder(val binding: RecyclerRowPostViewBinding) :
        RecyclerView.ViewHolder(binding.root) {

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PostHolder {
        val binding = RecyclerRowPostViewBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return PostHolder(binding)
    }

    override fun getItemCount(): Int {
        return postArrayList.size

    }

    override fun onBindViewHolder(holder: PostHolder, position: Int) {
        holder.binding.recyclerCommentText.text = postArrayList.get(position).comment
        Picasso.get().load(postArrayList[position].downloadUrl).into(holder.binding.recyclerImageView)

        auth = FirebaseAuth.getInstance()
        db = FirebaseFirestore.getInstance()


        db.collection("Users")
            .get()
            .addOnSuccessListener { documents ->
                for (document in documents) {

                    val userId = document.get("userId") as String

                    if (userId == postArrayList.get(position).userId) {

                        val username = document.get("username") as String
                        val downloadUrl = document.get("downloadUrl") as String
                        val name = document.get("name") as String
                        val userId = document.get("userId") as String
                        holder.binding.recyclerEmailText.text = username
                        Picasso.get().load(downloadUrl).into(holder.binding.profileImage)

                        for (likeScan in postArrayList[position].like) {

                            if (likeScan == userId){

                                holder.binding.likeButton.setImageResource(R.drawable.ic_likes_active)
                            }
                        }
                        for (saveScan in postArrayList[position].save) {

                            if (saveScan == userId){

                                holder.binding.saveButton.setImageResource(R.drawable.save_flag_active)
                            }
                        }


                    }

                }
            }



        holder.binding.likeButton.setOnClickListener {

            val postId = postArrayList[position].postId
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
            val postId = postArrayList[position].postId
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