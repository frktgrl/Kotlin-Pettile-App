package com.example.apppettileapp.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.navigation.Navigation
import androidx.recyclerview.widget.RecyclerView
import com.example.apppettileapp.databinding.RecyclerRowPetBinding
import com.example.apppettileapp.fragment.ProfilePetFragmentDirections
import com.example.apppettileapp.fragment.ProfileViewFragmentDirections
import com.example.apppettileapp.model.Pet
import com.squareup.picasso.Picasso

class PetRecyclerAdapter(private val petList: ArrayList<Pet>) : RecyclerView.Adapter<PetRecyclerAdapter.PostHolder>() {

    class PostHolder(val binding: RecyclerRowPetBinding) : RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PostHolder {
        val binding = RecyclerRowPetBinding.inflate(LayoutInflater.from(parent.context), parent, false)
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
    }
}
