package com.example.apppettileapp.adapter

import androidx.fragment.app.Fragment
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.example.apppettileapp.fragment.*

class ProfileViewPagerAdapter(fragmentActivity: ProfileViewFragment) :
    FragmentStateAdapter(fragmentActivity) {
    override fun getItemCount(): Int = 3

    override fun createFragment(position: Int): Fragment {
        return when(position) {
            0 -> ProfilePostViewFragment()
            1 -> ProfilePetViewFragment()
            2 -> ProfileFamilyViewFragment()
            else -> ProfilePostViewFragment()
        }
    }
}