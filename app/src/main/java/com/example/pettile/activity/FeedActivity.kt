package com.example.pettile.activity

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.navigation.NavController
import androidx.navigation.Navigation
import androidx.navigation.ui.NavigationUI.setupWithNavController
import com.example.pettile.R
import com.example.pettile.databinding.ActivityFeedBinding
import com.example.pettile.fragment.HomeFragmentDirections
import com.example.pettile.fragment.ProfileViewFragmentDirections
import com.onesignal.OneSignal

class FeedActivity : AppCompatActivity() {

    //Bildirimler için telefon id
    val ONESIGNAL_APP_ID = "e6779e56-d13f-44b0-aa2a-5d521277960f"


    private lateinit var binding: ActivityFeedBinding
    private lateinit var navController: NavController //Navigasyon denetleyici için değişken

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding= ActivityFeedBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)

        navController= Navigation.findNavController(this, R.id.activity_main_nav_host_fragment)
        setupWithNavController(binding.bottomNavigationView,navController)

        oneSignal()


    }

    fun oneSignal () {

        // Logging set to help debug issues, remove before releasing your app.
        OneSignal.setLogLevel(OneSignal.LOG_LEVEL.VERBOSE, OneSignal.LOG_LEVEL.NONE)

        // OneSignal Initialization
        OneSignal.initWithContext(this)
        OneSignal.setAppId(ONESIGNAL_APP_ID)

        // promptForPushNotifications will show the native Android notification permission prompt.
        // We recommend removing the following code and instead using an In-App Message to prompt for notification permission (See step 7)
        OneSignal.promptForPushNotifications();


    }

}