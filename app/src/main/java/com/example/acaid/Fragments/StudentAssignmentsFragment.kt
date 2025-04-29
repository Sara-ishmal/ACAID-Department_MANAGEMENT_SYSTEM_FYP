package com.example.acaid.Fragments

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.example.acaid.databinding.FragmentStudentAssignmentsBinding

class StudentAssignmentsFragment : Fragment() {
    private var _binding: FragmentStudentAssignmentsBinding? = null
    private val binding get() = _binding!!
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View{
        _binding=FragmentStudentAssignmentsBinding.inflate(inflater,container,false)
        return binding.root
    }


}