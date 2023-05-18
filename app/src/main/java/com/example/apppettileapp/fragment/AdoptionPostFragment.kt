package com.example.apppettileapp.fragment

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.apppettileapp.adapter.AdoptionRecyclerAdapter
import com.example.apppettileapp.databinding.FragmentAdoptionPostBinding
import com.example.apppettileapp.model.AdoptionPost
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query


class AdoptionPostFragment : Fragment() {

    private lateinit var binding: FragmentAdoptionPostBinding
    private lateinit var auth : FirebaseAuth
    private lateinit var db : FirebaseFirestore

    val postArrayList : ArrayList<AdoptionPost> = ArrayList()
    var adapter : AdoptionRecyclerAdapter? = null


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding= FragmentAdoptionPostBinding.inflate(layoutInflater)
        val view = binding.root


        //yanyana cardview için gridlayoutmanager kullan
        binding.adoptionRecyclerView.layoutManager = LinearLayoutManager(activity)

        adapter = AdoptionRecyclerAdapter(postArrayList)
        binding.adoptionRecyclerView.adapter = adapter


        auth = FirebaseAuth.getInstance()
        db = FirebaseFirestore.getInstance()

        getDataFromFirestore()


        return view
    }
    fun getDataFromFirestore() {

        db.collection("AdoptionPosts").orderBy("date",
            Query.Direction.DESCENDING).addSnapshotListener { snapshot, exception ->
            if (exception != null) {
                Toast.makeText(context,exception.localizedMessage, Toast.LENGTH_LONG).show()
            } else {

                if (snapshot != null) {
                    if (!snapshot.isEmpty) {

                        postArrayList.clear()

                        val documents = snapshot.documents
                        for (document in documents) {
                            val title = document.get("title") as String
                            val name = document.get("name") as String
                            val location = document.get("location") as String
                            val downloadUrl = document.get("downloadUrl") as String
                            val userId = document.get("userId") as String
                            val like = document.get("like") as List<String> // like alanını dizi olarak okuyun
                            val favorite = document.get("favorite") as List<String> // recomment alanını dizi olarak okuyun

                            val post = AdoptionPost(downloadUrl,title,name,location, userId, like, favorite)
                            postArrayList.add(post)
                        }
                        adapter!!.notifyDataSetChanged()

                    }
                }

            }
        }

    }

}