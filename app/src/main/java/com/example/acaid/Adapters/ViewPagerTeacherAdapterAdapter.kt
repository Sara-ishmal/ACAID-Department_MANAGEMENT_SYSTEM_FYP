package com.example.acaid.Adapters

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.example.acaid.Fragments.ChatFragment
import com.example.acaid.Fragments.FragmentTeacherHome
import com.example.acaid.Fragments.ProfileFragment
import com.example.acaid.Fragments.TeacherScheduleFragment

class ViewPagerTeacherAdapterAdapter(fragmentActivity:FragmentActivity):FragmentStateAdapter(fragmentActivity) {
    val fragments= listOf(FragmentTeacherHome(), ChatFragment(),
        TeacherScheduleFragment(), ProfileFragment())
    override fun getItemCount(): Int = fragments.size
    override fun createFragment(position: Int): Fragment {
        return fragments[position]
    }

}

