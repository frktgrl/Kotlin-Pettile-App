package com.example.pettile.adapter

import androidx.fragment.app.Fragment
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.example.pettile.fragment.AdoptionFavoritesFragment
import com.example.pettile.fragment.AdoptionPostFragment
import com.example.pettile.fragment.AdoptionFragment

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