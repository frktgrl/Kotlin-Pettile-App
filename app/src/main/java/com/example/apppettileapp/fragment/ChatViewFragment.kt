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

        db.collection("Chats").whereEqualTo("userEmail", "${auth.currentUser?.email.toString()}")
            .orderBy("date", Query.Direction.DESCENDING).
            addSnapshotListener { snapshot, exception ->
                if (exception != null) {
                    Toast.makeText(context, exception.localizedMessage, Toast.LENGTH_LONG).show()
                } else {

                    if (snapshot != null) {
                        if (!snapshot.isEmpty) {

                            chatArrayList.clear()

                            val documents = snapshot.documents
                            for (document in documents) {
                                val text = document.get("text") as String
                                val userEmail = document.get("userEmail") as String
                                val userTo = document.get("userTo") as String
                                //val downloadUrl = document.get("downloadUrl") as String
                                //val timestamp = document.get("date") as Timestamp
                                //val date = timestamp.toDate()

                                println(text)
                                println(userEmail)
                                println(userTo)


                                val chatview = ChatView(text, userEmail)
                                chatArrayList.add(chatview)
                            }
                            adapter!!.notifyDataSetChanged()

                        }
                    }

                }
            }


    }

}
