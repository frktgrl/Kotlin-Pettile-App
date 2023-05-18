package com.example.apppettileapp.adapter

import android.content.ContentValues
import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.navigation.Navigation
import androidx.recyclerview.widget.RecyclerView
import com.example.apppettileapp.databinding.RecyclerRowAdoptionPostBinding
import com.example.apppettileapp.fragment.AdoptionFragmentDirections
import com.example.apppettileapp.fragment.AdoptionPostFragmentDirections
import com.example.apppettileapp.model.AdoptionPost
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore
import com.squareup.picasso.Picasso

class AdoptionRecyclerAdapter (private val postArrayList : ArrayList<AdoptionPost>) : RecyclerView.Adapter<AdoptionRecyclerAdapter.PostHolder>() {

    private lateinit var auth: FirebaseAuth
    private lateinit var db: FirebaseFirestore

    class PostHolder(val binding: RecyclerRowAdoptionPostBinding) :
        RecyclerView.ViewHolder(binding.root) {

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PostHolder {
        val binding = RecyclerRowAdoptionPostBinding.inflate(
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
        holder.binding.titleText.text = postArrayList.get(position).title // başlık
        holder.binding.nameText.text = postArrayList.get(position).name // başlık
        holder.binding.locationText.text = postArrayList.get(position).location // başlık
        Picasso.get().load(postArrayList[position].downloadUrl).into(holder.binding.petView) //görsel


        auth = FirebaseAuth.getInstance()
        db = FirebaseFirestore.getInstance()

        // posttan profile gitmek için
        db.collection("Users")
            .get()
            .addOnSuccessListener { documents ->
                for (document in documents) {
                    val userId = document.get("userId") as String
                    if (userId == postArrayList[position].userId) {
                        val username = document.get("username") as String
                        val downloadUrl = document.get("downloadUrl") as String
                        val name = document.get("name") as String
                        val followers = document.get("followers").toString()
                        val following = document.get("following").toString()

                        val action = AdoptionFragmentDirections.actionAdoptionFragmentToProfileViewFragment(
                                name = name,
                                username = username,
                                downloadUrl = downloadUrl,
                                userId = userId,
                                followers = followers,
                                following = following
                            )

                        // Kullanıcı kendisi ise profil sayfasına değilse görüntülemeye
                        if (userId != "${auth.currentUser?.uid}") {
                            holder.binding.cardView.setOnClickListener {
                                Navigation.findNavController(holder.itemView).navigate(action)
                            }
                            holder.binding.petView.setOnClickListener {
                                Navigation.findNavController(holder.itemView).navigate(action)
                            }
                        } else {
                            val action =
                                AdoptionFragmentDirections.actionAdoptionFragmentToProfileFragment()
                            holder.binding.cardView.setOnClickListener {
                                Navigation.findNavController(holder.itemView).navigate(action)
                            }
                            holder.binding.petView.setOnClickListener {
                                Navigation.findNavController(holder.itemView).navigate(action)
                            }
                        }
                    }
                }
            }
            .addOnFailureListener { exception ->
                // hata durumu
            }



        holder.binding.saveButton.setOnClickListener {


            val collectionRef = db.collection("AdoptionPosts")
            val query = collectionRef.whereEqualTo("downloadUrl", postArrayList[position].downloadUrl).limit(1)

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
                            val documentId2 = documentSnapshot.id // documentId değişkenine değeri atandı
                            // Belge ID'sine ulaşabilirsiniz
                            Log.d(ContentValues.TAG, "Kullanıcı belge ID'si: $documentId")
                            val id1 = documentId2

                            //kullanıcının likepost alanına ekle
                            if (currentUserId != null) {
                                db.collection("Users").document(id1)
                                    .update(
                                        "favoriteposts",
                                        FieldValue.arrayUnion(
                                            hashMapOf(
                                                "postId" to documentId
                                            )
                                        )
                                    )
                                    .addOnSuccessListener {
                                        Log.d(
                                            ContentValues.TAG,
                                            "User favorite alanı güncellendi."
                                        )
                                        // Güncelleme başarılı olduğunda beğeni butonunun görüntüsünü güncelle
                                        // holder.binding.likeButton.setImageResource(R.drawable.ic_likes_active)
                                    }
                                    .addOnFailureListener { exception ->
                                        // Güncelleme başarısız olduğunda hata mesajı göster
                                        Log.e(
                                            ContentValues.TAG,
                                            "User favorite güncellenirken hata oluştu: ${exception.localizedMessage}"
                                        )
                                    }
                            }

                            db.collection("AdoptionPosts").document(documentId)
                                .update(
                                    "favorite",
                                    FieldValue.arrayUnion(
                                        hashMapOf(
                                            "userId" to currentUserId
                                        )
                                    )
                                )
                                .addOnSuccessListener {
                                    Log.d(
                                        ContentValues.TAG,
                                        "Post favorite alanı güncellendi."
                                    )
                                    // Güncelleme başarılı olduğunda beğeni butonunun görüntüsünü güncelle
                                    // holder.binding.likeButton.setImageResource(R.drawable.ic_likes_active)
                                }
                                .addOnFailureListener { exception ->
                                    // Güncelleme başarısız olduğunda hata mesajı göster
                                    Log.e(
                                        ContentValues.TAG,
                                        "Post favorite güncellenirken hata oluştu: ${exception.localizedMessage}"
                                    )
                                }
                        }
                    }
                }

            }
        }

    }


}