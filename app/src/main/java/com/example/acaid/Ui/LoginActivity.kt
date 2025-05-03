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
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.api.ApiException
import com.google.android.gms.tasks.Task
import com.google.firebase.auth.GoogleAuthProvider

class LoginActivity : AppCompatActivity() {
    private lateinit var binding: ActivityLoginBinding
    private lateinit var auth: FirebaseAuth
    private lateinit var googleSignInClient: GoogleSignInClient
    private val FIREBASE_REQUEST_CODE = 101
    private val Firestore= FirebaseFirestore.getInstance()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)
        auth = FirebaseAuth.getInstance()
        val googleSignInOptions = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestIdToken(getString(R.string.default_web_client_id))
            .requestEmail()
            .build()

        googleSignInClient = GoogleSignIn.getClient(this, googleSignInOptions)

        binding.btnLogin.setOnClickListener {
            loginUser()
        }

        binding.btnSignUp.setOnClickListener {
            startActivity(Intent(this, SignupActivity::class.java))
        }
        binding.signInWithGoogle.setOnClickListener {
            signInWithGoogle()
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
    private fun signInWithGoogle() {
        val signInIntent = googleSignInClient.signInIntent
        startActivityForResult(signInIntent, FIREBASE_REQUEST_CODE)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode == FIREBASE_REQUEST_CODE) {
            val task = GoogleSignIn.getSignedInAccountFromIntent(data)
            try {
                val account = task.getResult(ApiException::class.java)
                firebaseAuthWithGoogle(account)
            } catch (e: ApiException) {
                Toast.makeText(this, "Google Sign-In failed: ${e.message}", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun firebaseAuthWithGoogle(account: GoogleSignInAccount?) {
        if (account != null) {
            val credential = GoogleAuthProvider.getCredential(account.idToken, null)
            auth.signInWithCredential(credential)
                .addOnCompleteListener { task ->
                    if (task.isSuccessful) {
                        Toast.makeText(this, "Google Sign-In successful", Toast.LENGTH_SHORT).show()
                        val userId = auth.currentUser?.uid
                        if (userId != null) {
                            Firestore.collection("users")
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
                        Toast.makeText(this, "Authentication failed.", Toast.LENGTH_SHORT).show()
                    }
                }
        }
    }

}