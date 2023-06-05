package com.example.pettile.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.navigation.Navigation
import androidx.recyclerview.widget.RecyclerView
import com.example.pettile.databinding.RecyclerRowChatViewBinding
import com.example.pettile.fragment.ChatViewFragmentDirections
import com.example.pettile.model.ChatView
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

        holder.binding.usernameText.text = chatList.get(position).username
        Picasso.get().load(chatList[position].downloadUrl).into(holder.binding.userPhotoView)

        //CardViewa tıkla sohbet ekranına gitsin
        val action = ChatViewFragmentDirections.actionChatViewFragmentToChatFragment(
            userEmail = chatList[position].userEmail,
            name = chatList[position].name,
            username = chatList.get(position).username,
            downloadUrl = chatList[position].downloadUrl,
            userId = chatList[position].userId

        )
        //CardViewa tıkla sohbet ekranına gitsin
        holder.binding.cardView.setOnClickListener {
            val navController = Navigation.findNavController(holder.binding.cardView)
            navController.navigate(action)

        }

    }

}