package com.example.apppettileapp.activity

import android.Manifest
import android.content.ContentValues
import android.content.ContentValues.TAG
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.ImageDecoder
import android.net.Uri
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.example.apppettileapp.databinding.ActivityProfileUpdateBinding
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import java.io.IOException

class ProfileUpdateActivity : AppCompatActivity() {

    var selectedPicture: Uri? = null
    var selectedBitmap: Bitmap? = null
    private lateinit var auth: FirebaseAuth
    private lateinit var binding: ActivityProfileUpdateBinding
    private lateinit var permissionLauncher: ActivityResultLauncher<String>
    private lateinit var activityResultLauncher: ActivityResultLauncher<Intent>
    private lateinit var db: FirebaseFirestore

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityProfileUpdateBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)

        registerLauncher()
        auth = Firebase.auth
        db = Firebase.firestore


    }


    //Fotograf Secme Butonu ve izin alma
    fun changePhotoClicked(view: View) {
        if (ContextCompat.checkSelfPermission(
                this,
                Manifest.permission.READ_MEDIA_IMAGES
            ) != PackageManager.PERMISSION_GRANTED ||
            ContextCompat.checkSelfPermission(this, Manifest.permission.READ_MEDIA_AUDIO)
            != PackageManager.PERMISSION_GRANTED || ContextCompat.checkSelfPermission(
                this,
                Manifest.permission.READ_MEDIA_VIDEO
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            ActivityCompat.requestPermissions(
                this,
                arrayOf(
                    Manifest.permission.READ_MEDIA_IMAGES,
                    Manifest.permission.READ_MEDIA_AUDIO,
                    Manifest.permission.READ_MEDIA_VIDEO
                ),
                1
            )
            Snackbar.make(view, "Permission needed for gallery", Snackbar.LENGTH_INDEFINITE)
                .setAction("Give Permission", View.OnClickListener {
                    permissionLauncher.launch(Manifest.permission.READ_MEDIA_IMAGES)
                    permissionLauncher.launch(Manifest.permission.READ_MEDIA_AUDIO)
                    permissionLauncher.launch(Manifest.permission.READ_MEDIA_VIDEO)
                }).show()

        } else {

            val intentToGallery =
                Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
            activityResultLauncher.launch(intentToGallery)
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
                                this@ProfileUpdateActivity.contentResolver,
                                selectedPicture!!
                            )
                            selectedBitmap = ImageDecoder.decodeBitmap(source)
                            binding.profileImage.setImageBitmap(selectedBitmap)
                        } else {
                            selectedBitmap = MediaStore.Images.Media.getBitmap(
                                this@ProfileUpdateActivity.contentResolver,
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
                val intentToGallery =
                    Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
                activityResultLauncher.launch(intentToGallery)
            } else {
                //permission denied
                Toast.makeText(this@ProfileUpdateActivity, "Permisson needed!", Toast.LENGTH_LONG)
                    .show()
            }
        }
    }

    //Çıkış Yap butonu
    fun signOutButtonClicked(view: View) {

        auth.signOut()
        val intent = Intent(applicationContext, MainActivity::class.java)
        startActivity(intent)
        finish()
    }

    //Geri dön Butonu
    fun backButtonUpdateClicked(view: View) {
        val intent = Intent(applicationContext, FeedActivity::class.java)
        startActivity(intent)
        finish()

    }

    fun saveButtonClicked(view: View) {
        val name = binding.nameInput.text.toString()
        val username = binding.usernameInput.text.toString()
        val biography = binding.bioInput.text.toString()

        val collectionRef = db.collection("Users")
        val query = collectionRef.whereEqualTo("userId", "${auth.currentUser?.uid}").limit(1)

        query.get().addOnSuccessListener { querySnapshot ->
            if (!querySnapshot.isEmpty) {
                val documentSnapshot = querySnapshot.documents[0]
                val documentId = documentSnapshot.id

                // Belirli dökümana erişmek ve alanları güncellemek için
                val userRef = db.collection("Users").document(documentId)

                val updateData = HashMap<String, Any>()

                if (!name.isEmpty()) {
                    updateData["name"] = name
                }

                if (!username.isEmpty()) {
                    updateData["username"] = username
                }

                if (!biography.isEmpty()) {
                    updateData["biography"] = biography
                }

                if (updateData.isNotEmpty()) {
                    userRef.update(updateData).addOnSuccessListener {
                        Log.d(TAG, "Kullanıcı bilgileri güncellendi.")
                        // Başarılı güncelleme durumunda yapılacak işlemler
                        Toast.makeText(this@ProfileUpdateActivity, "Update Successful", Toast.LENGTH_LONG).show()
                        // EditText içeriğini temizle
                        binding.nameInput.text.clear()
                        binding.usernameInput.text.clear()
                        binding.bioInput.text.clear()
                    }.addOnFailureListener { e ->
                        Log.e(TAG, "Kullanıcı bilgileri güncellenirken hata oluştu.", e)
                        // Hata durumunda yapılacak işlemler
                    }
                } else {
                    Log.d(TAG, "Güncellenecek alan bulunamadı.")
                    // Güncellenecek alan yoksa yapılacak işlemler
                }
            } else {
                Log.d(TAG, "Kullanıcı bulunamadı.")
                // Kullanıcı bulunamadığı durumda yapılacak işlemler
            }
        }.addOnFailureListener { e ->
            Log.e(TAG, "Kullanıcı sorgulanırken hata oluştu.", e)
            // Hata durumunda yapılacak işlemler
        }
    }



}