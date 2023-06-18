package com.example.pettile.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.Button
import android.widget.LinearLayout
import androidx.recyclerview.widget.RecyclerView
import com.example.pettile.R
import com.example.pettile.databinding.RecyclerRowPetFamilyBinding
import com.example.pettile.model.PetFamily
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.squareup.picasso.Picasso
import de.hdodenhof.circleimageview.CircleImageView

class PetFamilyRecyclerAdapter(private val petFamilyList: ArrayList<PetFamily>) : RecyclerView.Adapter<PetFamilyRecyclerAdapter.PostHolder>() {

    private lateinit var auth: FirebaseAuth
    private lateinit var db: FirebaseFirestore

    class PostHolder(val binding: RecyclerRowPetFamilyBinding) : RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PostHolder {
        val binding = RecyclerRowPetFamilyBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return PostHolder(binding)
    }

    override fun getItemCount(): Int {
        return petFamilyList.size
    }

    override fun onBindViewHolder(holder: PostHolder, position: Int) {
        val pet = petFamilyList[position]

        with(holder.binding) {

            println(pet.name)
            println(pet.downloadUrl)
            petNameText.text = pet.name
            Picasso.get().load(pet.downloadUrl).into(petView)



            // Clear existing image views and buttons
            familyContainer.removeAllViews()

            for (imageUrl in pet.family) {
                val linearLayout = LinearLayout(familyContainer.context)
                linearLayout.orientation = LinearLayout.VERTICAL

                val imageView = CircleImageView(familyContainer.context)
                val layoutParams = ViewGroup.MarginLayoutParams(150, 150)
                layoutParams.setMargins(50, 10, 10, 10) // Yukarıdan ve aşağıdan 10dp boşluk bırakılıyor
                imageView.layoutParams = layoutParams
                Picasso.get().load(imageUrl).into(imageView)
                linearLayout.addView(imageView)

                val button = Button(familyContainer.context)

                auth = FirebaseAuth.getInstance()
                db = FirebaseFirestore.getInstance()


                //Butona basıldığında aileyi kaldır.

                val downloadUrl = petFamilyList[position].downloadUrl

                button.setOnClickListener {
                    val collectionRef = db.collection("Pets")
                    val query = collectionRef.whereEqualTo("downloadUrl", downloadUrl).limit(1)

                    query.get().addOnSuccessListener { querySnapshot ->
                        if (!querySnapshot.isEmpty) {
                            val documentSnapshot = querySnapshot.documents[0]

                            // "family" dizisinden veriyi silme
                            val family = documentSnapshot.get("family") as? ArrayList<String>

                            if (family != null && family.contains(imageUrl)) {
                                family.remove(imageUrl)

                                // Güncellenmiş veriyi Firestore'a kaydetme
                                documentSnapshot.reference.update("family", family)
                                    .addOnSuccessListener {
                                        // Eğer başarılı olursa gönderen kullanıcının dizisinden de sil
                                        val senderCollectionRef = db.collection("Pets")
                                        val senderQuery = senderCollectionRef.whereEqualTo("downloadUrl", imageUrl).limit(1)

                                        senderQuery.get().addOnSuccessListener { senderQuerySnapshot ->
                                            if (!senderQuerySnapshot.isEmpty) {
                                                val senderDocumentSnapshot = senderQuerySnapshot.documents[0]

                                                // "family" dizisinden veriyi silme
                                                val senderFamily = senderDocumentSnapshot.get("family") as? ArrayList<String>

                                                if (senderFamily != null && senderFamily.contains(downloadUrl!!)) {
                                                    senderFamily.remove(downloadUrl!!)

                                                    // Güncellenmiş veriyi Firestore'a kaydetme
                                                    senderDocumentSnapshot.reference.update("family", senderFamily)
                                                        .addOnSuccessListener {
                                                            // Veri başarıyla güncellendi
                                                        }
                                                        .addOnFailureListener { e ->
                                                            // Hata oluştu, veri güncellenemedi
                                                        }
                                                }
                                            }
                                        }
                                    }
                                    .addOnFailureListener { e ->
                                        // Hata oluştu, veri güncellenemedi
                                    }
                            }
                        }
                    }
                }

                val buttonLayoutParams = ViewGroup.MarginLayoutParams(
                    ViewGroup.LayoutParams.WRAP_CONTENT,
                    ViewGroup.LayoutParams.WRAP_CONTENT
                )
                buttonLayoutParams.setMargins(-20, 10, 0, 0) // Yukarıdan 10dp boşluk bırakılıyor
                button.layoutParams = buttonLayoutParams
                button.setBackgroundResource(R.drawable.baseline_clear_24) // Arka plan olarak ic_close kullanılıyor
                button.scaleX = 0.4f // X ekseninde boyutu küçültülüyor
                button.scaleY = 0.43f // Y ekseninde boyutu küçültülüyor
                linearLayout.addView(button)

                familyContainer.addView(linearLayout)

                if (familyContainer.childCount > 1) {
                    // İlk imageview'dan sonra sadece aralara boşluk ekleniyor
                    val marginLayoutParams = imageView.layoutParams as ViewGroup.MarginLayoutParams
                    marginLayoutParams.setMargins(50, 10, 10, 10) // 10dp boşluk bırakılıyor
                    imageView.layoutParams = marginLayoutParams
                }

            }

            }

        }

    }

