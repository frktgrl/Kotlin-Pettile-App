package com.example.pettile.fragment

import android.content.ContentValues.TAG
import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.navigation.Navigation
import androidx.viewpager2.widget.ViewPager2
import com.example.pettile.R
import com.example.pettile.activity.FeedActivity
import com.example.pettile.activity.PostCreateActivity
import com.example.pettile.adapter.ProfileViewPagerAdapter
import com.example.pettile.databinding.FragmentProfileViewBinding
import com.example.pettile.model.Pet
import com.google.android.material.tabs.TabLayout
import com.google.android.material.tabs.TabLayoutMediator
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import com.squareup.picasso.Picasso


class ProfileViewFragment : Fragment() {

    private lateinit var auth: FirebaseAuth
    private lateinit var db: FirebaseFirestore
    private lateinit var binding: FragmentProfileViewBinding
    private lateinit var viewPager: ViewPager2
    private lateinit var tabLayout: TabLayout
    private val followersList = ArrayList<HashMap<String, String>>()


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

        auth = FirebaseAuth.getInstance()
        db = FirebaseFirestore.getInstance()

        getDataFromFirestoreUserPets()

        // Bundle'dan verileri alma
        val userEmail = arguments?.getString("userEmail")
        val username = arguments?.getString("username")
        val name = arguments?.getString("name")
        val downloadUrl = arguments?.getString("downloadUrl")
        val userId = arguments?.getString("userId")
        val followers = arguments?.getString("followers")
        val following = arguments?.getString("following")
        val whichfragment = arguments?.getString("whichfragment")


        binding.backImage.setOnClickListener {

            if (whichfragment == "homefragment") {

                val action = ProfileViewFragmentDirections.actionProfileViewFragmentToHomeFragment()
                Navigation.findNavController(view).navigate(action)
            }

            if (whichfragment == "searchfragment") {

                val action =
                    ProfileViewFragmentDirections.actionProfileViewFragmentToSearchFragment()
                Navigation.findNavController(view).navigate(action)
            }

            if (whichfragment == "adoptionfragment") {

                val action =
                    ProfileViewFragmentDirections.actionProfileViewFragmentToAdoptionFragment()
                Navigation.findNavController(view).navigate(action)
            }

        }

        //Diğer Fragmentlarına veri dağıtma
        viewPager = binding.viewPager
        tabLayout = binding.tabLayout

        val viewadapter = ProfileViewPagerAdapter(this)
        viewadapter.addFragment(
            ProfilePostViewFragment.newInstanceForPostView(
                name,
                username,
                userId
            ), "Posts"
        )
        viewadapter.addFragment(
            ProfilePetViewFragment.newInstanceForPetView(
                name,
                username,
                userId
            ), "Pets"
        )
        viewPager.adapter = viewadapter

        // View pager ve Tablayout için
        TabLayoutMediator(tabLayout, viewPager) { tab, position ->
            val tabText = when (position) {
                0 -> "Posts"
                1 -> "Pets"
                2 -> "Family"
                else -> "Posts"
            }
            tab.text = tabText
        }.attach()


        println(userEmail)
        println(username)
        println(name)
        println(followers)
        println(following)

        // UI bileşenlerine verileri atama
        binding.usernameText.text = username
        binding.nameText.text = name
        Picasso.get().load(downloadUrl).into(binding.profileImage)

        //Mesaj gönderme ekranına giderken kullanıcı bilgilerini gönder
        binding.messageButton.setOnClickListener {

            val whichfragment = "profileview"
            val action = ProfileViewFragmentDirections.actionProfileViewFragmentToChatFragment(
                userEmail = userEmail.toString(),
                name = name.toString(),
                username = username.toString(),
                downloadUrl = downloadUrl.toString(),
                userId = userId.toString(),
                followers = followers.toString(),
                following = following.toString(),
                whichfragment = whichfragment

            )
            Navigation.findNavController(view).navigate(action)
        }

        // Following işlemleri için ( karşılıklı users dizilerine verileri ekle)
        // Kullanıcının kullanıcı id kullanarak kullanıcı belgesini sorgula
        val collectionRef = db.collection("Users")
        val query = collectionRef.whereEqualTo("userId", "${auth.currentUser?.uid}").limit(1)

