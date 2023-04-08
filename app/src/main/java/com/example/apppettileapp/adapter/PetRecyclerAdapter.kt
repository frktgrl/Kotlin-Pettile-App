package com.example.apppettileapp.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.apppettileapp.databinding.RecyclerRowPetBinding
import com.example.apppettileapp.model.Pet
import com.squareup.picasso.Picasso

class PetRecyclerAdapter (private val petList : ArrayList<Pet>) : RecyclerView.Adapter<PetRecyclerAdapter.PostHolder>() {

    class PostHolder(val binding: RecyclerRowPetBinding) : RecyclerView.ViewHolder(binding.root) {

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PostHolder {
        val binding = RecyclerRowPetBinding.inflate(LayoutInflater.from(parent.context),parent,false)
        return PostHolder(binding)
    }

    override fun getItemCount(): Int {
        return petList.size

    }

    override fun onBindViewHolder(holder: PostHolder, position: Int) {
        holder.binding.petNameText.text = petList.get(position).name
        holder.binding.genusEditText.text = petList.get(position).genus
        holder.binding.genderEditText.text = petList.get(position).gender
        holder.binding.ageEditText.text = petList.get(position).age

        Picasso.get().load(petList[position].downloadUrl).into(holder.binding.petImage)
    }

}