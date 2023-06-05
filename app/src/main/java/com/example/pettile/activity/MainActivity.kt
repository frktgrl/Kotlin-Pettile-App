package com.example.pettile.activity

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.Toast
import com.example.pettile.databinding.ActivityMainBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase

class MainActivity : AppCompatActivity() {


    private lateinit var auth : FirebaseAuth // Firebase kullanıcı işlemleri için
    private lateinit var binding: ActivityMainBinding


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        //Binding işlemleri
        binding = ActivityMainBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)
        //Binding işlemleri

        //Firebase kullanıcı işlemleri için
        auth = Firebase.auth

        //Giriş yapmış kullanıcı var mı kontrol eder

        val currentUser = auth.currentUser

        if (currentUser != null) {
            val intent = Intent(applicationContext, FeedActivity::class.java)
            startActivity(intent)
            finish()
        }


    }

    // Giriş yapma işlemleri için
    fun signInButtonClicked (view: View){
        val userEmail = binding.emailSignIn.text.toString()
        val password = binding.passwordSignIn.text.toString()

        if (userEmail.isNotEmpty() && password.isNotEmpty()) {
            auth.signInWithEmailAndPassword(userEmail,password).addOnCompleteListener { task ->

                if (task.isSuccessful) {
                    //Signed In
                    Toast.makeText(applicationContext,"Welcome: ${auth.currentUser?.email.toString()}",Toast.LENGTH_LONG).show()
                    val intent = Intent(applicationContext, FeedActivity::class.java)
                    startActivity(intent)
                    finish()

                }

            }.addOnFailureListener { exception ->
                Toast.makeText(applicationContext,exception.localizedMessage,Toast.LENGTH_LONG).show()
            }
        }else {

            Toast.makeText(this,"Enter email or password !",Toast.LENGTH_LONG).show()
        }




    }
    //Sign up ekranına giden button
    fun signUpTextButtonClicked (view: View) {

        val intent = Intent(applicationContext, RegisterActivity::class.java)
        startActivity(intent)
        finish()
    }




}