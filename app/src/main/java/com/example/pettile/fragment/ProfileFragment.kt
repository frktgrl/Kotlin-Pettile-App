package com.example.pettile.fragment

import android.content.ContentValues
import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.viewpager2.widget.ViewPager2
import com.example.pettile.activity.PetAddActivity
import com.example.pettile.activity.ProfileUpdateActivity
import com.example.pettile.activity.SaveActivity
import com.example.pettile.adapter.MyPagerAdapter
import com.example.pettile.databinding.FragmentProfileBinding
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


        // Takipçi sayısı gösterme
        val collectionRef = db.collection("Users")
        val userId = auth.currentUser!!.uid

        val query2 = collectionRef.whereEqualTo("userId", userId).limit(1)
        query2.get()
            .addOnSuccessListener { querySnapshot ->
                if (!querySnapshot.isEmpty) {
                    val documentSnapshot = querySnapshot.documents[0]
                    val documentId = documentSnapshot.id
                    val followers = documentSnapshot.get("followers") as ArrayList<HashMap<String, String>> // followers alanını dizi olarak al
                    val following = documentSnapshot.get("following") as ArrayList<HashMap<String, String>> // following alanını dizi olarak al

                    println(followers)
                    println(following)
                    // Takipçi ve takip ettikleri sayısını TextView bileşenine aktar
                    binding.followersNumberText.text = "${followers.size}"
                    binding.followingNumberText.text = "${following.size}"

                } else {
                    Log.d(ContentValues.TAG, "Belirtilen kullanıcı bulunamadı.")
                }
            }


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



