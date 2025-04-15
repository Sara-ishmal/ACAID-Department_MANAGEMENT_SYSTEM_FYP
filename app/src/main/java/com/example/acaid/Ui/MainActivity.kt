package com.example.acaid.Ui

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.example.acaid.Fragments.AdminDashboardFragment
import com.example.acaid.Fragments.StudentDashboardFragment
import com.example.acaid.Fragments.TeacherDashboardFragment
import com.example.acaid.databinding.ActivityMainBinding


class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        val sharedPref = getSharedPreferences("UserData", MODE_PRIVATE)
        val role = sharedPref.getString("user_role", null)
        val fragment = when (role) {
            "student" -> StudentDashboardFragment()
            "teacher" -> TeacherDashboardFragment()
            "admin" -> AdminDashboardFragment()
            else -> null
        }

        fragment?.let {
            supportFragmentManager.beginTransaction()
                .replace(android.R.id.content, it)
                .commit()
        }

    }

  }