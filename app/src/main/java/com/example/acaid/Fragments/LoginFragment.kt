package com.example.acaid.Fragments

import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.example.acaid.R
import com.example.acaid.Ui.MainActivity
import com.example.acaid.databinding.FragmentLoginBinding

class LoginFragment : Fragment() {
    private var _binding: FragmentLoginBinding? = null
    private val binding get() = _binding!!
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View{
        _binding = FragmentLoginBinding.inflate(inflater, container, false)

       binding.apply {
           val role = arguments?.getString("user_role")
           btnLogin.setOnClickListener {
               // After validating login
               val intent = Intent(requireContext(), MainActivity::class.java)
               intent.putExtra("user_role", role)
               startActivity(intent)
               requireActivity().finish()
           }
           btnSignUp.setOnClickListener {
               val signupFragment = SignupFragment()
               val bundle = Bundle()
               bundle.putString("user_role", role)
               signupFragment.arguments = bundle

               requireActivity().supportFragmentManager.beginTransaction()
                   .replace(android.R.id.content, signupFragment)
                   .addToBackStack(null)
                   .commit()
           }

       }

        return binding.root

    }

}