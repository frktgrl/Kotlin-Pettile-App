package com.example.apppettileapp.fragment

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.apppettileapp.adapter.PetRecyclerAdapter
import com.example.apppettileapp.databinding.FragmentProfilePetBinding
import com.example.apppettileapp.model.Pet
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query


class ProfilePetFragment : Fragment() {

    private lateinit var auth: FirebaseAuth
    private lateinit var db: FirebaseFirestore
    private lateinit var binding: FragmentProfilePetBinding

    val petArrayList: ArrayList<Pet> = ArrayList()
    var adapter: PetRecyclerAdapter? = null


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentProfilePetBinding.inflate(layoutInflater)
        val view = binding.root

        auth = FirebaseAuth.getInstance()
        db = FirebaseFirestore.getInstance()

        binding.petRecyclerView.layoutManager = LinearLayoutManager(activity)

        adapter = PetRecyclerAdapter(petArrayList)
        binding.petRecyclerView.adapter = adapter

        getDataFromFirestoreUserPets()


        return view
    }

    fun getDataFromFirestoreUserPets() {

        db.collection("Pets")
            .whereEqualTo("userEmail", "${auth.currentUser?.email.toString()}")
            .orderBy("date",Query.Direction.DESCENDING)
            .addSnapshotListener { snapshot, exception ->
                if (exception != null) {
                    Toast.makeText(context, exception.localizedMessage, Toast.LENGTH_LONG).show()
                } else {

                    if (snapshot != null) {
                        if (!snapshot.isEmpty) {

                            petArrayList.clear()

                            val documents = snapshot.documents
                            for (document in documents) {
                                val name = document.get("name") as String
                                val genus = document.get("genus") as String
                                val gender = document.get("gender") as String
                                val age = document.get("age") as String
                                val downloadUrl = document.get("downloadUrl") as String


                                println(name)
                                println(genus)
                                println(gender)
                                println(age)

                                val pet = Pet(name,genus,gender ,age,downloadUrl)
                                petArrayList.add(pet)


                            }
                            adapter!!.notifyDataSetChanged()

                        }
                    }

                }
            }


    }
}