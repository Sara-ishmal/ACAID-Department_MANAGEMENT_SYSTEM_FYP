package com.example.acaid.Ui

import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.view.View
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.acaid.Adapters.ViewPagerAdapter
import com.example.acaid.Fragments.ChatFragment
import com.example.acaid.Fragments.HomeFragment
import com.example.acaid.Fragments.ProfileFragment
import com.example.acaid.Fragments.ResourcesFragment
import com.example.acaid.Fragments.ScheduleFragment
import com.example.acaid.R
import com.example.acaid.databinding.ActivityStudentDashboardBinding
import com.google.android.material.tabs.TabLayout
import com.google.android.material.tabs.TabLayoutMediator

class StudentDashboardActivity : AppCompatActivity() {
    private lateinit var binding: ActivityStudentDashboardBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding= ActivityStudentDashboardBinding.inflate(layoutInflater)
        setContentView(binding.root)
        setDashboard()




    }

    private fun setDashboard() {
        window.navigationBarColor = ContextCompat.getColor(this, R.color.white)

        val viewPager=binding.myViewPager
        viewPager.adapter= ViewPagerAdapter(this)

        val tabLayout=binding.myTabLayout
        val tabLayoutMediator = TabLayoutMediator(tabLayout, viewPager) { tab, position ->
            when (position) {
                0 -> {
                    tab.setIcon(R.drawable.icons8_home)
                }
                1 -> {
                    tab.setIcon(R.drawable.book)
                }
                2 -> {
                    tab.setIcon(R.drawable.icons8_chat_100__1_)
                }
                3 -> {
                    tab.setIcon(R.drawable.timetable)
                }
                4 -> {
                    tab.setIcon(R.drawable.profile)
                }
            }
        }
        tabLayoutMediator.attach()
    }


}