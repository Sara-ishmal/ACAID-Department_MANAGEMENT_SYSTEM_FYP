package com.example.acaid.Ui


import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import androidx.appcompat.app.AppCompatActivity
import com.example.acaid.R
import com.google.firebase.auth.FirebaseAuth

class SplashActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splash)

        Handler(Looper.getMainLooper()).postDelayed({
            val sharedPref = getSharedPreferences("UserPrefs", Context.MODE_PRIVATE)
            val isProfileComplete = sharedPref.getBoolean("isProfileComplete", false)
            val role = sharedPref.getString("userRole", null)
            val currentUser = FirebaseAuth.getInstance().currentUser

            if (currentUser != null) {
                if (isProfileComplete && role != null) {
                    when (role) {
                        "student" -> startActivity(Intent(this, StudentDashboardActivity::class.java))
                        "teacher" -> startActivity(Intent(this, TeacherDashboardActivity::class.java))
                        "admin" -> startActivity(Intent(this, AdminDashboardActivity::class.java))
                        else -> {
                            // Unknown role, send to role selection
                            startActivity(Intent(this, SelectRoleActivity::class.java))
                        }
                    }
                } else {
                    // Logged in but profile not complete
                    startActivity(Intent(this, SelectRoleActivity::class.java))
                }
            } else {
                // Not logged in
                startActivity(Intent(this, LoginActivity::class.java))
            }

            finish()
        }, 3000)
    }
}

