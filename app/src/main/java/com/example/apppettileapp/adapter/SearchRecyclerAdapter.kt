package com.example.apppettileapp.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.navigation.Navigation
import androidx.recyclerview.widget.RecyclerView
import com.example.apppettileapp.databinding.RecyclerRowSearchBinding
import com.example.apppettileapp.fragment.SearchFragmentDirections
import com.example.apppettileapp.model.UserSearch
import com.google.firebase.auth.FirebaseAuth
import com.squareup.picasso.Picasso

class SearchRecyclerAdapter(private val userArrayList : ArrayList<UserSearch>) : RecyclerView.Adapter<SearchRecyclerAdapter.PostHolder>() {

    private lateinit var auth: FirebaseAuth

    class PostHolder(val binding: RecyclerRowSearchBinding) :
        RecyclerView.ViewHolder(binding.root) {


    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PostHolder {
        val binding = RecyclerRowSearchBinding.inflate(LayoutInflater.from(parent.context), parent, false)

        return PostHolder(binding)
    }

    override fun getItemCount(): Int {
        return userArrayList.size

    }

    override fun onBindViewHolder(holder: PostHolder, position: Int) {


        auth = FirebaseAuth.getInstance()


        holder.binding.userNameText.text = userArrayList[position].username
        Picasso.get().load(userArrayList[position].downloadUrl).into(holder.binding.userPhotoView)

        //Profile Fragmenta Veri Gönderme
        val action = SearchFragmentDirections.actionSearchFragmentToProfileViewFragment(
            name = userArrayList[position].name,
            username = userArrayList[position].username,
            downloadUrl = userArrayList[position].downloadUrl,
            userId = userArrayList[position].userId,
            followers = userArrayList[position].followers,
            following = userArrayList[position].following,

        )
        if (userArrayList[position].userId != "${auth.currentUser?.uid}") {

            holder.binding.cardView.setOnClickListener {
                Navigation.findNavController(holder.itemView).navigate(action)
            }



        }else {

            val action = SearchFragmentDirections.actionSearchFragmentToProfileFragment()

            holder.binding.cardView.setOnClickListener {
                Navigation.findNavController(holder.itemView).navigate(action)
            }

        }
    }



}