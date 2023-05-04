package com.example.apppettileapp.fragment

import android.content.ContentValues.TAG
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.Navigation
import androidx.viewpager2.widget.ViewPager2
import com.example.apppettileapp.R
import com.example.apppettileapp.adapter.ProfileViewPagerAdapter
import com.example.apppettileapp.databinding.FragmentProfileViewBinding
import com.google.android.material.tabs.TabLayout
import com.google.android.material.tabs.TabLayoutMediator
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FieldValue
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
            val tabText = when (position) {
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
        val userId = arguments?.getString("userId")
        val followers = arguments?.getString("followers")
        val following = arguments?.getString("following")

        println(userEmail)
        println(username)
        println(name)
        println(followers)
        println(following)

        // UI bileşenlerine verileri atama
        binding.usernameText.text = userEmail
        binding.nameText.text = username
        binding.biographyText.text = name
        Picasso.get().load(downloadUrl).into(binding.profileImage)

        //Mesaj gönderme ekranına giderken kullanıcı bilgilerini gönder
        binding.messageButton.setOnClickListener {
            val action = ProfileViewFragmentDirections.actionProfileViewFragmentToChatFragment(
                userEmail = userEmail.toString(),
                name = name.toString(),
                username = username.toString(),
                downloadUrl = downloadUrl.toString(),
                userId = userId.toString()

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


                        // Takipçi kullanıcının followers alanına takip edilen kullanıcının bilgilerini ekleyen kod
                        binding.addUserImage.setOnClickListener {
                            val requesterUserMail = userEmail.toString()
                            val targetUserMail = auth.currentUser?.email.toString()

                            db.collection("Users").document(id1)
                                .update(
                                    "followers",
                                    FieldValue.arrayUnion(
                                        hashMapOf(
                                            "userId" to id2,
                                            "username" to requesterUserMail
                                        )
                                    )
                                )
                                .addOnSuccessListener {
                                    Log.d(TAG, "Takipçi kullanıcının followers alanı güncellendi.")

                                    // Takip edilen kullanıcının following alanına takipçi kullanıcının bilgilerini ekleyen kod
                                    db.collection("Users").document(id2)
                                        .update(
                                            "following",
                                            FieldValue.arrayUnion(
                                                hashMapOf(
                                                    "userId" to id1,
                                                    "username" to targetUserMail
                                                )
                                            )
                                        )
                                        .addOnSuccessListener {
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
                    val followers = documentSnapshot.get("followers") as ArrayList<HashMap<String, String>> // followers alanını dizi olarak al
                    val following = documentSnapshot.get("following") as ArrayList<HashMap<String, String>> // following alanını dizi olarak al

                    println(followers)
                    println(following)
                    // Takipçi ve takip ettikleri sayısını TextView bileşenine aktar
                    binding.followersNumberText.text = "${followers.size}"
                    binding.followingNumberText.text = "${following.size}"

                } else {
                    Log.d(TAG, "Belirtilen kullanıcı bulunamadı.")
                }
            }


        return view
    }






}