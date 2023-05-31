package com.example.apppettileapp.fragment

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.apppettileapp.adapter.ChatViewRecyclerAdapter
import com.example.apppettileapp.databinding.FragmentChatViewBinding
import com.example.apppettileapp.model.ChatView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query


class ChatViewFragment : Fragment() {

    private lateinit var auth: FirebaseAuth
    private lateinit var db: FirebaseFirestore
    private lateinit var binding: FragmentChatViewBinding

    val chatArrayList: ArrayList<ChatView> = ArrayList()
    var adapter: ChatViewRecyclerAdapter? = null


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentChatViewBinding.inflate(layoutInflater)
        val view = binding.root

        binding.chatViewRecycler.layoutManager = LinearLayoutManager(activity)

        adapter = ChatViewRecyclerAdapter(chatArrayList)
        binding.chatViewRecycler.adapter = adapter

        auth = FirebaseAuth.getInstance()
        db = FirebaseFirestore.getInstance()

        getDataFromFireStoreChats()

        return view

    }


    fun getDataFromFireStoreChats() {

        val currentUserUid = auth.currentUser?.uid // Oturum açmış kullanıcının userId'sini alın

        db.collection("Chats").whereEqualTo("userEmail", "${auth.currentUser?.email.toString()}")
            .orderBy("date", Query.Direction.DESCENDING)
            .addSnapshotListener { snapshot, exception ->
                if (exception != null) {
                    Toast.makeText(context, exception.localizedMessage, Toast.LENGTH_LONG).show()
                } else {

                    if (snapshot != null) {
                        if (!snapshot.isEmpty) {

                            chatArrayList.clear()

                            val documents = snapshot.documents
                            val uniqueDownloadUrls = HashSet<String>() // Gönderilen downloadUrl'leri tutmak için bir küme oluştur
                            val uniqueUsernames = HashSet<String>() // Gönderilen username'leri tutmak için bir küme oluştur
                            val uniqueNames = HashSet<String>() // Gönderilen username'leri tutmak için bir küme oluştur
                            val uniqueUserId = HashSet<String>()
                            val uniqueUserEmail = HashSet<String>()
                            for (document in documents) {
                                val userEmail = document.get("userEmail") as String
                                val userTo = document.get("userTo") as String

                                if (userEmail != null && userTo != null) {

                                    val otherUserId = userTo.replace(
                                        currentUserUid.toString(),
                                        ""
                                    ) // Oturum açmış kullanıcının userId'sini çıkar

                                    println(otherUserId)
                                    println(userEmail)
                                    println(userTo)

                                    db.collection("Users")
                                        .whereEqualTo("userId", otherUserId)
                                        .addSnapshotListener { snapshot, exception ->
                                            if (exception != null) {
                                                Toast.makeText(
                                                    requireContext(),
                                                    exception.localizedMessage,
                                                    Toast.LENGTH_LONG
                                                ).show()
                                            } else {

                                                if (snapshot != null) {
                                                    if (!snapshot.isEmpty) {

                                                        val documents = snapshot.documents
                                                        for (document in documents) {
                                                            val name = document.get("name") as String
                                                            val userEmail = document.get("userEmail") as String
                                                            val userId = document.get("userId") as String
                                                            val username = document.get("username") as String
                                                            val downloadUrl = document.get("downloadUrl") as String

                                                            println(name)
                                                            println(username)

                                                            // Eğer downloadUrl veya username daha önce eklenmediyse
                                                            if (!uniqueDownloadUrls.contains(downloadUrl) && !uniqueUsernames.contains(username) && !uniqueNames.contains(name) && !uniqueUserId.contains(userId)&& !uniqueUserEmail.contains(userEmail)) {

                                                                uniqueDownloadUrls.add(downloadUrl) // Kümeye ekle
                                                                uniqueUsernames.add(username) // Kümeye ekle
                                                                uniqueUsernames.add(name)
                                                                uniqueUsernames.add(userId)
                                                                uniqueUsernames.add(userEmail)
                                                                val chatview = ChatView(downloadUrl,username,name,userId,userEmail)
                                                                chatArrayList.add(chatview)
                                                            }
                                                            adapter!!.notifyDataSetChanged()

                                                        }
                                                    }

                                                }
                                            }
                                        }

                                }
                            }

                        }
                    }


                }

            }
    }
}