        query.get().addOnSuccessListener { querySnapshot ->
            if (!querySnapshot.isEmpty) {
                val documentSnapshot = querySnapshot.documents[0]
                val documentId = documentSnapshot.id // documentId değişkenine değeri atandı
                // Belge ID'sine ulaşabilirsiniz
                Log.d(TAG, "Kullanıcı belge ID'si: $documentId")
                val id1 = documentId

                val queryFollowers = collectionRef.whereEqualTo("userId", "${userId}").limit(1)

                queryFollowers.get().addOnSuccessListener { querySnapshot ->
                    if (!querySnapshot.isEmpty) {
                        val documentSnapshot = querySnapshot.documents[0]
                        val documentIdFollowers =
                            documentSnapshot.id // documentId değişkenine değeri atandı
                        // Belge ID'sine ulaşabilirsiniz
                        Log.d(TAG, "Kullanıcı belge ID'si: $documentIdFollowers")
                        val id2 = documentIdFollowers


                        // Takipçi kullanıcının followeing alanına takip edilen kullanıcının bilgilerini ekleyen kod
                        binding.addUserImage.setOnClickListener {
                            val requesterUserMail = userEmail.toString()
                            val targetUserMail = auth.currentUser?.email.toString()

                            db.collection("Users").document(id1)
                                .update(
                                    "following",
                                    FieldValue.arrayUnion(
                                        hashMapOf(
                                            "userId" to id2,
                                            "username" to requesterUserMail
                                        )
                                    )
                                )
                                .addOnSuccessListener {
                                    Log.d(TAG, "Takipçi kullanıcının following alanı güncellendi.")

                                    binding.addUserImage.setBackgroundResource(R.drawable.user_checked)

                                    // Takip edilen kullanıcının followers alanına takipçi kullanıcının bilgilerini ekleyen kod
                                    db.collection("Users").document(id2)
                                        .update(
                                            "followers",
                                            FieldValue.arrayUnion(
                                                hashMapOf(
                                                    "userId" to id1,
                                                    "username" to targetUserMail
                                                )
                                            )
                                        )
                                        .addOnSuccessListener {
                                            Toast.makeText(
                                                requireContext(),
                                                "Followed Successful!",
                                                Toast.LENGTH_LONG
                                            ).show()
                                            Log.d(
                                                TAG,
                                                "Takip edilen kullanıcının following alanı güncellendi."

                                            )
                                        }
                                        .addOnFailureListener { exception ->
                                            Log.e(
                                                TAG,
                                                "Takip edilen kullanıcının following alanını güncellerken hata meydana geldi: ${exception.localizedMessage}"
                                            )
                                        }
                                }
                                .addOnFailureListener { exception ->
                                    Log.e(
                                        TAG,
                                        "Takipçi kullanıcının followers alanını güncellerken hata meydana geldi: ${exception.localizedMessage}"
                                    )
                                }
                        }
                    } else {
                        Log.d(TAG, "Belirtilen kullanıcı bulunamadı.")
                    }

                }.addOnFailureListener { exception ->
                    Log.e(TAG, "Kullanıcı belge ID'sini alma hatası: ${exception.localizedMessage}")
                }


            }


        }

        // Takipçi sayısı gösterme
        val query2 = collectionRef.whereEqualTo("userId", "${userId}").limit(1)
        query2.get()
            .addOnSuccessListener { querySnapshot ->
                if (!querySnapshot.isEmpty) {
                    val documentSnapshot = querySnapshot.documents[0]
                    val documentId = documentSnapshot.id
                    val followers =
                        documentSnapshot.get("followers") as ArrayList<HashMap<String, String>> // followers alanını dizi olarak al
                    val following =
                        documentSnapshot.get("following") as ArrayList<HashMap<String, String>> // following alanını dizi olarak al

                    val collectionRef = db.collection("Users")
                    val query =
                        collectionRef.whereEqualTo("userId", "${auth.currentUser?.uid}").limit(1)

                    query.get().addOnSuccessListener { querySnapshot ->
                        if (!querySnapshot.isEmpty) {
                            val documentSnapshot = querySnapshot.documents[0]
                            val documentId =
                                documentSnapshot.id // documentId değişkenine değeri atandı
                            // Belge ID'sine ulaşabilirsiniz
                            Log.d(TAG, "Kullanıcı belge ID'si: $documentId")
                            val id1 = documentId


                            if (followers != null && followers.any { it["userId"] == id1 }) {
                                binding.addUserImage.setBackgroundResource(R.drawable.user_checked)
                            } else {
                                binding.addUserImage.setBackgroundResource(R.drawable.baseline_person_add_24)
                            }

                            println(followers)
                            println(following)
                            // Takipçi ve takip ettikleri sayısını TextView bileşenine aktar
                            binding.followersNumberText.text = "${followers.size}"
                            binding.followingNumberText.text = "${following.size}"

                        } else {
                            Log.d(TAG, "Belirtilen kullanıcı bulunamadı.")
                        }
                    }

                }
            }
        return view
    }


    companion object {
        fun newInstanceForPostView(
            name: String?,
            username: String?,
            userId: String?
        ): ProfilePostViewFragment {
            val fragment = ProfilePostViewFragment()
            val args = Bundle()
            args.putString("name", name)
            args.putString("username", username)
            args.putString("userId", userId)
            fragment.arguments = args
            return fragment
        }

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

    fun getDataFromFirestoreUserPets() {

        val userId = arguments?.getString("userId")

        db.collection("Pets")
            .whereEqualTo("userId", userId)
            .orderBy("date", Query.Direction.DESCENDING)
            .addSnapshotListener { snapshot, exception ->
                if (exception != null) {
                   // Toast.makeText(context, exception.localizedMessage, Toast.LENGTH_LONG).show()
                } else {
                    if (snapshot != null) {

                        binding.petNumberText.text =
                            snapshot.size().toString() // Snapshot'tan pet sayısını alın
                    }
                }
            }
    }

}



