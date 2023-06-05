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
import com.example.pettile.databinding.ActivityPetAddBinding
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

class PetAddActivity : AppCompatActivity() {


    var selectedPicture : Uri? = null
    var selectedBitmap : Bitmap? = null
    private lateinit var db : FirebaseFirestore
    private lateinit var auth : FirebaseAuth
    private lateinit var binding: ActivityPetAddBinding
    private lateinit var activityResultLauncher: ActivityResultLauncher<Intent>
    private lateinit var permissionLauncher: ActivityResultLauncher<String>


    fun backImageClicked(view: View) {
        val intent = Intent(this, FeedActivity::class.java)
        intent.putExtra("selectedTab", 5) // AdoptionFragment'ı açmak için seçili sekmenin indeksini iletiyoruz
        startActivity(intent)
        finish() // AdoptionCreateActivity'yi kapatıyoruz
    }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityPetAddBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)

        auth = Firebase.auth
        db = Firebase.firestore

        registerLauncher()


    }


    //Verileri Kaydetme
    fun saveButtonClicked (view: View) {

        //UUID -> image name
        val uuid = UUID.randomUUID()
        val imageName = "$uuid.jpg"
        val petId = UUID.randomUUID().toString()

        val storage = Firebase.storage
        val reference = storage.reference
        val imagesReference = reference.child("imagesPet").child(imageName)

        if (selectedPicture != null) {
            imagesReference.putFile(selectedPicture!!).addOnSuccessListener { taskSnapshot ->

                val uploadedPictureReference = storage.reference.child("imagesPet").child(imageName)
                uploadedPictureReference.downloadUrl.addOnSuccessListener { uri ->
                    val downloadUrl = uri.toString()
                    println(downloadUrl)

                    val familyList = ArrayList<Map<String, Any>>()


                    val postMap = hashMapOf<String, Any>()
                    postMap["userEmail"] = auth.currentUser?.email.toString()
                    postMap["userId"] = "${auth.currentUser?.uid}"
                    postMap["downloadUrl"] = downloadUrl
                    postMap["name"] = binding.petNameText.text.toString()
                    postMap["genus"] = binding.petGenusText.text.toString()
                    postMap["gender"] = binding.petGenderText.text.toString()
                    postMap["age"] = binding.petAgeText.text.toString()
                    postMap["date"] = Timestamp.now()
                    postMap["petId"] = petId
                    postMap["family"] = familyList



                    db.collection( "Pets").add(postMap).addOnCompleteListener{task ->

                        if (task.isComplete && task.isSuccessful) {
                            //back
                            Toast.makeText(this@PetAddActivity, "Upload Successful", Toast.LENGTH_LONG).show()
                            finish()

                            val intent = Intent(this, FeedActivity::class.java)
                            intent.putExtra("selectedTab", 5) // AdoptionFragment'ı açmak için seçili sekmenin indeksini iletiyoruz
                            startActivity(intent)
                            finish() // AdoptionCreateActivity'yi kapatıyoruz

                        }

                    }.addOnFailureListener{exception ->
                        Toast.makeText(applicationContext,exception.localizedMessage, Toast.LENGTH_LONG).show()
                    }


                }

            }

        }


    }
    fun selectPetImage (view : View){

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
                            val source = ImageDecoder.createSource(this@PetAddActivity.contentResolver, imageData!!)
                            selectedBitmap = ImageDecoder.decodeBitmap(source)
                            binding.petAddImage.setImageBitmap(selectedBitmap)
                        } else {
                            selectedBitmap = MediaStore.Images.Media.getBitmap(this@PetAddActivity.contentResolver, imageData)
                            binding.petAddImage.setImageBitmap(selectedBitmap)
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
                Toast.makeText(this@PetAddActivity, "Permission needed!", Toast.LENGTH_LONG).show()
            }
        }
    }



    fun backButtonClicked (view: View) {
        val intent = Intent(applicationContext, FeedActivity::class.java)
        startActivity(intent)
        finish()

    }

}