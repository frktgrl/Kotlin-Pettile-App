package com.example.apppettileapp.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.navigation.Navigation
import androidx.recyclerview.widget.RecyclerView
import com.example.apppettileapp.databinding.RecyclerRowHomeBinding
import com.example.apppettileapp.fragment.HomeFragmentDirections
import com.example.apppettileapp.fragment.SearchFragmentDirections
import com.example.apppettileapp.model.Post
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.squareup.picasso.Picasso


class FeedRecyclerAdapter(private val postList : ArrayList<Post>) : RecyclerView.Adapter<FeedRecyclerAdapter.PostHolder>() {

    private lateinit var auth: FirebaseAuth
    private lateinit var db: FirebaseFirestore

    class PostHolder(val binding: RecyclerRowHomeBinding) : RecyclerView.ViewHolder(binding.root) {

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PostHolder {
        val binding =
            RecyclerRowHomeBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return PostHolder(binding)
    }

    override fun getItemCount(): Int {
        return postList.size

    }

    override fun onBindViewHolder(holder: PostHolder, position: Int) {
        //  holder.binding.recyclerEmailText.text = postList.get(position).email
        holder.binding.recyclerCommentText.text = postList.get(position).comment

        Picasso.get().load(postList[position].downloadUrl).into(holder.binding.recyclerImageView)

        auth = FirebaseAuth.getInstance()
        db = FirebaseFirestore.getInstance()

        //postta username ve profil foto için
        db.collection("Users")
            .get()
            .addOnSuccessListener { documents ->
                for (document in documents) {

                    val userEmail = document.get("userEmail") as String

                    if (userEmail == postList.get(position).email) {

                        val username = document.get("username") as String
                        val downloadUrl = document.get("downloadUrl") as String
                        val name = document.get("name") as String
                        holder.binding.recyclerEmailText.text = username
                        Picasso.get().load(downloadUrl).into(holder.binding.profileImage)

                        println(username)
                        println(downloadUrl)
                        println(userEmail)


                        val action = HomeFragmentDirections.actionHomeFragmentToProfileViewFragment(
                            userEmail = userEmail,
                            name = name,
                            username = username,
                            downloadUrl = downloadUrl

                        )

                        //Kullanıcı kendisi ise profil sayfasına değilse görüntülemeye
                        if (userEmail != "${auth.currentUser?.email.toString()}") {

                            holder.binding.profileImage.setOnClickListener {
                                Navigation.findNavController(holder.itemView).navigate(action)
                            }
                            holder.binding.recyclerEmailText.setOnClickListener {
                                Navigation.findNavController(holder.itemView).navigate(action)
                            }


                        }else {

                            val action = HomeFragmentDirections.actionHomeFragmentToProfileFragment()

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


    }

}


