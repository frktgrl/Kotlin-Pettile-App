package com.example.apppettileapp.activity

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.ImageDecoder
import android.net.Uri
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.MediaStore
import android.view.View
import android.widget.Toast
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.example.apppettileapp.databinding.ActivityProfileCreateBinding
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.Timestamp
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.ktx.storage
import java.io.IOException
import java.util.*

class ProfileCreateActivity : AppCompatActivity() {

    var selectedPicture : Uri? = null
    var selectedBitmap : Bitmap? = null
    private lateinit var db : FirebaseFirestore
    private lateinit var auth : FirebaseAuth
    private lateinit var binding: ActivityProfileCreateBinding
    private lateinit var activityResultLauncher: ActivityResultLauncher<Intent>
    private lateinit var permissionLauncher: ActivityResultLauncher<String>


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityProfileCreateBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)
        registerLauncher()

        auth = Firebase.auth
        db = Firebase.firestore


        // API 33-
//        binding.profileImage.setOnClickListener {
//
//            if (ContextCompat.checkSelfPermission(this  , Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED){
//
//                if (ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.READ_EXTERNAL_STORAGE)){
//
//                    Snackbar.make(view,"Permission needed for gallery", Snackbar.LENGTH_INDEFINITE).setAction("Give Permission") {
//                        //request permission
//                        permissionLauncher.launch(Manifest.permission.READ_EXTERNAL_STORAGE)
//                    }.show()
//                }else {
//                    //request permission
//                    permissionLauncher.launch(Manifest.permission.READ_EXTERNAL_STORAGE)
//                }
//
//            }else{
//
//                val intentToGallery = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
//                //start activity for result
//                activityResultLauncher.launch(intentToGallery)
//            }
//
//
//        }

        // API 33+
        binding.profileImage.setOnClickListener {     if (ContextCompat.checkSelfPermission(this, Manifest.permission.READ_MEDIA_IMAGES) != PackageManager.PERMISSION_GRANTED ||
            ContextCompat.checkSelfPermission(this, Manifest.permission.READ_MEDIA_AUDIO) != PackageManager.PERMISSION_GRANTED ||         ContextCompat.checkSelfPermission(this, Manifest.permission.READ_MEDIA_VIDEO) != PackageManager.PERMISSION_GRANTED)

        { ActivityCompat.requestPermissions(this, arrayOf(Manifest.permission.READ_MEDIA_IMAGES, Manifest.permission.READ_MEDIA_AUDIO, Manifest.permission.READ_MEDIA_VIDEO), 1)
            Snackbar.make(view, "Permission needed for gallery", Snackbar.LENGTH_INDEFINITE).setAction("Give Permission", View.OnClickListener {
                permissionLauncher.launch(Manifest.permission.READ_MEDIA_IMAGES)
                permissionLauncher.launch(Manifest.permission.READ_MEDIA_AUDIO)
                permissionLauncher.launch(Manifest.permission.READ_MEDIA_VIDEO)}).show()}
        else { val intentToGallery = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
            activityResultLauncher.launch(intentToGallery) }

        }


    }


    //Verileri Kaydetme
    fun saveButtonClicked (view: View) {

        //UUID -> image name
        val uuid = UUID.randomUUID()
        val imageName = "$uuid.jpg"

        val storage = Firebase.storage
        val reference = storage.reference
        val imagesReference = reference.child("imagesUser").child(imageName)

        if (selectedPicture != null) {
            imagesReference.putFile(selectedPicture!!).addOnSuccessListener { taskSnapshot ->

                val uploadedPictureReference = storage.reference.child("imagesUser").child(imageName)
                uploadedPictureReference.downloadUrl.addOnSuccessListener { uri ->
                    val downloadUrl = uri.toString()
                    println(downloadUrl)

                    val postMap = hashMapOf<String, Any>()
                    postMap.put("downloadUrl", downloadUrl)
                    postMap.put("name", binding.nameInput.text.toString())
                    postMap.put("userEmail", "${auth.currentUser?.email.toString()}")
                    postMap.put("userId", "${auth.currentUser?.uid}")
                    postMap.put("username", binding.usernameInput.text.toString())
                    postMap.put("biography", binding.bioInput.text.toString())
                    postMap.put("date", Timestamp.now())
                    postMap.put("followers", ArrayList<Map<String, Any>>()) // Boş ArrayList ekleniyor
                    postMap.put("following", ArrayList<Map<String, Any>>()) // Boş ArrayList ekleniyor
                    postMap.put("likeposts", ArrayList<Map<String, Any>>()) // Boş ArrayList ekleniyor
                    postMap.put("favoriteposts", ArrayList<Map<String, Any>>()) // Boş ArrayList ekleniyor


                    db.collection( "Users").add(postMap).addOnCompleteListener{task ->

                        if (task.isComplete && task.isSuccessful) {

                            //back
                            Toast.makeText(this@ProfileCreateActivity, "Registration Successful", Toast.LENGTH_LONG).show()
                            finish()
                            val intent = Intent(applicationContext, FeedActivity::class.java)
                            startActivity(intent)
                            finish()

                        }

                    }.addOnFailureListener{exception ->
                        Toast.makeText(applicationContext,exception.localizedMessage,Toast.LENGTH_LONG).show()
                    }


                }

            }

        }


    }

    private fun registerLauncher() {
        activityResultLauncher = registerForActivityResult(
            ActivityResultContracts.StartActivityForResult()
        ) { result ->
            if (result.resultCode == RESULT_OK) {
                val intentFromResult = result.data
                if (intentFromResult != null) {
                    selectedPicture = intentFromResult.data
                    try {
                        if (Build.VERSION.SDK_INT >= 28) {
                            val source = ImageDecoder.createSource(
                                this@ProfileCreateActivity.contentResolver,
                                selectedPicture!!
                            )
                            selectedBitmap = ImageDecoder.decodeBitmap(source)
                            binding.profileImage.setImageBitmap(selectedBitmap)
                        } else {
                            selectedBitmap = MediaStore.Images.Media.getBitmap(
                                this@ProfileCreateActivity.contentResolver,
                                selectedPicture
                            )
                            binding.profileImage.setImageBitmap(selectedBitmap)
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
                Toast.makeText(this@ProfileCreateActivity, "Permisson needed!", Toast.LENGTH_LONG).show()
            }
        }
    }


}
