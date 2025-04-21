package com.example.acaid.Ui

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.acaid.databinding.ActivitySelectRoleBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore


class SelectRoleActivity : AppCompatActivity() {
    private lateinit var binding: ActivitySelectRoleBinding
    private lateinit var auth: FirebaseAuth
    private lateinit var firestore: FirebaseFirestore
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySelectRoleBinding.inflate(layoutInflater)
        setContentView(binding.root)
        binding.apply {
            auth = FirebaseAuth.getInstance()
            firestore = FirebaseFirestore.getInstance()

            student.setOnClickListener { updateRoleAndProceed("student") }
            teacher.setOnClickListener { updateRoleAndProceed("teacher") }
            admin.setOnClickListener { updateRoleAndProceed("admin") }
        }



    }

    private fun updateRoleAndProceed(role: String) {
        val uid = auth.currentUser?.uid
        if (uid != null) {
            firestore.collection("users")
                .document(uid)
                .update("role", role)
                .addOnSuccessListener {
                    val sharedPref=getSharedPreferences("UserPrefs", MODE_PRIVATE)
                    sharedPref.edit().putString("userRole", role).apply()
                    Toast.makeText(this, "$role selected", Toast.LENGTH_SHORT).show()
                    when (role) {
                        "student" -> startActivity(Intent(this, StudentInfoActivity::class.java))
                        "teacher" -> startActivity(Intent(this, TeacherInfoActivity::class.java))
                        "admin" -> startActivity(Intent(this, AdminInfoActivity::class.java))
                    }

                    finish()
                }
                .addOnFailureListener {
                    Toast.makeText(this, "Failed to save role", Toast.LENGTH_SHORT).show()
                }
        }


    }
}