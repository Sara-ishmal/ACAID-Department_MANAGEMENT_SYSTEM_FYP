package com.example.acaid.Ui

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.acaid.databinding.ActivityAdminInfoBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

class AdminInfoActivity : AppCompatActivity() {
    private lateinit var binding: ActivityAdminInfoBinding
    private lateinit var auth: FirebaseAuth
    private lateinit var firestore: FirebaseFirestore

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAdminInfoBinding.inflate(layoutInflater)
        setContentView(binding.root)

        auth = FirebaseAuth.getInstance()
        firestore = FirebaseFirestore.getInstance()

        setupSpinners()

        binding.btnSaveAdmin.setOnClickListener {
            binding.btnSaveAdmin.isEnabled = false
            binding.btnSaveAdmin.text = "Saving..."
            binding.progressBar.visibility = View.VISIBLE
            saveAdminInfo()
        }
    }

    private fun setupSpinners() {
       val roles = arrayOf("Select Role", "Department Administrator", "Examination Administrator", "Academic Coordinator ")

        val roleAdapter = ArrayAdapter(this, android.R.layout.simple_spinner_dropdown_item, roles)

        binding.spinnerRoleTitle.adapter = roleAdapter
    }

    private fun saveAdminInfo() {
        val uid = auth.currentUser?.uid
        if (uid == null) {
            Toast.makeText(this, "User not logged in", Toast.LENGTH_SHORT).show()
            return
        }

        val name = binding.etAdminName.text.toString().trim()

        val role = binding.spinnerRoleTitle.selectedItem.toString()

        if (name.isEmpty() || role == "Select Role") {
            Toast.makeText(this, "Please fill all fields", Toast.LENGTH_SHORT).show()
            binding.btnSaveAdmin.isEnabled = true
            binding.btnSaveAdmin.text = "Save"
            binding.progressBar.visibility = View.GONE
            return
        }

        val adminData = hashMapOf(
            "full name" to name,
            "roleTitle" to role
        )

        firestore.collection("users").document(uid)
            .update(adminData as Map<String, Any>)
            .addOnSuccessListener {
                Toast.makeText(this, "Admin info saved", Toast.LENGTH_SHORT).show()
                binding.btnSaveAdmin.isEnabled = true
                binding.btnSaveAdmin.text = "Save"
                binding.progressBar.visibility = View.GONE
                val sharedPref= getSharedPreferences("UserPrefs", MODE_PRIVATE)
                sharedPref.edit().putBoolean("isProfileComplete", true).apply()

               startActivity(Intent(this, AdminDashboardActivity::class.java))
                finish()
            }
            .addOnFailureListener {
                Toast.makeText(this, "Failed to save info", Toast.LENGTH_SHORT).show()
                binding.btnSaveAdmin.isEnabled = true
                binding.btnSaveAdmin.text = "Save"
                binding.progressBar.visibility = View.GONE
            }
    }
}