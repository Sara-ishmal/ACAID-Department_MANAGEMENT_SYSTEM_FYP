package com.example.acaid.Fragments

import android.content.Intent
import android.os.Bundle
import android.text.TextUtils
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.acaid.R
import com.example.acaid.Ui.MainActivity
import com.example.acaid.databinding.FragmentLoginBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase

class LoginFragment : Fragment() {
    private var _binding: FragmentLoginBinding? = null
    private val binding get() = _binding!!
    private lateinit var auth: FirebaseAuth
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View{
        _binding = FragmentLoginBinding.inflate(inflater, container, false)
        auth = FirebaseAuth.getInstance()



        binding.apply {

            btnLogin.setOnClickListener {
                val email = etEmail.text.toString().trim()
                val password = etPassword.text.toString().trim()

                if (TextUtils.isEmpty(email) || TextUtils.isEmpty(password)) {
                    Toast.makeText(requireContext(), "Please enter all fields", Toast.LENGTH_SHORT).show()
                } else {
                    auth.signInWithEmailAndPassword(email, password)
                        .addOnCompleteListener { task ->
                            if (task.isSuccessful) {
                                val userId = auth.currentUser?.uid
                                if (userId != null) {
                                    val databaseRef = FirebaseDatabase.getInstance().getReference("users").child(userId)
                                    databaseRef.get().addOnSuccessListener { dataSnapshot ->
                                        val role = dataSnapshot.child("role").value?.toString()
                                        val sharedPref = requireContext().getSharedPreferences("UserData", AppCompatActivity.MODE_PRIVATE)
                                        sharedPref.edit().putString("user_role", role).apply()
                                        if (!role.isNullOrEmpty()) {
                                            Toast.makeText(requireContext(), "Login successful", Toast.LENGTH_SHORT).show()
                                            val intent = Intent(requireContext(), MainActivity::class.java)
                                            startActivity(intent)
                                            requireActivity().finish()
                                        } else {
                                            Toast.makeText(requireContext(), "User role not found", Toast.LENGTH_SHORT).show()
                                        }
                                    }.addOnFailureListener {
                                        Toast.makeText(requireContext(), "Failed to fetch user data", Toast.LENGTH_SHORT).show()
                                    }
                                } else {
                                    Toast.makeText(requireContext(), "User ID not found", Toast.LENGTH_SHORT).show()
                                }
                            }
                            else {
                                Toast.makeText(requireContext(), "Login failed: ${task.exception?.message}", Toast.LENGTH_LONG).show()
                            }
                        }
                }
            }

            btnSignUp.setOnClickListener {
                requireActivity().supportFragmentManager.beginTransaction()
                    .replace(android.R.id.content, SignupFragment())
                    .addToBackStack(null)
                    .commit()
            }
        }

        return binding.root

    }
    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

}