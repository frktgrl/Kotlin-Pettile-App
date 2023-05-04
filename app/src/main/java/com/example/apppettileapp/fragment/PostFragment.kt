package com.example.apppettileapp.fragment

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.ImageDecoder
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.provider.MediaStore
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.example.apppettileapp.activity.FeedActivity
import com.example.apppettileapp.databinding.FragmentPostBinding
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.ktx.storage
import java.io.IOException
import java.util.*

//ART BOOK İZİNLER
class PostFragment : Fragment() {



    var selectedBitmap : Bitmap? = null
    private lateinit var binding: FragmentPostBinding
    private lateinit var activityResultLauncher : ActivityResultLauncher<Intent>
    private lateinit var permissionLauncher: ActivityResultLauncher<String>
    var selectedPicture : Uri? = null
    private lateinit var auth: FirebaseAuth
    private lateinit var firestore : FirebaseFirestore
    private lateinit var storage : FirebaseStorage



    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment

        binding = FragmentPostBinding.inflate(layoutInflater)
        val view = binding.root

        auth = Firebase.auth
        firestore = Firebase.firestore
        storage = Firebase.storage

        binding.backImage.setOnClickListener {

            val intent = Intent(activity, FeedActivity::class.java)
            startActivity(intent)
        }



        binding.shareButton.setOnClickListener {

            val uuid = UUID.randomUUID() //rastgele id
            val imageName = "$uuid.jpg"

            val reference = storage.reference
            val imageReference = reference.child("images").child(imageName)

            if(selectedPicture != null) {

                imageReference.putFile(selectedPicture!!).addOnSuccessListener {

                    //download url -> firestore

                    val uploadPictureReference = storage.reference.child("images").child(imageName)
                    uploadPictureReference.downloadUrl.addOnSuccessListener {
                        val downloadUrl = it.toString()

                        if (auth.currentUser != null) {

                            val postMap = hashMapOf<String, Any>()
                            postMap.put("downloadUrl",downloadUrl)
                            postMap.put("userEmail",auth.currentUser!!.email!!)
                            postMap.put("comment",binding.commentText.text.toString())
                            postMap.put("date",com.google.firebase.Timestamp.now())
                            postMap.put("userId",auth.currentUser?.uid!!)
                            postMap.put("like", ArrayList<Map<String, Any>>()) // Boş ArrayList ekleniyor
                            postMap.put("recomment", ArrayList<Map<String, Any>>()) // Boş ArrayList ekleniyor

                            firestore.collection("Posts").add(postMap).addOnSuccessListener {

                                Toast.makeText(requireActivity(),"Upload Successful !", Toast.LENGTH_LONG).show()
                                val intent = Intent(activity, FeedActivity::class.java)
                                startActivity(intent)


                            }.addOnFailureListener {
                                Toast.makeText(activity,it.localizedMessage, Toast.LENGTH_LONG).show()

                            }

                        }
                    }
                }.addOnFailureListener {

                    Toast.makeText(requireActivity(),it.localizedMessage, Toast.LENGTH_LONG).show()
                }

            }

        }



        // API 33-
        binding.postImage.setOnClickListener {

            if (ContextCompat.checkSelfPermission(requireActivity(), Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED){

                if (ActivityCompat.shouldShowRequestPermissionRationale(requireActivity(), Manifest.permission.READ_EXTERNAL_STORAGE)){

                    Snackbar.make(view,"Permission needed for gallery", Snackbar.LENGTH_INDEFINITE).setAction("Give Permission") {
                        //request permission
                        permissionLauncher.launch(Manifest.permission.READ_EXTERNAL_STORAGE)
                    }.show()
                }else {
                    //request permission
                    permissionLauncher.launch(Manifest.permission.READ_EXTERNAL_STORAGE)
                }

            }else{

                val intentToGallery = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
                //start activity for result
                activityResultLauncher.launch(intentToGallery)
            }


        }

        // API 33+
        binding.postImage.setOnClickListener {     if (ContextCompat.checkSelfPermission(requireActivity(), Manifest.permission.READ_MEDIA_IMAGES) != PackageManager.PERMISSION_GRANTED ||
            ContextCompat.checkSelfPermission(requireActivity(), Manifest.permission.READ_MEDIA_AUDIO) != PackageManager.PERMISSION_GRANTED ||         ContextCompat.checkSelfPermission(requireActivity(), Manifest.permission.READ_MEDIA_VIDEO) != PackageManager.PERMISSION_GRANTED)

        { ActivityCompat.requestPermissions(requireActivity(), arrayOf(Manifest.permission.READ_MEDIA_IMAGES, Manifest.permission.READ_MEDIA_AUDIO, Manifest.permission.READ_MEDIA_VIDEO), 1)
            Snackbar.make(view, "Permission needed for gallery", Snackbar.LENGTH_INDEFINITE).setAction("Give Permission", View.OnClickListener {
                permissionLauncher.launch(Manifest.permission.READ_MEDIA_IMAGES)
                permissionLauncher.launch(Manifest.permission.READ_MEDIA_AUDIO)
                permissionLauncher.launch(Manifest.permission.READ_MEDIA_VIDEO)}).show()}
        else { val intentToGallery = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
            activityResultLauncher.launch(intentToGallery) }

        }




        registerLauncher()
        return view

    }

    fun registerLauncher() {
        activityResultLauncher = registerForActivityResult(
            ActivityResultContracts.StartActivityForResult()
        ) { result ->
            if (result.resultCode == AppCompatActivity.RESULT_OK) {
                val intentFromResult = result.data
                if (intentFromResult != null) {
                    selectedPicture = intentFromResult.data
                    try {
                        if (Build.VERSION.SDK_INT >= 28) {
                            val source = ImageDecoder.createSource(
                                requireActivity().contentResolver,
                                selectedPicture!!
                            )
                            selectedBitmap = ImageDecoder.decodeBitmap(source)
                            binding.postImage.setImageBitmap(selectedBitmap)
                        } else {
                            selectedBitmap = MediaStore.Images.Media.getBitmap(
                                requireActivity().contentResolver,
                                selectedPicture
                            )
                            binding.postImage.setImageBitmap(selectedBitmap)
                        }
                    } catch (e: IOException) {
                        e.printStackTrace()
                    }
                }
            }
        }
        permissionLauncher = registerForActivityResult(
            ActivityResultContracts.RequestPermission()
        ) { result ->
            if (result) {
                //permission granted
                val intentToGallery = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
                activityResultLauncher.launch(intentToGallery)
            } else {
                //permission denied
                Toast.makeText(activity, "Permisson needed!", Toast.LENGTH_LONG).show()
            }
        }
    }




}