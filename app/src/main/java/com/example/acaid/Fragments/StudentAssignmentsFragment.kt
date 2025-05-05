package com.example.acaid.Fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.acaid.Adapters.StudentAssignmentAdapter
import com.example.acaid.Models.Assignment
import com.example.acaid.R
import com.example.acaid.databinding.FragmentStudentAssignmentsBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query

class StudentAssignmentsFragment : Fragment() {

    private var _binding: FragmentStudentAssignmentsBinding? = null
    private val binding get() = _binding!!
    private val db = FirebaseFirestore.getInstance()
    private val userId = FirebaseAuth.getInstance().currentUser?.uid
    private lateinit var assignmentAdapter: StudentAssignmentAdapter
    private val assignmentsList = mutableListOf<Assignment>()
    private var studentClass: String = ""

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentStudentAssignmentsBinding.inflate(inflater, container, false)
        activity?.window?.statusBarColor = ContextCompat.getColor(requireContext(), R.color.white)
        activity?.window?.navigationBarColor = ContextCompat.getColor(requireContext(), R.color.white)

        setupRecyclerView()
        loadStudentData()

        return binding.root
    }

    private fun setupRecyclerView() {
        assignmentAdapter = StudentAssignmentAdapter(assignmentsList)
        binding.StudentAssignmentRecyclerView.apply {
            layoutManager = LinearLayoutManager(context)
            adapter = assignmentAdapter
        }
    }

    private fun loadStudentData() {
        userId?.let { uid ->
            db.collection("users").document(uid).get().addOnSuccessListener { doc ->
                if (doc.exists()) {
                    studentClass = doc.getString("class") ?: ""
                    if (studentClass.isNotEmpty()) {
                        loadAssignments(studentClass)
                    } else {
                        Toast.makeText(context, "Class information not found", Toast.LENGTH_SHORT).show()
                    }
                }
            }.addOnFailureListener { e ->
                Toast.makeText(context, "Error loading student data: ${e.message}", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun loadAssignments(className: String) {
        db.collection("assignments")
            .whereEqualTo("className", className)
            .orderBy("timestamp", Query.Direction.DESCENDING)
            .addSnapshotListener { snapshot, e ->
                if (e != null) {
                    return@addSnapshotListener
                }

                assignmentsList.clear()
                snapshot?.forEach { document ->
                    val assignment = document.toObject(Assignment::class.java)
                    assignmentsList.add(assignment)
                }

                if (assignmentsList.isEmpty()) {
                    binding.noAssignmentContainer.visibility = View.VISIBLE
                    binding.StudentAssignmentRecyclerView.visibility = View.GONE
                    Toast.makeText(context, "No assignments found", Toast.LENGTH_SHORT).show()
                } else {
                    binding.noAssignmentContainer.visibility = View.GONE
                    binding.StudentAssignmentRecyclerView.visibility = View.VISIBLE
                }


                assignmentAdapter.notifyDataSetChanged()
            }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
