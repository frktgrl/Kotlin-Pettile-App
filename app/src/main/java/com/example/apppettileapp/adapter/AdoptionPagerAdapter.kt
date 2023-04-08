package com.example.apppettileapp.adapter

import androidx.fragment.app.Fragment
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.example.apppettileapp.activity.AdoptionActivity
import com.example.apppettileapp.fragment.AdoptionFavoritesFragment
import com.example.apppettileapp.fragment.AdoptionFragment

class AdoptionPagerAdapter(activity: AdoptionActivity) :
    FragmentStateAdapter(activity) {
    override fun getItemCount(): Int = 2

    override fun createFragment(position: Int): Fragment {
        return when(position) {
            0 -> AdoptionFragment()
            1 -> AdoptionFavoritesFragment()
            else -> AdoptionFragment()
        }
    }
}