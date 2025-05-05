package com.example.acaid.Fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import com.example.acaid.Models.Assignment
import com.example.acaid.R
import com.example.acaid.databinding.FragmentTeacherAssignmentBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

class TeacherAssignmentFragment : Fragment() {

    private var _binding: FragmentTeacherAssignmentBinding? = null
    private val binding get() = _binding!!
    private val db = FirebaseFirestore.getInstance()
    private val userId = FirebaseAuth.getInstance().currentUser?.uid
    private var selectedClass: String? = null
    private var selectedSubject: String? = null
    private var fullName: String = ""

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentTeacherAssignmentBinding.inflate(inflater, container, false)
        activity?.window?.statusBarColor = ContextCompat.getColor(requireContext(), R.color.white)
        activity?.window?.navigationBarColor = ContextCompat.getColor(requireContext(), R.color.white)

        loadUserData()

        binding.btnUploadAssignment.setOnClickListener {
            uploadAssignment()
        }

        return binding.root
    }

    private fun loadUserData() {
        userId?.let { uid ->
            db.collection("users").document(uid).get().addOnSuccessListener { doc ->
                if (doc.exists()) {
                    fullName = doc.getString("fullName") ?: ""
                    val classesTaught = doc.get("classesTaught") as? List<String> ?: emptyList()
                    val subjectsTaught = doc.get("subjectsTaught") as? List<String> ?: emptyList()

                    setClassSpinner(classesTaught)
                    setSubjectSpinner(subjectsTaught)
                }
            }
        }
    }

    private fun setClassSpinner(classes: List<String>) {
        val adapter = ArrayAdapter(requireContext(), android.R.layout.simple_spinner_item, classes)
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        binding.spinnerClass.adapter = adapter

        binding.spinnerClass.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>, view: View?, position: Int, id: Long) {
                selectedClass = parent.getItemAtPosition(position) as String
            }

            override fun onNothingSelected(parent: AdapterView<*>) {
                selectedClass = null
            }
        }
    }

    private fun setSubjectSpinner(subjects: List<String>) {
        val adapter = ArrayAdapter(requireContext(), android.R.layout.simple_spinner_item, subjects)
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        binding.spinnerSubject.adapter = adapter

        binding.spinnerSubject.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>, view: View?, position: Int, id: Long) {
                selectedSubject = parent.getItemAtPosition(position) as String
            }

            override fun onNothingSelected(parent: AdapterView<*>) {
                selectedSubject = null
            }
        }
    }

    private fun uploadAssignment() {
        val title = binding.etAssignmentTitle.text.toString().trim()
        val description = binding.etAssignmentDescription.text.toString().trim()
        val dueDate = binding.etDeadline.text.toString().trim()  // Use date picker ideally

        if (title.isEmpty() || description.isEmpty() || dueDate.isEmpty() || selectedClass == null || selectedSubject == null) {
            Toast.makeText(requireContext(), "Please fill all fields", Toast.LENGTH_SHORT).show()
            return
        }

        val assignment = Assignment(
            title = title,
            description = description,
            deadline = dueDate,
            className = selectedClass!!,
            subject = selectedSubject!!,
            teacherId = userId ?: "",
            teacherName = fullName,
            timestamp = System.currentTimeMillis()
        )

        db.collection("assignments")
            .add(assignment)
            .addOnSuccessListener {
                Toast.makeText(requireContext(), "Assignment uploaded!", Toast.LENGTH_SHORT).show()
                binding.etAssignmentTitle.text.clear()
                binding.etAssignmentDescription.text.clear()
                binding.etDeadline.text.clear()
            }
            .addOnFailureListener { e ->
                Toast.makeText(requireContext(), "Upload failed: $e", Toast.LENGTH_SHORT).show()
            }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
