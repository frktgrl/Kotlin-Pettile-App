package com.example.pettile.fragment

import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.viewpager2.widget.ViewPager2
import com.example.pettile.activity.AdoptionCreateActivity
import com.example.pettile.adapter.AdoptionPagerAdapter
import com.example.pettile.databinding.FragmentAdoptionBinding
import com.google.android.material.tabs.TabLayout
import com.google.android.material.tabs.TabLayoutMediator

//ART BOOK İZİNLER
class AdoptionFragment : Fragment() {



    private lateinit var binding: FragmentAdoptionBinding
    private lateinit var viewPagerAdoption: ViewPager2
    private lateinit var tabLayoutAdoption: TabLayout



    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment

        binding = FragmentAdoptionBinding.inflate(layoutInflater)
        val view = binding.root


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



        binding.adoptionAddImage.setOnClickListener {

            val intent = Intent(requireContext(), AdoptionCreateActivity::class.java)
            startActivity(intent)

        }

        return view

    }


}