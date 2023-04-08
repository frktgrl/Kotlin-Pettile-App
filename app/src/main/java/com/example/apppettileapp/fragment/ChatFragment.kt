package com.example.apppettileapp.fragment

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.apppettileapp.R
import com.example.apppettileapp.adapter.ChatRecyclerAdapter
import com.example.apppettileapp.databinding.FragmentChatBinding
import com.example.apppettileapp.databinding.FragmentHomeBinding
import com.example.apppettileapp.model.Chat
import com.example.apppettileapp.model.Post
import com.google.firebase.Timestamp
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import com.squareup.picasso.Picasso


class ChatFragment : Fragment() {

    private var _binding: FragmentChatBinding? = null
    private val binding get() = _binding!!
    private lateinit var adapter: ChatRecyclerAdapter
    private lateinit var firestore : FirebaseFirestore
    private lateinit var auth : FirebaseAuth
    private var chats = arrayListOf<Chat>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        firestore = FirebaseFirestore.getInstance()
        auth = FirebaseAuth.getInstance()



    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentChatBinding.inflate(inflater,container,false)
        val view = binding.root
        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        adapter = ChatRecyclerAdapter()
        binding.chatRecyclerView.adapter = adapter
        binding.chatRecyclerView.layoutManager = LinearLayoutManager(requireContext())

        binding.sendButton.setOnClickListener {

            val chatText = binding.chatText.text.toString()
            val userEmail = auth.currentUser!!.email!!

            val dataMap = HashMap<String, Any>()
            dataMap.put("text",chatText)
            dataMap.put("userEmail",userEmail)
            dataMap.put("date",FieldValue.serverTimestamp())

            firestore.collection("Chats").add(dataMap).addOnSuccessListener {
                binding.chatText.setText("")
            }.addOnFailureListener {
                Toast.makeText(requireContext(),it.localizedMessage,Toast.LENGTH_LONG).show()
                binding.chatText.setText("")
            }
        }

        firestore.collection("Chats").orderBy("date",
            Query.Direction.ASCENDING).addSnapshotListener { value, error ->
            if (value != null) {
                if(value!!.isEmpty) {
                    Toast.makeText(requireContext(),"No Chat",Toast.LENGTH_LONG).show()
                } else {
                    val documents = value.documents
                    chats.clear()
                    for (document in documents ) {
                        val text = document.get("text") as String
                        val user = document.get("userEmail") as String
                        val chat = Chat(user,text)
                        chats.add(chat)
                        adapter.chats = chats
                    }
                }
            }
            adapter.notifyDataSetChanged()
        }

    }


    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}