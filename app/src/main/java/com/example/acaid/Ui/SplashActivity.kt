package com.example.acaid.Ui


import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import androidx.appcompat.app.AppCompatActivity
import com.example.acaid.R
import com.google.firebase.auth.FirebaseAuth

class SplashActivity:AppCompatActivity(){
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splash)
        Handler(Looper.getMainLooper()).postDelayed({
            val currentUser = FirebaseAuth.getInstance().currentUser
            if (currentUser != null) {
                startActivity(Intent(this, MainActivity::class.java))
            } else {
                startActivity(Intent(this, SelectRoleActivity::class.java))
            }
            finish()
        }, 3000)

    }}
