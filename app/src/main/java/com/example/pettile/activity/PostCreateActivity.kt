package com.example.pettile.activity

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
import com.example.pettile.databinding.ActivityPostCreateBinding
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

class PostCreateActivity : AppCompatActivity() {

    private lateinit var binding: ActivityPostCreateBinding

    private lateinit var activityResultLauncher : ActivityResultLauncher<Intent>
    private lateinit var permissionLauncher: ActivityResultLauncher<String>
    var selectedPicture : Uri? = null
    private lateinit var auth: FirebaseAuth
    private lateinit var firestore : FirebaseFirestore
    private lateinit var storage : FirebaseStorage
    var selectedBitmap : Bitmap? = null

    fun backImageClicked ( view: View) {

        val intent = Intent(this, FeedActivity::class.java)
        startActivity(intent)
    }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding= ActivityPostCreateBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)

        auth = Firebase.auth
        firestore = Firebase.firestore
        storage = Firebase.storage

        registerLauncher()



        //paylaş butonu
        binding.shareButton.setOnClickListener {

            val uuid = UUID.randomUUID() //rastgele id
            val imageName = "$uuid.jpg"

            val reference = storage.reference
            val imageReference = reference.child("imagesPost").child(imageName)

            if(selectedPicture != null) {

                imageReference.putFile(selectedPicture!!).addOnSuccessListener {

                    //download url -> firestore

                    val uploadPictureReference = storage.reference.child("imagesPost").child(imageName)
                    uploadPictureReference.downloadUrl.addOnSuccessListener {
                        val downloadUrl = it.toString()

                        if (auth.currentUser != null) {

                            val uuidPost = UUID.randomUUID() //rastgele id

                            val postMap = hashMapOf<String, Any>()
                            postMap["downloadUrl"] = downloadUrl
                            postMap["comment"] = binding.titleInput.text.toString()
                            postMap["date"] = com.google.firebase.Timestamp.now()
                            postMap["userId"] = auth.currentUser!!.uid
                            postMap["like"] = ArrayList<Map<String, Any>>() // Boş ArrayList ekleniyor
                            postMap["save"] = ArrayList<Map<String, Any>>() // Boş ArrayList ekleniyor
                            postMap["postId"] = "$uuidPost"

                            firestore.collection("Posts").add(postMap).addOnSuccessListener {
                                Toast.makeText(this, "Upload Successful!", Toast.LENGTH_LONG).show()
                                val intent = Intent(this, FeedActivity::class.java)
                                startActivity(intent)

                            }.addOnFailureListener {
                                Toast.makeText(this,it.localizedMessage, Toast.LENGTH_LONG).show()

                            }
                        }
                    }
                }.addOnFailureListener {

                    Toast.makeText(this,it.localizedMessage, Toast.LENGTH_LONG).show()
                }
            }
        }


        }




    fun selectPostImage (view : View){

        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            if(ContextCompat.checkSelfPermission(this, Manifest.permission.READ_MEDIA_IMAGES) != PackageManager.PERMISSION_GRANTED) {
                if (ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.READ_MEDIA_IMAGES)) {
                    Snackbar.make(view, "Permission needed for gallery", Snackbar.LENGTH_INDEFINITE).setAction("Give Permission",
                        {
                            permissionLauncher.launch(Manifest.permission.READ_MEDIA_IMAGES)
                        }).show()
                } else {
                    permissionLauncher.launch(Manifest.permission.READ_MEDIA_IMAGES)
                }
            } else {
                val intentToGallery = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
                activityResultLauncher.launch(intentToGallery)
            }
        } else {
            if(ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
                if (ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.READ_EXTERNAL_STORAGE)) {
                    Snackbar.make(view, "Permission needed for gallery", Snackbar.LENGTH_INDEFINITE).setAction("Give Permission",
                        View.OnClickListener {
                            permissionLauncher.launch(Manifest.permission.READ_EXTERNAL_STORAGE)
                        }).show()
                } else {
                    permissionLauncher.launch(Manifest.permission.READ_EXTERNAL_STORAGE)
                }
            } else {
                val intentToGallery = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
                activityResultLauncher.launch(intentToGallery)
            }
        }
    }


    private fun registerLauncher() {
        activityResultLauncher = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            if (result.resultCode == RESULT_OK) {
                val intentFromResult = result.data
                if (intentFromResult != null) {
                    val imageData = intentFromResult.data
                    // Uri'yi kullanarak işlemleri gerçekleştirin
                    try {
                        if (Build.VERSION.SDK_INT >= 28) {
                            val source = ImageDecoder.createSource(this@PostCreateActivity.contentResolver, imageData!!)
                            selectedBitmap = ImageDecoder.decodeBitmap(source)
                            binding.petImage.setImageBitmap(selectedBitmap)
                        } else {
                            selectedBitmap = MediaStore.Images.Media.getBitmap(this@PostCreateActivity.contentResolver, imageData)
                            binding.petImage.setImageBitmap(selectedBitmap)
                        }
                        // Uri'yi elde etmek için imageData'ı selectedPicture değişkenine atayın
                        selectedPicture = imageData
                    } catch (e: IOException) {
                        e.printStackTrace()
                    }
                }
            }
        }

        permissionLauncher = registerForActivityResult(ActivityResultContracts.RequestPermission()) { result ->
            if (result) {
                // İzin verildiğinde galeriye erişim için intent başlatılıyor
                val intentToGallery = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
                activityResultLauncher.launch(intentToGallery)
            } else {
                // İzin reddedildiğinde bir Toast mesajı gösteriliyor
                Toast.makeText(this@PostCreateActivity, "Permission needed!", Toast.LENGTH_LONG).show()
            }
        }
    }





}