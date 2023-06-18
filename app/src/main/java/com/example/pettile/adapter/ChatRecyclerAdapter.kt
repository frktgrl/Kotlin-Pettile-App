package com.example.pettile.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.AsyncListDiffer
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.example.pettile.R
import com.example.pettile.model.Chat
import com.google.firebase.auth.FirebaseAuth
import com.squareup.picasso.Picasso


class ChatRecyclerAdapter : RecyclerView.Adapter<ChatRecyclerAdapter.ChatHolder>() {

    private val VIEW_TYPE_MESSAGE_SENT = 1  //gönderilen mesaj
    private val VIEW_TYPE_MESSAGE_RECEIVED = 2  //alınan mesaj



    class ChatHolder(itemView : View) : RecyclerView.ViewHolder(itemView) {

        val message: TextView = itemView.findViewById(R.id.message)
        val profileImage: ImageView = itemView.findViewById(R.id.profileImage)


    }


    private val diffUtil = object : DiffUtil.ItemCallback<Chat>() {
        override fun areItemsTheSame(oldItem: Chat, newItem: Chat): Boolean {
            return oldItem == newItem
        }

        override fun areContentsTheSame(oldItem: Chat, newItem: Chat): Boolean {
            return oldItem == newItem
        }

    }

    private val recyclerListDiffer = AsyncListDiffer(this, diffUtil)

    var chats: List<Chat>
        get() = recyclerListDiffer.currentList
        set(value) = recyclerListDiffer.submitList(value)

    override fun getItemViewType(position: Int): Int {
        val chat = chats.get(position)

        if(chat.userEmail == FirebaseAuth.getInstance().currentUser?.email.toString()) { //chati gönderen giriş yapan kullanıcı ise

            return VIEW_TYPE_MESSAGE_SENT
        } else {
            return VIEW_TYPE_MESSAGE_RECEIVED
        }

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ChatHolder {

        if(viewType  == VIEW_TYPE_MESSAGE_RECEIVED) {

            val view = LayoutInflater.from(parent.context).inflate(R.layout.friend_message_item, parent, false)
            return ChatHolder(view)

        } else {
            val view = LayoutInflater.from(parent.context).inflate(R.layout.my_message_item, parent, false)
            return ChatHolder(view)

        }
    }


    override fun onBindViewHolder(holder: ChatHolder, position: Int) {
        val chat = chats[position]
        holder.message.text = "${chat.text}"
        Picasso.get().load(chats[position].downloadUrl).into(holder.profileImage)
    }


    override fun getItemCount(): Int {
        return chats.size
    }
}