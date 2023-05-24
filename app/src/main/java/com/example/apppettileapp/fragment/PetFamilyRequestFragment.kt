package com.example.apppettileapp.fragment

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.apppettileapp.Interface.PetItemClickListener
import com.example.apppettileapp.adapter.PetRequestMyPetAdapter
import com.example.apppettileapp.databinding.FragmentPetFamilyRequestBinding
import com.example.apppettileapp.model.PetRequest
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.squareup.picasso.Picasso




class PetFamilyRequestFragment : Fragment(), PetItemClickListener {


    private lateinit var binding: FragmentPetFamilyRequestBinding
    private lateinit var auth: FirebaseAuth
    private lateinit var db: FirebaseFirestore

    val petArrayList : ArrayList<PetRequest> = ArrayList()
    var adapter : PetRequestMyPetAdapter? = null


    override fun onPetItemClick(name: String, downloadUrl: String) {
        binding.requestNameText.text = name
        Picasso.get().load(downloadUrl).into(binding.requestImage)
    }



    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        }


    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        binding = FragmentPetFamilyRequestBinding.inflate(layoutInflater)
        val view = binding.root


        binding.mypetRecyclerView.layoutManager = LinearLayoutManager(activity)

        adapter = PetRequestMyPetAdapter(this,petArrayList,this)
        binding.mypetRecyclerView.adapter = adapter


        auth = FirebaseAuth.getInstance()
        db = FirebaseFirestore.getInstance()


        getDataFromFirestoreUserPet()


        val downloadUrl = arguments?.getString("downloadUrl")
        val name = arguments?.getString("name")
        val petId = arguments?.getString("petId")
        Picasso.get().load(downloadUrl).into(binding.profileImage)
        binding.profileNameText.text = name



        return view
    }

    //İstek göndermek isteyen kişinin petlerini listele ve seçmesini sağla

    fun getDataFromFirestoreUserPet() {

        db.collection("Pets")
            .whereEqualTo("userId", "${auth.currentUser?.uid}")
            .addSnapshotListener { snapshot, exception ->
                if (exception != null) {
                    Toast.makeText(requireContext(), exception.localizedMessage, Toast.LENGTH_LONG).show()
                } else {

                    if (snapshot != null) {
                        if (!snapshot.isEmpty) {

                            petArrayList.clear()


                            val documents = snapshot.documents
                            for (document in documents) {
                                val name = document.get("name") as String
                                val downloadUrl = document.get("downloadUrl") as String
                                val petId = document.get("petId") as String

                                println(name)
                                val pet = PetRequest(downloadUrl,name,petId)
                                petArrayList.add(pet)

                            }
                            adapter!!.notifyDataSetChanged()

                        }
                    }

                }
            }

    }

}