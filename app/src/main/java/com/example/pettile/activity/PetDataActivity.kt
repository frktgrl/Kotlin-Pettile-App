package com.example.pettile.activity

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.pettile.adapter.PetRecyclerAdapter
import com.example.pettile.databinding.ActivityPetDataBinding
import com.example.pettile.model.Pet
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query

class PetDataActivity : AppCompatActivity() {

    private lateinit var auth: FirebaseAuth
    private lateinit var db: FirebaseFirestore
    private lateinit var binding: ActivityPetDataBinding

    val petArrayList: ArrayList<Pet> = ArrayList()
    var adapter: PetRecyclerAdapter? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding= ActivityPetDataBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)

        auth = FirebaseAuth.getInstance()
        db = FirebaseFirestore.getInstance()

        binding.petDataRecyclerView.layoutManager = LinearLayoutManager(applicationContext)

        adapter = PetRecyclerAdapter(petArrayList)
        binding.petDataRecyclerView.adapter = adapter

        getDataFromFirestoreUserPets()

    }
    fun getDataFromFirestoreUserPets() {

        db.collection("Pets")
            .whereEqualTo("userId", "${auth.currentUser?.uid}")
            .orderBy("date", Query.Direction.DESCENDING)
            .addSnapshotListener { snapshot, exception ->
                if (exception != null) {
                    Toast.makeText(applicationContext, exception.localizedMessage, Toast.LENGTH_LONG).show()
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
                                val userId = document.get("userId")as String // family alanını dizi olarak okuyun


                                val pet = Pet(name,genus,gender ,age,downloadUrl,petId,family,userId)
                                petArrayList.add(pet)

                            }
                            adapter!!.notifyDataSetChanged()

                        }
                    }

                }
            }


    }
    fun backImageClicked (view : View) {

        val intent = Intent(applicationContext, SaveActivity::class.java)
        startActivity(intent)
        finish()

    }
}