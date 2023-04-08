package com.example.apppettileapp.activity

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.navigation.NavController
import androidx.navigation.Navigation
import androidx.navigation.ui.NavigationUI.setupWithNavController
import com.example.apppettileapp.R
import com.example.apppettileapp.databinding.ActivityFeedBinding

class FeedActivity : AppCompatActivity() {


    private lateinit var binding: ActivityFeedBinding
    private lateinit var navController: NavController //Navigasyon denetleyici için değişken

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding= ActivityFeedBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)

        navController= Navigation.findNavController(this, R.id.activity_main_nav_host_fragment)
        setupWithNavController(binding.bottomNavigationView,navController)

    }
}