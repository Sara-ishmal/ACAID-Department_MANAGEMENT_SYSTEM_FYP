package com.example.acaid.Ui

import android.annotation.SuppressLint
import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.acaid.Adapters.ViewPagerAdapter
import com.example.acaid.Adapters.ViewPagerTeacherAdapterAdapter
import com.example.acaid.R
import com.example.acaid.databinding.ActivityTeacherDashboardBinding
import com.google.android.material.tabs.TabLayoutMediator

class TeacherDashboardActivity : AppCompatActivity() {

    private lateinit var binding: ActivityTeacherDashboardBinding
    @SuppressLint("SuspiciousIndentation")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding=ActivityTeacherDashboardBinding.inflate(layoutInflater)
        setContentView(binding.root)
        setDashboard()
    }


    private fun setDashboard() {
        window.navigationBarColor = ContextCompat.getColor(this, R.color.white)

        val viewPager=binding.myViewPager
        viewPager.offscreenPageLimit = 5
        viewPager.adapter= ViewPagerTeacherAdapterAdapter(this)

        val tabLayout=binding.myTabLayout
        val tabLayoutMediator = TabLayoutMediator(tabLayout, viewPager) { tab, position ->
            when (position) {
                0 -> {
                    tab.setIcon(R.drawable.icons8_home)
                }
                1 -> {
                    tab.setIcon(R.drawable.icons8_chat_100__1_)
                }
                2 -> {
                    tab.setIcon(R.drawable.timetable)
                }
                3 -> {
                    tab.setIcon(R.drawable.profile)
                }
            }
        }
        tabLayoutMediator.attach()
    }
}