package com.example.acaid.Fragments

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.acaid.Adapters.AllClassesAdapter
import com.example.acaid.Models.AllClassesModel
import com.example.acaid.Models.AllStudentsModel
import com.example.acaid.R
import com.example.acaid.databinding.DialogAddClassBinding
import com.example.acaid.databinding.FragmentAllClassesBinding
import com.google.firebase.firestore.FirebaseFirestore


class AllClassesFragment : Fragment(), AllClassesAdapter.OnClassClickListener {
    private var _binding: FragmentAllClassesBinding? = null
    private val binding get() = _binding!!
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val classList = mutableListOf(AllClassesModel("BSR25S1", "BSSE Regular 1 (2025-2029) - Semester 1", listOf()),
            AllClassesModel("BSS23S4", "BSSE Self Support 2 (2023-2027) - Semester 4", listOf()),
            AllClassesModel("BSS22S6", "BSSE Self Support 2 (2022-2026) - Semester 6", listOf()),
            AllClassesModel("BSR24S2", "BSSE Regular 1 (2024-2028) - Semester 2", listOf()),
            AllClassesModel("BSS22S6", "BSSE Self Support 1 (2022-2026) - Semester 6", listOf()),
            AllClassesModel("BSS24S2", "BSSE Self Support 2 (2024-2028) - Semester 2", listOf()),
            AllClassesModel("BSR22S6", "BSSE Regular 1 (2022-2026) - Semester 6", listOf()),
            AllClassesModel("BSS21S8", "BSSE Self Support 1 (2021-2025) - Semester 8", listOf()),
            AllClassesModel("BSR21S8", "BSSE Regular 1 (2021-2025) - Semester 8", listOf()),
            AllClassesModel("BSS23S4", "BSSE Self Support 1 (2023-2027) - Semester 4", listOf()),
            AllClassesModel("BSR23S4", "BSSE Regular 1 (2023-2027) - Semester 4", listOf()),
            AllClassesModel("BSR24S3", "BSSE Regular 1 (2024-2028) - Semester 3", listOf()),
            AllClassesModel("BSS24S2", "BSSE Self Support 1 (2024-2028) - Semester 2", listOf()),
            AllClassesModel("MSS24S2", "MSSE Self Support 1 (2024-2026) - Semester 2", listOf()),
            AllClassesModel("BSS25S1", "BSSE Self Support 1 (2025-2029) - Semester 1", listOf())
        )

        val adapter = AllClassesAdapter(classList, this)
        binding.AllClassesRecyclerView.adapter = adapter
        storeClassesInFirebase(classList)
        binding.AddMoreClasses.setOnClickListener {
            addMoreClasses()
        }
    }
    private fun addMoreClasses() {
        val binding1= DialogAddClassBinding.inflate(layoutInflater)
        val dialogView = binding1.root

        val dialogBuilder = AlertDialog.Builder(requireContext())
            .setTitle("Add New Class")
            .setView(dialogView)
            .setPositiveButton("Add") { _, _ ->
                val classId = binding1.editClassId.text.toString().trim()
                val className = binding1.editClassName.text.toString().trim()

                if (classId.isNotEmpty() && className.isNotEmpty()) {
                    val newClass = AllClassesModel(classId, className, listOf())

                    val adapter = binding.AllClassesRecyclerView.adapter as AllClassesAdapter
                    adapter.list.add(newClass)
                    adapter.notifyItemInserted(adapter.list.size - 1)

                    val db = FirebaseFirestore.getInstance()
                    db.collection("classes").document(classId).set(mapOf(
                        "classId" to classId,
                        "className" to className,
                        "students" to listOf<Any>()
                    )).addOnSuccessListener {
                        Log.d("AllClassesFragment", "Class added successfully")
                    }.addOnFailureListener {
                        Log.e("AllClassesFragment", "Failed to add class", it)
                    }
                } else {
                    Toast.makeText(requireContext(), "Please fill in both fields", Toast.LENGTH_SHORT).show()
                }
            }
            .setNegativeButton("Cancel", null)

        dialogBuilder.show()
    }




    private fun storeClassesInFirebase(classList: MutableList<AllClassesModel>) {
        val db = FirebaseFirestore.getInstance()

        for (classItem in classList) {
            val classRef = db.collection("classes").document(classItem.classId)

            classRef.get().addOnSuccessListener { document ->
                if (!document.exists()) {
                    classRef.set(mapOf(
                        "classId" to classItem.classId,
                        "className" to classItem.className,
                        "students" to listOf<Any>()
                    ))
                }
            }
        }
    }



    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View{
        _binding = FragmentAllClassesBinding.inflate(inflater, container, false)
        binding.AllClassesRecyclerView.layoutManager = LinearLayoutManager(requireContext())

        return binding.root
    }


    override fun onViewStudentsClicked(classId: String) {
        val db = FirebaseFirestore.getInstance()
        db.collection("classes")
            .document(classId)
            .get()
            .addOnSuccessListener { document ->
                val studentsList = mutableListOf<AllStudentsModel>()
                val students = document.get("students") as? List<Map<String, Any>>

                if (students != null) {
                    for (studentMap in students) {
                        val student = AllStudentsModel(
                            studentId = studentMap["studentId"] as? String ?: "",
                            studentName = studentMap["studentName"] as? String ?: "",
                            studentRoll = studentMap["studentRoll"] as? String ?: "",
                            studentClass = studentMap["studentClass"] as? String ?: ""
                        )
                        studentsList.add(student)
                    }
                }

                val bundle = Bundle()
                val fragment = AllStudentListFragment()
                bundle.putString("className", document.getString("className"))
                bundle.putString("classId", document.getString("classId"))
                bundle.putParcelableArrayList("studentsList", ArrayList(studentsList))
                fragment.arguments = bundle

                requireActivity().supportFragmentManager.beginTransaction()
                    .replace(android.R.id.content, fragment)
                    .addToBackStack(null)
                    .commit()
            }
            .addOnFailureListener { e ->
                Log.e("AllClassesFragment", "Failed to fetch students", e)
            }
    }





}