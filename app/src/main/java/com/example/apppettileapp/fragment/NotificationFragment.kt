package com.example.apppettileapp.fragment

import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.Navigation
import com.example.apppettileapp.R
import com.example.apppettileapp.activity.AdoptionActivity
import com.example.apppettileapp.databinding.FragmentChatBinding
import com.example.apppettileapp.databinding.FragmentNotificationBinding


class NotificationFragment : Fragment() {

    private lateinit var binding: FragmentNotificationBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentNotificationBinding.inflate(layoutInflater)
        val view = binding.root



        binding.chatImage.setOnClickListener {

            val action = NotificationFragmentDirections.actionNotificationFragmentToChatFragment()
            Navigation.findNavController(requireView()).navigate(action)
        }

        return view
    }


}