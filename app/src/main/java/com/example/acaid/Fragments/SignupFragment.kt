package com.example.acaid.Fragments

import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatActivity.MODE_PRIVATE
import com.example.acaid.Ui.MainActivity
import com.example.acaid.databinding.FragmentSignupBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase


class SignupFragment : Fragment() {
    private var _binding: FragmentSignupBinding? = null
    private val binding get() = _binding!!
    private lateinit var firebaseAuth: FirebaseAuth
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View{
        _binding = FragmentSignupBinding.inflate(inflater, container, false)
        val sharedPref = requireActivity().getSharedPreferences("UserData", MODE_PRIVATE)
        val role = sharedPref.getString("user_role", null)
        firebaseAuth = FirebaseAuth.getInstance()

        binding.apply {
            btnRegister.setOnClickListener {
                val username = etUserName.text.toString().trim()
                val email = etEmail.text.toString().trim()
                val password = etPassword.text.toString().trim()

                // Validation
                if (username.isEmpty() || email.isEmpty() || password.isEmpty()) {
                    Toast.makeText(context, "Please fill all fields", Toast.LENGTH_SHORT).show()
                    return@setOnClickListener
                }

                // Firebase sign-up
                firebaseAuth.createUserWithEmailAndPassword(email, password)
                    .addOnCompleteListener { task ->
                        if (task.isSuccessful) {
                            val uid = firebaseAuth.currentUser?.uid
                            val userMap = mapOf(
                                "username" to username,
                                "email" to email,
                                "role" to role
                            )

                            // Save user info to Realtime Database
                            FirebaseDatabase.getInstance().reference
                                .child("users")
                                .child(uid!!)
                                .setValue(userMap)
                                .addOnCompleteListener {
                                    if (it.isSuccessful) {
                                        Toast.makeText(context, "Registered Successfully", Toast.LENGTH_SHORT).show()
                                        val intent = Intent(requireContext(), MainActivity::class.java)
                                        startActivity(intent)
                                        requireActivity().finish()
                                    } else {
                                        Toast.makeText(context, "Failed to save user info", Toast.LENGTH_SHORT).show()
                                    }
                                }
                        } else {
                            Toast.makeText(context, "Signup Failed: ${task.exception?.message}", Toast.LENGTH_LONG).show()
                        }
                    }
            }

            login.setOnClickListener {
                requireActivity().onBackPressed()
            }
        }


        return binding.root
    }
    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }


}