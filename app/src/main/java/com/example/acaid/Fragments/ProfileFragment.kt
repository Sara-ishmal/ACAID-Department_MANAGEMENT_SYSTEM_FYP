package com.example.acaid.Fragments

import android.app.AlertDialog
import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import com.example.acaid.R
import com.example.acaid.Ui.LoginActivity
import com.example.acaid.databinding.FragmentProfileBinding
import com.google.firebase.auth.FirebaseAuth


class ProfileFragment : Fragment() {
    private var _binding: FragmentProfileBinding? = null
    private val binding get() = _binding!!
    private val auth= FirebaseAuth.getInstance()
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View{
        _binding = FragmentProfileBinding.inflate(inflater, container, false)
        binding.apply {
            Logout.setOnClickListener {
              logout()
            }
        }
        activity?.window?.statusBarColor = ContextCompat.getColor(requireContext(), R.color.white)
        activity?.window?.navigationBarColor = ContextCompat.getColor(requireContext(), R.color.white)


        return binding.root
    }

    private fun logout() {
        val builder= AlertDialog.Builder(requireContext())
        builder.setTitle("Logout")
        builder.setMessage("Are you sure you want to logout?")
        builder.setPositiveButton("Yes"){dialog,_->
            dialog.dismiss()
            auth.signOut()
            startActivity(Intent(requireActivity(), LoginActivity()::class.java))
            requireActivity().finish()
        }
        builder.setNegativeButton("No"){dialog,_ ->
            dialog.dismiss()
        }
        builder.create().show()
    }

}