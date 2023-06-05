package com.example.pettile.fragment

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.pettile.Interface.PetItemClickListener
import com.example.pettile.adapter.PetRequestMyPetAdapter
import com.example.pettile.databinding.FragmentPetFamilyRequestBinding
import com.example.pettile.model.PetRequest
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.squareup.picasso.Picasso


class PetFamilyRequestFragment : Fragment(), PetItemClickListener {


    private lateinit var binding: FragmentPetFamilyRequestBinding
    private lateinit var auth: FirebaseAuth
    private lateinit var db: FirebaseFirestore

    val petArrayList : ArrayList<PetRequest> = ArrayList()
    var adapter : PetRequestMyPetAdapter? = null


    override fun onPetItemClick(name: String, downloadUrl: String) {
        binding.requestNameText.text = name
        Picasso.get().load(downloadUrl).into(binding.requestImage)

        // saveButton tıklama olayı
        binding.postImage.setOnClickListener {
            saveDataToFirebase(downloadUrl)
        }

    }



    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        }


    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        binding = FragmentPetFamilyRequestBinding.inflate(layoutInflater)
        val view = binding.root


        binding.mypetRecyclerView.layoutManager = LinearLayoutManager(activity)

        adapter = PetRequestMyPetAdapter(this,petArrayList,this)
        binding.mypetRecyclerView.adapter = adapter


        auth = FirebaseAuth.getInstance()
        db = FirebaseFirestore.getInstance()


        getDataFromFirestoreUserPet()

        //Aile isteği atılacak pet bilgileri
        val downloadUrl = arguments?.getString("downloadUrl")
        val name = arguments?.getString("name")
        val petId = arguments?.getString("petId")
        Picasso.get().load(downloadUrl).into(binding.profileImage)
        binding.profileNameText.text = name


        binding.backImage.setOnClickListener {

            requireActivity().supportFragmentManager.popBackStack()
        }

        return view
    }

    //İstek göndermek isteyen kişinin petlerini listele ve seçmesini sağla

    fun getDataFromFirestoreUserPet() {

        db.collection("Pets")
            .whereEqualTo("userId", "${auth.currentUser?.uid}")
            .addSnapshotListener { snapshot, exception ->
                if (exception != null) {
                    Toast.makeText(requireContext(), exception.localizedMessage, Toast.LENGTH_LONG).show()
                } else {

                    if (snapshot != null) {
                        if (!snapshot.isEmpty) {

                            petArrayList.clear()


                            val documents = snapshot.documents
                            for (document in documents) {
                                val name = document.get("name") as String
                                val downloadUrl = document.get("downloadUrl") as String
                                val petId = document.get("petId") as String

                                println(name)
                                val pet = PetRequest(downloadUrl,name,petId)
                                petArrayList.add(pet)

                            }
                            adapter!!.notifyDataSetChanged()

                        }
                    }

                }
            }

    }

    fun saveDataToFirebase(downloadUrlRequest: String) {

        // downloadUrl değerini almak için
        val downloadUrl = arguments?.getString("downloadUrl")

        val collectionRef = db.collection("Pets")
        val query = collectionRef.whereEqualTo("downloadUrl", downloadUrl).limit(1)

        query.get().addOnSuccessListener { querySnapshot ->
            if (!querySnapshot.isEmpty) {
                val documentSnapshot = querySnapshot.documents[0]

                // "family" dizisine yeni verileri ekleme
                val family = documentSnapshot.get("family") as? ArrayList<String>

                if (family != null && !family.contains(downloadUrlRequest)) {
                    family.add(downloadUrlRequest)

                    // Güncellenmiş veriyi Firestore'a kaydetme
                    documentSnapshot.reference.update("family", family)
                        .addOnSuccessListener {
                            // Veri başarıyla güncellendi
                            Toast.makeText(requireActivity(), "Request Successful", Toast.LENGTH_LONG).show()

                            // Eğer başarılı olursa gönderen kullanıcının dizisine de ekle
                            val senderCollectionRef = db.collection("Pets")
                            val senderQuery = senderCollectionRef.whereEqualTo("downloadUrl", downloadUrlRequest).limit(1)

                            senderQuery.get().addOnSuccessListener { senderQuerySnapshot ->
                                if (!senderQuerySnapshot.isEmpty) {
                                    val senderDocumentSnapshot = senderQuerySnapshot.documents[0]

                                    // "family" dizisine yeni verileri ekleme
                                    val senderFamily = senderDocumentSnapshot.get("family") as? ArrayList<String>

                                    if (senderFamily != null && !senderFamily.contains(downloadUrl!!)) {
                                        senderFamily.add(downloadUrl!!)

                                        // Güncellenmiş veriyi Firestore'a kaydetme
                                        senderDocumentSnapshot.reference.update("family", senderFamily)
                                            .addOnSuccessListener {
                                                // Veri başarıyla güncellendi
                                            }
                                            .addOnFailureListener { e ->
                                                // Hata oluştu, veri güncellenemedi
                                            }
                                    }
                                }
                            }
                        }
                        .addOnFailureListener { e ->
                            // Hata oluştu, veri güncellenemedi
                        }
                }
            }
        }
    }




}