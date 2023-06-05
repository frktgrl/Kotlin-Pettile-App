package com.example.pettile.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.pettile.Interface.PetItemClickListener
import com.example.pettile.databinding.RecyclerRowRequestMypetBinding
import com.example.pettile.fragment.PetFamilyRequestFragment
import com.example.pettile.model.PetRequest
import com.squareup.picasso.Picasso



class PetRequestMyPetAdapter(

    private val fragment: PetFamilyRequestFragment?,
    private val petList: ArrayList<PetRequest>,
    private val petItemClickListener: PetItemClickListener?
) : RecyclerView.Adapter<PetRequestMyPetAdapter.PostHolder>() {

    class PostHolder(val binding: RecyclerRowRequestMypetBinding) : RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PostHolder {
        val binding = RecyclerRowRequestMypetBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return PostHolder(binding)
    }

    override fun getItemCount(): Int {
        return petList.size
    }

    override fun onBindViewHolder(holder: PostHolder, position: Int) {
        val pet = petList[position]
        with(holder.binding) {
            nameText.text = pet.name
            Picasso.get().load(pet.downloadUrl).into(petImage)

        }

        holder.binding.saveButton.setOnClickListener {
            val name = pet.name
            val downloadUrl = pet.downloadUrl
            petItemClickListener?.onPetItemClick(name, downloadUrl)
        }
    }



}
