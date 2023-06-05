package com.example.pettile.activity

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import com.example.pettile.R
import com.example.pettile.databinding.ActivitySaveBinding
import com.example.pettile.fragment.*

class SaveActivity : AppCompatActivity() {

    private lateinit var binding: ActivitySaveBinding

    fun backImageClicked (view : View) {

        val intent = Intent(this, FeedActivity::class.java)
        intent.putExtra("selectedTab", 5) // AdoptionFragment'ı açmak için seçili sekmenin indeksini iletiyoruz
        startActivity(intent)
        finish() // AdoptionCreateActivity'yi kapatıyoruz
    }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding= ActivitySaveBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)


        binding.likesLinear.setOnClickListener {

            val intent = Intent(applicationContext, LikesDataActivity::class.java)
            startActivity(intent)
            finish()
        }

        binding.savesLinear.setOnClickListener {

            val intent = Intent(applicationContext, SaveDataActivity::class.java)
            startActivity(intent)
            finish()
        }


        binding.sharedPostLinear.setOnClickListener {

            val intent = Intent(applicationContext, SharedPostDataActivity::class.java)
            startActivity(intent)
            finish()

        }
        binding.sharedAdoptionLinear.setOnClickListener {

            val intent = Intent(applicationContext, SharedAdoptionPostDataActivity::class.java)
            startActivity(intent)
            finish()

        }
        binding.petsLinear.setOnClickListener {

            val intent = Intent(applicationContext, PetDataActivity::class.java)
            startActivity(intent)
            finish()
        }
        binding.familyLinear.setOnClickListener {

            val intent = Intent(applicationContext, FamilyDataActivity::class.java)
            startActivity(intent)
            finish()
        }

    }

}