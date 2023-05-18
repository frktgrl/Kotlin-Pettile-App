package com.example.apppettileapp.adapter

import androidx.fragment.app.Fragment
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.example.apppettileapp.fragment.AdoptionFavoritesFragment
import com.example.apppettileapp.fragment.AdoptionPostFragment
import com.example.apppettileapp.fragment.AdoptionFragment

class AdoptionPagerAdapter(fragment: AdoptionFragment) :
    FragmentStateAdapter(fragment) {
    override fun getItemCount(): Int = 2

    override fun createFragment(position: Int): Fragment {
        return when(position) {
            0 -> AdoptionPostFragment()
            1 -> AdoptionFavoritesFragment()
            else -> AdoptionFragment()
        }
    }
}