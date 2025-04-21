package com.example.acaid.Ui

import android.content.Intent
import android.os.Bundle
import android.util.Patterns
import android.view.View
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.acaid.R
import com.example.acaid.databinding.ActivityLoginBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

class LoginActivity : AppCompatActivity() {
    private lateinit var binding: ActivityLoginBinding
    private lateinit var auth: FirebaseAuth
    private val Firestore= FirebaseFirestore.getInstance()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)
        auth = FirebaseAuth.getInstance()

        binding.btnLogin.setOnClickListener {
            loginUser()
        }

        binding.btnSignUp.setOnClickListener {
            startActivity(Intent(this, SignupActivity::class.java))
        }


    }

    private fun loginUser() {
        val email = binding.etEmail.text.toString().trim()
        val password = binding.etPassword.text.toString().trim()

        if (email.isEmpty() || !Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            binding.etEmail.error = "Enter valid email"
            return
        }

        if (password.isEmpty() || password.length < 6) {
            binding.etPassword.error = "Enter valid password"
            return
        }


        binding.progressBar.visibility= View.VISIBLE
        binding.btnLogin.isEnabled = false

        auth.signInWithEmailAndPassword(email, password)
            .addOnCompleteListener { task ->
                binding.progressBar.visibility = View.GONE
                binding.btnLogin.isEnabled = true

                if (task.isSuccessful) {
                    Toast.makeText(this, "Login successful", Toast.LENGTH_SHORT).show()

                        val userId = auth.currentUser?.uid
                        if (userId != null) {
                            FirebaseFirestore.getInstance().collection("users")
                                .document(userId)
                                .get()
                                .addOnSuccessListener { document ->
                                    if (document.exists()) {
                                        val role = document.getString("role")
                                        if (!role.isNullOrEmpty()) {
                                            val sharedPref = getSharedPreferences("UserPrefs", MODE_PRIVATE)
                                            sharedPref.edit().putString("userRole", role).apply()
                                            sharedPref.edit().putBoolean("isProfileComplete", true).apply()
                                            when (role) {
                                                "admin" -> startActivity(Intent(this, AdminDashboardActivity::class.java))
                                                "teacher" -> startActivity(Intent(this, TeacherDashboardActivity::class.java))
                                                "student" -> startActivity(Intent(this, StudentDashboardActivity::class.java))
                                                else -> {
                                                    Toast.makeText(this, "Invalid role", Toast.LENGTH_SHORT).show()
                                                }

                                            }
                                            finish()

                                        }
                                    }
                                }
                                .addOnFailureListener {
                                    Toast.makeText(this, "Failed to fetch role: ${it.message}", Toast.LENGTH_SHORT).show()
                                }
                        }


                } else {
                    Toast.makeText(this, "Login failed: ${task.exception?.message}", Toast.LENGTH_SHORT).show()
                }
            }
    }

}