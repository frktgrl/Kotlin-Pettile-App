package com.example.pettile.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.pettile.databinding.RecyclerRowPetFamilyBinding
import com.example.pettile.model.PetFamily
import com.squareup.picasso.Picasso
import de.hdodenhof.circleimageview.CircleImageView

class PetFamilyViewAdapter(private val petFamilyList: ArrayList<PetFamily>) : RecyclerView.Adapter<PetFamilyViewAdapter.PostHolder>() {

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

            // Clear existing image views
            familyContainer.removeAllViews()



            for (imageUrl in pet.family) {
                val imageView = CircleImageView(familyContainer.context)
                val layoutParams = ViewGroup.MarginLayoutParams(150, 150)
                layoutParams.setMargins(50, 10, 10, 10) // Yukarıdan ve aşağıdan 10dp boşluk bırakılıyor
                imageView.layoutParams = layoutParams
                Picasso.get().load(imageUrl).into(imageView)
                familyContainer.addView(imageView)

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
