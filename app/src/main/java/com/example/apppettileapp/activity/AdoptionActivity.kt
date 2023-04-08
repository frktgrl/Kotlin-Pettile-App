package com.example.apppettileapp.activity

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.viewpager2.widget.ViewPager2
import com.example.apppettileapp.adapter.AdoptionPagerAdapter
import com.example.apppettileapp.adapter.MyPagerAdapter
import com.example.apppettileapp.databinding.ActivityAdoptionBinding
import com.example.apppettileapp.databinding.ActivityFeedBinding
import com.example.apppettileapp.databinding.FragmentProfileBinding
import com.google.android.material.tabs.TabLayout
import com.google.android.material.tabs.TabLayoutMediator

class AdoptionActivity : AppCompatActivity() {

    private lateinit var binding: ActivityAdoptionBinding
    private lateinit var viewPagerAdoption: ViewPager2
    private lateinit var tabLayoutAdoption: TabLayout

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding= ActivityAdoptionBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)


        viewPagerAdoption = binding.viewPagerAdoption
        tabLayoutAdoption = binding.tabLayoutAdoption

        val viewadapterAdoption = AdoptionPagerAdapter(this)
        viewPagerAdoption.adapter = viewadapterAdoption

        TabLayoutMediator(tabLayoutAdoption, viewPagerAdoption) { tab, position ->
            val tabText = when(position) {
                0 -> "Adoption"
                1 -> "Favorites"
                else -> "Adoption"
            }
            tab.text = tabText
        }.attach()

    }
}