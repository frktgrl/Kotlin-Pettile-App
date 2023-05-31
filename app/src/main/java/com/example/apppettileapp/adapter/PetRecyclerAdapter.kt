package com.example.apppettileapp.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.apppettileapp.databinding.RecyclerRowPetBinding
import com.example.apppettileapp.model.Pet
import com.google.firebase.firestore.FirebaseFirestore
import com.squareup.picasso.Picasso

class PetRecyclerAdapter(private val petList: ArrayList<Pet>) : RecyclerView.Adapter<PetRecyclerAdapter.PostHolder>() {

    private lateinit var db: FirebaseFirestore

    class PostHolder(val binding: RecyclerRowPetBinding) : RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PostHolder {
        val binding =
            RecyclerRowPetBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return PostHolder(binding)
    }

    override fun getItemCount(): Int {
        return petList.size
    }

    override fun onBindViewHolder(holder: PostHolder, position: Int) {
        val pet = petList[position]
        with(holder.binding) {
            petNameText.text = pet.name
            genusEditText.text = pet.genus
            genderEditText.text = pet.gender
            ageEditText.text = pet.age
            Picasso.get().load(pet.downloadUrl).into(petImage)


        }

        db = FirebaseFirestore.getInstance()

        holder.binding.deleteButton.setOnClickListener {

            println("basıldı")

            val petId = petList[position].petId

            // Firebase Firestore işlemleri
            val db = FirebaseFirestore.getInstance()
            val postsRef = db.collection("Pets")

            postsRef.whereEqualTo("petId", petId)
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
