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


            val collectionRef = db.collection("Posts")
            val query =
                collectionRef.whereEqualTo("downloadUrl", postList[position].downloadUrl).limit(1)

            query.get().addOnSuccessListener { querySnapshot ->
                if (!querySnapshot.isEmpty) {
                    val documentSnapshot = querySnapshot.documents[0]
                    val documentId = documentSnapshot.id

                    val currentUserId = auth.currentUser?.uid // Oturum açmış kullanıcının ID'sini al
                    val collectionRef = db.collection("Users")
                    val query = collectionRef.whereEqualTo("userId", "${auth.currentUser?.uid}").limit(1)

                    query.get().addOnSuccessListener { querySnapshot ->
                        if (!querySnapshot.isEmpty) {
                            val documentSnapshot = querySnapshot.documents[0]
                            val documentId2 =
                                documentSnapshot.id // documentId değişkenine değeri atandı
                            // Belge ID'sine ulaşabilirsiniz
                            Log.d(ContentValues.TAG, "Kullanıcı belge ID'si: $documentId")
                            val id1 = documentId2

                            //kullanıcının likepost alanına ekle
                            if (currentUserId != null) {
                                db.collection("Users").document(id1)
                                    .update(
                                        "likeposts",
                                        FieldValue.arrayUnion(
                                            hashMapOf(
                                                "postId" to documentId
                                            )
                                        )
                                    )
                                    .addOnSuccessListener {
                                        Log.d(
                                            ContentValues.TAG,
                                            "User like alanı güncellendi."
                                        )
                                        // Güncelleme başarılı olduğunda beğeni butonunun görüntüsünü güncelle
                                        holder.binding.likeButton.setImageResource(R.drawable.ic_likes_active)
                                    }
                                    .addOnFailureListener { exception ->
                                        // Güncelleme başarısız olduğunda hata mesajı göster
                                        Log.e(
                                            ContentValues.TAG,
                                            "User beğeni güncellenirken hata oluştu: ${exception.localizedMessage}"
                                        )
                                    }
                            }

                            db.collection("Posts").document(documentId)
                                .update(
                                    "like",
                                    FieldValue.arrayUnion(
                                        hashMapOf(
                                            "userId" to currentUserId
                                        )
                                    )
                                )
                                .addOnSuccessListener {
                                    Log.d(
                                        ContentValues.TAG,
                                        "Post like alanı güncellendi."
                                    )
                                    // Güncelleme başarılı olduğunda beğeni butonunun görüntüsünü güncelle
                                    holder.binding.likeButton.setImageResource(R.drawable.ic_likes_active)
                                }
                                .addOnFailureListener { exception ->
                                    // Güncelleme başarısız olduğunda hata mesajı göster
                                    Log.e(
                                        ContentValues.TAG,
                                        "Post beğeni güncellenirken hata oluştu: ${exception.localizedMessage}"
                                    )
                                }
                        }
                    }
                }

            }
        }
    }


}







