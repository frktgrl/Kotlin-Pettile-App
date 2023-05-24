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
import com.example.apppettileapp.databinding.ActivityPetAddBinding
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


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityPetAddBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)

        auth = Firebase.auth
        db = Firebase.firestore

        registerLauncher()


    }

    fun changePhotoClicked (view : View) {


        // API 33+
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.READ_MEDIA_IMAGES) != PackageManager.PERMISSION_GRANTED ||
            ContextCompat.checkSelfPermission(this, Manifest.permission.READ_MEDIA_AUDIO) != PackageManager.PERMISSION_GRANTED ||
            ContextCompat.checkSelfPermission(this, Manifest.permission.READ_MEDIA_VIDEO) != PackageManager.PERMISSION_GRANTED) {

            ActivityCompat.requestPermissions(this, arrayOf(Manifest.permission.READ_MEDIA_IMAGES, Manifest.permission.READ_MEDIA_AUDIO, Manifest.permission.READ_MEDIA_VIDEO), 1)
            Snackbar.make(view, "Permission needed for gallery", Snackbar.LENGTH_INDEFINITE).setAction("Give Permission", View.OnClickListener {
                permissionLauncher.launch(Manifest.permission.READ_MEDIA_IMAGES)
                permissionLauncher.launch(Manifest.permission.READ_MEDIA_AUDIO)
                permissionLauncher.launch(Manifest.permission.READ_MEDIA_VIDEO)}).show()}
        else {
            val intentToGallery = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
            activityResultLauncher.launch(intentToGallery) }

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
                    val familyList = ArrayList<HashMap<String, Any>>() // familyList'in türünü HashMap<String, Any> olarak belirtin
                    val favoriteItem = HashMap<String, Any>()
                    favoriteItem["petId"] = petId
                    familyList.add(favoriteItem)

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
                            val intent = Intent(applicationContext, FeedActivity::class.java)
                            startActivity(intent)
                            finish()

                        }

                    }.addOnFailureListener{exception ->
                        Toast.makeText(applicationContext,exception.localizedMessage, Toast.LENGTH_LONG).show()
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
                                this@PetAddActivity.contentResolver,
                                selectedPicture!!
                            )
                            selectedBitmap = ImageDecoder.decodeBitmap(source)
                            binding.petAddView.setImageBitmap(selectedBitmap)
                        } else {
                            selectedBitmap = MediaStore.Images.Media.getBitmap(
                                this@PetAddActivity.contentResolver,
                                selectedPicture
                            )
                            binding.petAddView.setImageBitmap(selectedBitmap)
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
                Toast.makeText(this@PetAddActivity, "Permisson needed!", Toast.LENGTH_LONG).show()
            }
        }
    }

    fun backButtonClicked (view: View) {
        val intent = Intent(applicationContext, FeedActivity::class.java)
        startActivity(intent)
        finish()

    }

}