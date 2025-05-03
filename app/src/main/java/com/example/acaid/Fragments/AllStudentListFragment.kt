package com.example.acaid.Fragments

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.core.widget.addTextChangedListener
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.acaid.Adapters.AllStudentsAdapter
import com.example.acaid.Models.AllStudentsModel
import com.example.acaid.Models.User
import com.example.acaid.databinding.DialogAddStudentsBinding
import com.example.acaid.databinding.FragmentAllStudentListBinding
import com.google.firebase.firestore.FirebaseFirestore


class AllStudentListFragment : Fragment() {
    private var _binding: FragmentAllStudentListBinding? = null
    private val binding get() = _binding!!
    private var allStudentsList: ArrayList<AllStudentsModel>? = null
    private var filteredStudentsList: MutableList<AllStudentsModel> = mutableListOf()
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View{
        _binding = FragmentAllStudentListBinding.inflate(inflater, container, false)

        binding.AddMoreStudents.setOnClickListener {
            addMoreStudents()
        }
        binding.subtitle.text = arguments?.getString("className")
        allStudentsList = arguments?.getParcelableArrayList("studentsList")
        if (!allStudentsList.isNullOrEmpty()) {
            filteredStudentsList.addAll(allStudentsList!!)
            val adapter = AllStudentsAdapter(filteredStudentsList)
            binding.apply {
                AllStudentsRecyclerView.adapter = adapter
                AllStudentsRecyclerView.layoutManager = LinearLayoutManager(requireContext())
                AllStudentsRecyclerView.visibility = View.VISIBLE
                noStudentInClass.visibility = View.GONE
            }
        } else {
            binding.apply {
                AllStudentsRecyclerView.visibility = View.GONE
                noStudentInClass.visibility = View.VISIBLE
            }
        }
        binding.searchStudents.addTextChangedListener { editable ->
            val searchText = editable.toString().trim().lowercase()

            filteredStudentsList.clear()
            if (searchText.isEmpty()) {
                allStudentsList?.let { filteredStudentsList.addAll(it) }
            } else {
                allStudentsList?.forEach { student ->
                    if (student.studentName.lowercase().contains(searchText) ||
                        student.studentRoll.lowercase().contains(searchText)) {
                        filteredStudentsList.add(student)
                    }
                }
            }

            if (filteredStudentsList.isEmpty()) {
                binding.AllStudentsRecyclerView.visibility = View.GONE
                binding.noStudentInClass.visibility = View.VISIBLE
                binding.noStudentInClass.text = "No matching students found"
            } else {
                binding.AllStudentsRecyclerView.visibility = View.VISIBLE
                binding.noStudentInClass.visibility = View.GONE
                (binding.AllStudentsRecyclerView.adapter as AllStudentsAdapter).updateList(filteredStudentsList)
            }
        }


        return binding.root
    }

    private fun addMoreStudents() {
        val binding1 = DialogAddStudentsBinding.inflate(layoutInflater)
        val dialogView = binding1.root
        val className = arguments?.getString("className") ?: return
        val classId = arguments?.getString("classId") ?: return

        val currentList = (binding.AllStudentsRecyclerView.adapter as? AllStudentsAdapter)?.studentList?.toMutableList()
            ?: mutableListOf()

        AlertDialog.Builder(requireContext())
            .setTitle("Add New Student")
            .setView(dialogView)
            .setPositiveButton("Add") { _, _ ->
                val name = binding1.editStudentName.text.toString().trim()
                val roll = binding1.editStudentRoll.text.toString().trim()
                val email = binding1.editStudentEmail.text.toString().trim()

                if (name.isNotEmpty() && roll.isNotEmpty() && email.isNotEmpty()) {
                    val user = User(className, email, name, "student", roll, name)
                    val db = FirebaseFirestore.getInstance()

                    db.collection("users").add(user)
                        .addOnSuccessListener { docRef ->
                            val userId = docRef.id
                            val student = AllStudentsModel(userId, name, roll, className)
                            currentList.add(student)

                            db.collection("classes").document(classId)
                                .update("students", currentList)
                                .addOnSuccessListener {
                                    Toast.makeText(requireContext(), "Student added", Toast.LENGTH_SHORT).show()
                                    binding.AllStudentsRecyclerView.adapter = AllStudentsAdapter(currentList)
                                    binding.noStudentInClass.visibility = View.GONE
                                    binding.AllStudentsRecyclerView.visibility = View.VISIBLE
                                }
                                .addOnFailureListener {
                                    Toast.makeText(requireContext(), "Failed to add student", Toast.LENGTH_SHORT).show()
                                }
                        }
                        .addOnFailureListener {
                            Toast.makeText(requireContext(), "Failed to create user", Toast.LENGTH_SHORT).show()
                        }
                } else {
                    Toast.makeText(requireContext(), "Please fill all fields", Toast.LENGTH_SHORT).show()
                }
            }
            .setNegativeButton("Cancel", null)
            .show()
    }



}