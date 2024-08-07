package com.example.pettile.fragment

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.pettile.adapter.PetFamilyRecyclerAdapter
import com.example.pettile.databinding.FragmentProfileFamilyBinding
import com.example.pettile.model.PetFamily
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query


class ProfileFamilyFragment : Fragment() {

    private lateinit var binding: FragmentProfileFamilyBinding
    private lateinit var auth: FirebaseAuth
    private lateinit var db: FirebaseFirestore

    val petFamilyList: ArrayList<PetFamily> = ArrayList()
    var adapter: PetFamilyRecyclerAdapter? = null



    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentProfileFamilyBinding.inflate(layoutInflater)
        val view = binding.root

        auth = FirebaseAuth.getInstance()
        db = FirebaseFirestore.getInstance()


        binding.petFamilyRecyclerView.layoutManager = LinearLayoutManager(activity)

        adapter = PetFamilyRecyclerAdapter(petFamilyList)
        binding.petFamilyRecyclerView.adapter = adapter

        getDataFromFirestorePetsFamily()

        return view
    }


    fun getDataFromFirestorePetsFamily() {
        db.collection("Pets")
            .whereEqualTo("userId", auth.currentUser?.uid)
            .orderBy("date", Query.Direction.DESCENDING)
            .addSnapshotListener { snapshot, exception ->
                if (exception != null) {
                   // Toast.makeText(context, exception.localizedMessage, Toast.LENGTH_LONG).show()
                } else {
                    if (snapshot != null && !snapshot.isEmpty) {
                        petFamilyList.clear()

                        val documents = snapshot.documents
                        for (document in documents) {
                            val downloadUrl = document.get ("downloadUrl") as String
                            val name = document.get("name") as String
                            val family = document.get("family") as List<String>

                            val petFamily = PetFamily(downloadUrl,name,family)
                            petFamilyList.add(petFamily)


                        }

                        adapter!!.notifyDataSetChanged()
                    }
                }
            }
    }



}