package com.example.apppettileapp.fragment

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.apppettileapp.adapter.ChatRecyclerAdapter
import com.example.apppettileapp.databinding.FragmentChatBinding
import com.example.apppettileapp.model.Chat
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


        // Bundle'dan verileri alma
        val userEmail = arguments?.getString("userEmail")
        val username = arguments?.getString("username")
        val name = arguments?.getString("name")
        val downloadUrl = arguments?.getString("downloadUrl")
        val userId = arguments?.getString("userId")
        // Diğer verileri de bundle'dan alabilirsiniz

        println(userEmail)
        println(username)
        println(name)

        // UI bileşenlerine verileri atama
        binding.usernameText.text= username
        Picasso.get().load(downloadUrl).into(binding.profileImage)

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

            //chatlerin kimle kim arasında olduğunu
            val username = arguments?.getString("username")
            val name = arguments?.getString("name")
            val downloadUrl = arguments?.getString("downloadUrl")
            val userId = arguments?.getString("userId")
            val userTo = userId+"${auth.currentUser?.uid}" // bundledan gelen mesaj gönderilecek kişi ve oturum açmış kişinin userId birleşimi
            //chatlerin kimle kim arasında olduğunu

            val dataMap = HashMap<String, Any>()
            dataMap.put("text",chatText)
            dataMap.put("userEmail",userEmail)
            dataMap.put("date",FieldValue.serverTimestamp())
            dataMap.put("userTo",userTo)
            dataMap.put("username", username!!)
            dataMap.put("name",name!!)
            dataMap.put("downloadUrl",downloadUrl!!)


            firestore.collection("Chats").add(dataMap).addOnSuccessListener {
                binding.chatText.setText("")
            }.addOnFailureListener {
                Toast.makeText(requireContext(),it.localizedMessage,Toast.LENGTH_LONG).show()
                binding.chatText.setText("")
            }
        }

        //kimden kime belirlemek için
        val userId = arguments?.getString("userId")

        //üstte sorgulanan ekrana yazar

        firestore.collection("Chats")
            .orderBy("date", Query.Direction.ASCENDING)
            .whereIn("userTo", listOf(
                "${auth.currentUser?.uid.toString()}$userId",
                "$userId${auth.currentUser?.uid.toString()}"
            ))
            .addSnapshotListener { value, error ->
                if (error != null) {
                    Toast.makeText(requireContext(), error.localizedMessage, Toast.LENGTH_LONG).show()
                    return@addSnapshotListener
                }

                chats.clear()

                if (value != null && !value.isEmpty) {
                    val documents = value.documents

                    for (document in documents) {
                        val text = document.getString("text")
                        val userEmail = document.getString("userEmail")
                        val username = document.getString("username")
                        val name = document.getString("name")
                        val downloadUrl = document.getString("downloadUrl")

                        if (text != null && userEmail != null) {
                            val chat = Chat(text, userEmail,name,username,downloadUrl)
                            chats.add(chat)
                        }
                    }
                }

                adapter.chats = chats
                adapter.notifyDataSetChanged()
            }

    }


    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}