package com.example.acaid.Fragments

import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.example.acaid.R
import com.example.acaid.Ui.MainActivity
import com.example.acaid.databinding.FragmentSignupBinding


class SignupFragment : Fragment() {
    private var _binding: FragmentSignupBinding? = null
    private val binding get() = _binding!!
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View{
        _binding = FragmentSignupBinding.inflate(inflater, container, false)

        binding.apply {
            val role = arguments?.getString("user_role")
            btnRegister.setOnClickListener {
                // After successful registration
                val intent = Intent(requireContext(), MainActivity::class.java)
                intent.putExtra("user_role", role)
                startActivity(intent)
                requireActivity().finish()
            }
            login.setOnClickListener {
                requireActivity().onBackPressed()
            }
        }


        return binding.root
    }


}