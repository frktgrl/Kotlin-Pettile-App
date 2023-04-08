package com.example.apppettileapp.fragment

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.viewpager2.widget.ViewPager2
import com.example.apppettileapp.adapter.MyPagerAdapter
import com.example.apppettileapp.adapter.ProfileViewPagerAdapter
import com.example.apppettileapp.databinding.FragmentProfileBinding
import com.example.apppettileapp.databinding.FragmentProfileViewBinding
import com.google.android.material.tabs.TabLayout
import com.google.android.material.tabs.TabLayoutMediator
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.squareup.picasso.Picasso


class ProfileViewFragment : Fragment() {

    private lateinit var auth: FirebaseAuth
    private lateinit var db: FirebaseFirestore
    private lateinit var binding: FragmentProfileViewBinding
    private lateinit var viewPager: ViewPager2
    private lateinit var tabLayout: TabLayout


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentProfileViewBinding.inflate(layoutInflater)
        val view = binding.root

        // View pager ve Tablayout için

        viewPager = binding.viewPager
        tabLayout = binding.tabLayout

        val viewadapter = ProfileViewPagerAdapter(this)
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

        // Bundle'dan verileri alma
        val userEmail = arguments?.getString("userEmail")
        val username = arguments?.getString("username")
        val name = arguments?.getString("name")
        val downloadUrl = arguments?.getString("downloadUrl")
        // Diğer verileri de bundle'dan alabilirsiniz

        println(userEmail)
        println(username)
        println(name)

        // UI bileşenlerine verileri atama
        binding.usernameText.text = userEmail
        binding.nameText.text = username
        binding.biographyText.text = name
        Picasso.get().load(downloadUrl).into(binding.profileImage)
        // Diğer bileşenlere de verileri atayabilirsiniz


//        // ProfileViewFragment'a geçişte putExtra() yöntemiyle eklenen "userEmail" verisini alma
//        val userEmail = arguments?.getString("userEmail")



        return view

    }
//    fun getDataFromFirestoreUser(userEmail: String) {
//        db.collection("Users")
//            .whereEqualTo("userEmail", userEmail)
//            .get()
//            .addOnSuccessListener { documents ->
//                for (document in documents) {
//                    val name = document.getString("name")
//                    val username = document.getString("username")
//                    val biography = document.getString("biography")
//                    val downloadUrl = document.getString("downloadUrl")
//
//                    binding.nameText.text = name
//                    binding.usernameText.text = username
//                    binding.biographyText.text = biography
//                    Picasso.get().load(downloadUrl).into(binding.profileImage)
//
//
//                    println(name)
//                    println(username)
//                    println(biography)
//
//                }
//            }
//            .addOnFailureListener { exception ->
//                Toast.makeText(context, exception.localizedMessage, Toast.LENGTH_LONG).show()
//            }
//    }
//
//


}