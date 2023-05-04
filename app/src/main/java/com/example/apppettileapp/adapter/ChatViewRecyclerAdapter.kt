package com.example.apppettileapp.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.apppettileapp.databinding.RecyclerRowChatViewBinding
import com.example.apppettileapp.databinding.RecyclerRowHomeBinding
import com.example.apppettileapp.databinding.RecyclerRowUserProfileBinding
import com.example.apppettileapp.model.ChatView
import com.example.apppettileapp.model.Post
import com.squareup.picasso.Picasso

class ChatViewRecyclerAdapter (private val chatList : ArrayList<ChatView>) : RecyclerView.Adapter<ChatViewRecyclerAdapter.PostHolder>() {

    class PostHolder(val binding: RecyclerRowChatViewBinding) : RecyclerView.ViewHolder(binding.root) {

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PostHolder {
        val binding = RecyclerRowChatViewBinding.inflate(LayoutInflater.from(parent.context),parent,false)
        return PostHolder(binding)
    }

    override fun getItemCount(): Int {
        return chatList.size

    }

    override fun onBindViewHolder(holder: PostHolder, position: Int) {
        holder.binding.textView4.text = chatList.get(position).userEmail
//        holder.binding.recyclerCommentText.text = postList.get(position).comment
//        Picasso.get().load(postList[position].downloadUrl).into(holder.binding.recyclerImageView)
    }

}