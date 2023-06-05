package com.example.pettile.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.pettile.databinding.RecyclerRowSharedAdoptionPostDataBinding
import com.example.pettile.model.AdoptionPost
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.squareup.picasso.Picasso


class SharedAdoptionPostRecyclerAdapter(private val postList: ArrayList<AdoptionPost>) :
    RecyclerView.Adapter<SharedAdoptionPostRecyclerAdapter.PostHolder>()  {

    private lateinit var auth: FirebaseAuth
    private lateinit var db: FirebaseFirestore



    class PostHolder(val binding: RecyclerRowSharedAdoptionPostDataBinding) : RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PostHolder {

        val binding = RecyclerRowSharedAdoptionPostDataBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return PostHolder(binding)
    }

    override fun getItemCount(): Int {
        return postList.size
    }

    override fun onBindViewHolder(holder: PostHolder, position: Int) {

        auth = FirebaseAuth.getInstance()
        db = FirebaseFirestore.getInstance()

        Picasso.get().load(postList[position].downloadUrl).into(holder.binding.likeView)

        holder.binding.deleteLayout.setOnClickListener {
            val adoptionPostId = postList[position].adoptionPostId

            // Firebase Firestore işlemleri
            val db = FirebaseFirestore.getInstance()
            val postsRef = db.collection("AdoptionPosts")

            postsRef.whereEqualTo("adoptionPostId", adoptionPostId)
                .get()
                .addOnSuccessListener { querySnapshot ->
                    for (document in querySnapshot.documents) {
                        val postRef = postsRef.document(document.id)

                        // Gönderiyi silme işlemi
                        postRef.delete()
                            .addOnSuccessListener {
                                // Silme işlemi başarılı
                                println("Post Deleted")
                            }
                            .addOnFailureListener { e ->
                                // Hata durumunda yapılacak işlemler
                            }
                    }
                }
                .addOnFailureListener { e ->
                    // Hata durumunda yapılacak işlemler
                }
        }


    }
}