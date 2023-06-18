package com.example.pettile.activity

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.pettile.adapter.PetFamilyRecyclerAdapter
import com.example.pettile.databinding.ActivityFamilyDataBinding
import com.example.pettile.model.PetFamily
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query

class FamilyDataActivity : AppCompatActivity() {

    private lateinit var binding: ActivityFamilyDataBinding
    private lateinit var auth: FirebaseAuth
    private lateinit var db: FirebaseFirestore

    val petFamilyList: ArrayList<PetFamily> = ArrayList()
    var adapter: PetFamilyRecyclerAdapter? = null


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding= ActivityFamilyDataBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)

        auth = FirebaseAuth.getInstance()
        db = FirebaseFirestore.getInstance()


        binding.familyDataRecyclerView.layoutManager = LinearLayoutManager(applicationContext)

        adapter = PetFamilyRecyclerAdapter(petFamilyList)
        binding.familyDataRecyclerView.adapter = adapter

        getDataFromFirestorePetsFamily()
    }

    fun getDataFromFirestorePetsFamily() {
        db.collection("Pets")
            .whereEqualTo("userId", auth.currentUser?.uid)
            .orderBy("date", Query.Direction.DESCENDING)
            .addSnapshotListener { snapshot, exception ->
                if (exception != null) {
                   // Toast.makeText(applicationContext, exception.localizedMessage, Toast.LENGTH_LONG).show()
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

    fun backImageClicked (view : View) {

        val intent = Intent(applicationContext, SaveActivity::class.java)
        startActivity(intent)
        finish()

    }

}