package com.example.apppettileapp.fragment

import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.viewpager2.widget.ViewPager2
import com.example.apppettileapp.activity.PetAddActivity
import com.example.apppettileapp.activity.ProfileUpdateActivity
import com.example.apppettileapp.activity.SaveActivity
import com.example.apppettileapp.adapter.MyPagerAdapter
import com.example.apppettileapp.databinding.FragmentProfileBinding
import com.google.android.material.tabs.TabLayout
import com.google.android.material.tabs.TabLayoutMediator
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.squareup.picasso.Picasso


class ProfileFragment : Fragment() {


    private lateinit var auth: FirebaseAuth
    private lateinit var db: FirebaseFirestore
    private lateinit var binding: FragmentProfileBinding
    private lateinit var viewPager: ViewPager2
    private lateinit var tabLayout: TabLayout



    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        binding = FragmentProfileBinding.inflate(layoutInflater)
        val view = binding.root

        // View pager ve Tablayout için

        viewPager = binding.viewPager
        tabLayout = binding.tabLayout

        val viewadapter = MyPagerAdapter(this)
        viewPager.adapter = viewadapter

        TabLayoutMediator(tabLayout, viewPager) { tab, position ->
            val tabText = when(position) {
                0 -> "Posts"
                1 -> "Pets"
                2 -> "Family"
                else -> "Posts"
            }
            tab.text = tabText
        }.attach()

        // View pager ve Tablayout için


        auth = FirebaseAuth.getInstance()
        db = FirebaseFirestore.getInstance()


        getDataFromFirestoreUser()
        editProfileButtonClicked(view)
        petAddButtonClicked(view)
        myActivityButtonClicked(view)

        return view
    }

    fun getDataFromFirestoreUser() {

        db.collection("Users")
            .whereEqualTo("userId", "${auth.currentUser?.uid}")
            .addSnapshotListener { snapshot, exception ->
            if (exception != null) {
                Toast.makeText(requireContext(), exception.localizedMessage, Toast.LENGTH_LONG).show()
            } else {

                if (snapshot != null) {
                    if (!snapshot.isEmpty) {


                        val documents = snapshot.documents
                        for (document in documents) {
                            val name = document.get("name") as String
                            val username = document.get("username") as String
                            val biography = document.get("biography") as String
                            val downloadUrl = document.get("downloadUrl") as String
                            val userId = document.get("userId") as String
                            //val timestamp = document.get("date") as Timestamp
                            //val date = timestamp.toDate()

                            println(userId)
                            println(name)
                            println(username)
                            println(biography)

                            binding.nameText.text = name
                            binding.usernameText.text = username
                            binding.biographyText.text = biography
                            Picasso.get().load(downloadUrl).into(binding.profileImage)


                        }

                    }
                }

            }
        }

    }

    fun editProfileButtonClicked(view: View) {

        binding.editProfileButton.setOnClickListener {
            val intent = Intent(activity, ProfileUpdateActivity::class.java)
            startActivity(intent)
        }
    }
    fun petAddButtonClicked (view: View) {

        binding.addPetImage.setOnClickListener {
            val intent = Intent(activity, PetAddActivity::class.java)
            startActivity(intent)
        }
    }
    fun myActivityButtonClicked (view: View) {

        binding.myActivityButton.setOnClickListener {
            val intent = Intent(activity, SaveActivity::class.java)
            startActivity(intent)
        }
    }


}



