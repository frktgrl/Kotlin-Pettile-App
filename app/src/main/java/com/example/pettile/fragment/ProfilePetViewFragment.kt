package com.example.pettile.fragment

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.pettile.adapter.ProfilePetViewRecyclerAdapter
import com.example.pettile.databinding.FragmentProfilePetViewBinding
import com.example.pettile.model.Pet
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query


class ProfilePetViewFragment() : Fragment() {


    private lateinit var auth: FirebaseAuth
    private lateinit var db: FirebaseFirestore
    private lateinit var binding: FragmentProfilePetViewBinding

    val petArrayList: ArrayList<Pet> = ArrayList()
    var adapter: ProfilePetViewRecyclerAdapter? = null


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentProfilePetViewBinding.inflate(layoutInflater)
        val view = binding.root

        binding.profilePetViewRecyclerView.layoutManager = LinearLayoutManager(activity)

        adapter = ProfilePetViewRecyclerAdapter(petArrayList)
        binding.profilePetViewRecyclerView.adapter = adapter

        auth = FirebaseAuth.getInstance()
        db = FirebaseFirestore.getInstance()

        getDataFromFireStore()


        return view
    }


    private fun getDataFromFireStore() {
        db.collection("Pets")
            .whereEqualTo("userId", arguments?.getString("userId"))
            .orderBy("date", Query.Direction.DESCENDING)
            .addSnapshotListener { snapshot, exception ->
                if (exception != null) {
                    // Toast.makeText(context, exception.localizedMessage, Toast.LENGTH_LONG).show()
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
                                val petId = document.get("petId") as String
                                val family = document.get("family") as List<String>
                                val userId =
                                    document.get("userId") as String // family alanını dizi olarak okuyun


                                println(name)
                                println(genus)
                                println(gender)
                                println(age)

                                val pet = Pet(
                                    name,
                                    genus,
                                    gender,
                                    age,
                                    downloadUrl,
                                    petId,
                                    family,
                                    userId
                                )
                                petArrayList.add(pet)

                            }
                            adapter?.notifyDataSetChanged()
                        }
                    }
                }
            }

    }

    companion object {
        fun newInstanceForPetView(
            name: String?,
            username: String?,
            userId: String?
        ): ProfilePetViewFragment {
            val fragment = ProfilePetViewFragment()
            val args = Bundle()
            args.putString("name", name)
            args.putString("username", username)
            args.putString("userId", userId)
            fragment.arguments = args
            return fragment
        }
    }

}