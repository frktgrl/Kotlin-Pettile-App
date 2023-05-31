package com.example.apppettileapp.activity

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import com.example.apppettileapp.R
import com.example.apppettileapp.databinding.ActivityFeedBinding
import com.example.apppettileapp.databinding.ActivitySaveBinding
import com.example.apppettileapp.fragment.*

class SaveActivity : AppCompatActivity() {

    private lateinit var binding: ActivitySaveBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding= ActivitySaveBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)


        binding.likesLinear.setOnClickListener {
            val fragment = LikesDataFragment()
            val transaction = supportFragmentManager.beginTransaction()
            transaction.replace(R.id.container, fragment)
            transaction.addToBackStack(null)
            transaction.commit()
        }

        binding.savesLinear.setOnClickListener {
            val fragment = SaveDataFragment()
            val transaction = supportFragmentManager.beginTransaction()
            transaction.replace(R.id.container, fragment)
            transaction.addToBackStack(null)
            transaction.commit()

        }

        binding.sharedPostLinear.setOnClickListener {
            val fragment = SharedPostDataFragment()
            val transaction = supportFragmentManager.beginTransaction()
            transaction.replace(R.id.container, fragment)
            transaction.addToBackStack(null)
            transaction.commit()

        }
        binding.sharedAdoptionLinear.setOnClickListener {
            val fragment = SharedAdoptionPostDataFragment()
            val transaction = supportFragmentManager.beginTransaction()
            transaction.replace(R.id.container, fragment)
            transaction.addToBackStack(null)
            transaction.commit()

        }
        binding.petsLinear.setOnClickListener {
            val fragment = PetDataFragment()
            val transaction = supportFragmentManager.beginTransaction()
            transaction.replace(R.id.container, fragment)
            transaction.addToBackStack(null)
            transaction.commit()

        }
        binding.familyLinear.setOnClickListener {
            val fragment = FamilyDataFragment()
            val transaction = supportFragmentManager.beginTransaction()
            transaction.replace(R.id.container, fragment)
            transaction.addToBackStack(null)
            transaction.commit()

        }





    }






}