package com.example.apppettileapp.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.apppettileapp.databinding.RecyclerRowUserProfileBinding
import com.example.apppettileapp.model.Post
import com.google.firebase.auth.FirebaseAuth
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
    }
}