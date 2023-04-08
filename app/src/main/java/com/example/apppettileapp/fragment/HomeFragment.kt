package com.example.apppettileapp.fragment

import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.apppettileapp.activity.AdoptionActivity
import com.example.apppettileapp.adapter.FeedRecyclerAdapter
import com.example.apppettileapp.databinding.FragmentHomeBinding
import com.example.apppettileapp.model.Post
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query


class HomeFragment : Fragment() {


    private lateinit var auth : FirebaseAuth
    private lateinit var db : FirebaseFirestore
    private lateinit var binding: FragmentHomeBinding

    val postArrayList : ArrayList<Post> = ArrayList()
    var adapter : FeedRecyclerAdapter? = null


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment

        binding = FragmentHomeBinding.inflate(layoutInflater)
        val view = binding.root


        binding.feedRecyclerView.layoutManager = LinearLayoutManager(activity)

        adapter = FeedRecyclerAdapter(postArrayList)
        binding.feedRecyclerView.adapter = adapter


        auth = FirebaseAuth.getInstance()
        db = FirebaseFirestore.getInstance()

        getDataFromFirestore()
        adoptionClicked (view)
        return view


    }


    fun getDataFromFirestore() {

        db.collection("Posts").orderBy("date",
            Query.Direction.DESCENDING).addSnapshotListener { snapshot, exception ->
            if (exception != null) {
                Toast.makeText(context,exception.localizedMessage, Toast.LENGTH_LONG).show()
            } else {

                if (snapshot != null) {
                    if (!snapshot.isEmpty) {

                        postArrayList.clear()

                        val documents = snapshot.documents
                        for (document in documents) {
                            val comment = document.get("comment") as String
                            val useremail = document.get("userEmail") as String
                            val downloadUrl = document.get("downloadUrl") as String
                            //val timestamp = document.get("date") as Timestamp
                            //val date = timestamp.toDate()

                            val post = Post(useremail,comment, downloadUrl)
                            postArrayList.add(post)
                        }
                        adapter!!.notifyDataSetChanged()

                    }
                }

            }
        }

    }

    fun adoptionClicked (view: View) {

        binding.adoptionImage.setOnClickListener {
            val intent = Intent(activity, AdoptionActivity::class.java)
            startActivity(intent)
        }
    }


}