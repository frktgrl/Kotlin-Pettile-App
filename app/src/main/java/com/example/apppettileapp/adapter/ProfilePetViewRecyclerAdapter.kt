package com.example.apppettileapp.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.navigation.Navigation
import androidx.recyclerview.widget.RecyclerView
import com.example.apppettileapp.databinding.RecyclerRowPetViewBinding
import com.example.apppettileapp.fragment.ProfileViewFragmentDirections
import com.example.apppettileapp.model.Pet
import com.example.apppettileapp.model.Post
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.squareup.picasso.Picasso

class ProfilePetViewRecyclerAdapter (private val petArrayList : ArrayList<Pet>) : RecyclerView.Adapter<ProfilePetViewRecyclerAdapter.PostHolder>() {


    class PostHolder(val binding: RecyclerRowPetViewBinding) :
        RecyclerView.ViewHolder(binding.root) {

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PostHolder {
        val binding = RecyclerRowPetViewBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return PostHolder(binding)
    }

    override fun getItemCount(): Int {
        return petArrayList.size

    }

    override fun onBindViewHolder(holder: PostHolder, position: Int) {
        val pet = petArrayList[position]
        with(holder.binding) {
            petNameText.text = pet.name
            genusEditText.text = pet.genus
            genderEditText.text = pet.gender
            ageEditText.text = pet.age
            Picasso.get().load(pet.downloadUrl).into(petImage)


        }
        holder.binding.familyButton.setOnClickListener {

            val action =  ProfileViewFragmentDirections.actionProfileViewFragmentToPetFamilyRequestFragment(downloadUrl = petArrayList[position].downloadUrl, name = petArrayList[position].name, petId = petArrayList[position].petId)
            val navController = Navigation.findNavController(holder.binding.familyButton)
            navController.navigate(action)

        }


    }



}
