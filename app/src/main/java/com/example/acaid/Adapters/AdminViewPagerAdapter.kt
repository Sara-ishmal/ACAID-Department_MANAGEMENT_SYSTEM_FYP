package com.example.acaid.Adapters

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.example.acaid.Fragments.AdminHomeFragment
import com.example.acaid.Fragments.ChatFragment
import com.example.acaid.Fragments.CreateScheduleFragment
import com.example.acaid.Fragments.ProfileFragment
import com.example.acaid.Fragments.ResourcesFragment

class AdminViewPagerAdapter(fragmentActivity: FragmentActivity): FragmentStateAdapter(fragmentActivity) {
    val fragments= listOf(
        AdminHomeFragment(), ChatFragment(),
        CreateScheduleFragment(), ProfileFragment()
    )
    override fun getItemCount(): Int = fragments.size
    override fun createFragment(position: Int): Fragment {
        return fragments[position]
    }

}