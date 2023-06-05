package com.example.pettile.fragment

import android.graphics.Rect
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.pettile.adapter.ChatRecyclerAdapter
import com.example.pettile.databinding.FragmentChatBinding
import com.example.pettile.databinding.FragmentHomeBinding
import com.example.pettile.model.Chat
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import com.squareup.picasso.Picasso


class ChatFragment : Fragment() {

    private lateinit var binding: FragmentChatBinding
    private lateinit var adapter: ChatRecyclerAdapter
    private lateinit var firestore : FirebaseFirestore
    private lateinit var auth : FirebaseAuth
    private var chats = arrayListOf<Chat>()
    private lateinit var layoutManager: LinearLayoutManager




    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        firestore = FirebaseFirestore.getInstance()
        auth = FirebaseAuth.getInstance()

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        binding = FragmentChatBinding.inflate(layoutInflater)
        val view = binding.root


        layoutManager = LinearLayoutManager(requireContext())

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

        binding.backImage.setOnClickListener {

            requireActivity().supportFragmentManager.popBackStack()

        }

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

                //fragment başladığında son mesajın olduğu yerden başlat.
                val itemCount = chats.size
                if (itemCount > 0) {
                    binding.chatRecyclerView.scrollToPosition(itemCount - 1)
                }


                adapter.notifyDataSetChanged()
            }

        // Klavye açıldığında son mesajı klavyenin üstüne getirir. Klavye mesajların üstüne açılmaz.
        binding.chatRecyclerView.layoutManager = layoutManager
        // Klavye durumunu dinlemek için bir global layout değişikliği dinleyici ekleyin
        view.viewTreeObserver.addOnGlobalLayoutListener {
            val rect = Rect()
            view.getWindowVisibleDisplayFrame(rect)
            val screenHeight = view.rootView.height
            val keypadHeight = screenHeight - rect.bottom

            if (keypadHeight > screenHeight * 0.15) {
                // Klavye açıldığında (ekran yüksekliğinin %15'inden daha büyük olduğunda) ve mesajlar varsa kaydırmayı yapın
                if (chats.isNotEmpty()) {
                    binding.chatRecyclerView.smoothScrollToPosition(chats.size - 1)
                }
            }
        }
    }

}