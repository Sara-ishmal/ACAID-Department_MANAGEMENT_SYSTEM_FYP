package com.example.acaid.Ui

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.example.acaid.Fragments.LoginFragment
import com.example.acaid.databinding.ActivitySelectRoleBinding


class SelectRoleActivity : AppCompatActivity() {
    private lateinit var binding: ActivitySelectRoleBinding
    var selectedRole: String? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySelectRoleBinding.inflate(layoutInflater)
        setContentView(binding.root)
        binding.apply {
           student.setOnClickListener {
                selectedRole = "student"
                openLoginFragment()
            }

           teacher.setOnClickListener {
                selectedRole = "teacher"
                openLoginFragment()
            }

           admin.setOnClickListener {
                selectedRole = "admin"
                openLoginFragment()
            }
        }



    }

    private fun openLoginFragment() {
        val fragment = LoginFragment()
        val bundle = Bundle()
        bundle.putString("user_role", selectedRole)
        fragment.arguments = bundle

        supportFragmentManager.beginTransaction()
            .replace(android.R.id.content, fragment)
            .addToBackStack(null)
            .commit()
    }

}