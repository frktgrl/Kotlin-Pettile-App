package com.example.pettile.adapter

import androidx.fragment.app.Fragment
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.example.pettile.fragment.*

class MyPagerAdapter(fragmentActivity: ProfileFragment) :
    FragmentStateAdapter(fragmentActivity) {
    override fun getItemCount(): Int = 3

    override fun createFragment(position: Int): Fragment {
        return when(position) {
            0 -> ProfilePostFragment()
            1 -> ProfilePetFragment()
            2 -> ProfileFamilyFragment()
            else -> ProfilePostFragment()
        }
    }
}
