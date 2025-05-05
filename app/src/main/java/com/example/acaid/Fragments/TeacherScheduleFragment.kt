package com.example.acaid.Fragments

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.core.content.ContextCompat
import com.example.acaid.R
import com.example.acaid.databinding.FragmentTeacherScheduleBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore


class TeacherScheduleFragment : Fragment() {
    private var _binding: FragmentTeacherScheduleBinding? = null
    private val binding get() = _binding!!
    private val db= FirebaseFirestore.getInstance()
    private val auth= FirebaseAuth.getInstance()
    private val classIdMap = mapOf(
        "BSSE Regular 1 (2025-2029) - Semester 1" to "BSR25S1",
        "BSSE Self Support 2 (2023-2027) - Semester 4" to "BSS23S4",
        "BSSE Self Support 2 (2022-2026) - Semester 6" to "BSS22S6",
        "BSSE Regular 1 (2024-2028) - Semester 2" to "BSR24S2",
        "BSSE Self Support 1 (2022-2026) - Semester 6" to "BSS22S6",
        "BSSE Self Support 2 (2024-2028) - Semester 2" to "BSS24S2",
        "BSSE Regular 1 (2022-2026) - Semester 6" to "BSR22S6",
        "BSSE Self Support 1 (2021-2025) - Semester 8" to "BSS21S8",
        "BSSE Regular 1 (2021-2025) - Semester 8" to "BSR21S8",
        "BSSE Self Support 1 (2023-2027) - Semester 4" to "BSS23S4",
        "BSSE Regular 1 (2023-2027) - Semester 4" to "BSR23S4",
        "BSSE Regular 1 (2024-2028) - Semester 3" to "BSR24S3",
        "BSSE Self Support 1 (2024-2028) - Semester 2" to "BSS24S2",
        "MSSE Self Support 1 (2024-2026) - Semester 2" to "MSS24S2",
        "BSSE Self Support 1 (2025-2029) - Semester 1" to "BSS25S1"
    )
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View{
        _binding = FragmentTeacherScheduleBinding.inflate(inflater, container, false)
        activity?.window?.statusBarColor = ContextCompat.getColor(requireContext(), R.color.white)
        activity?.window?.navigationBarColor = ContextCompat.getColor(requireContext(), R.color.white)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val userId = auth.currentUser?.uid ?: return
        db.collection("users").document(userId).get()
            .addOnSuccessListener { document ->
                if (document != null && document.exists()) {
                    val classesTaught = document.get("classesTaught") as? List<String> ?: emptyList()
                    setupSpinner(classesTaught)
                } else {
                    Toast.makeText(requireContext(), "Teacher data not found", Toast.LENGTH_SHORT).show()
                }
            }
            .addOnFailureListener {
                Toast.makeText(requireContext(), "Failed to load teacher info", Toast.LENGTH_SHORT).show()
            }


    }
    private fun setupSpinner(classList: List<String>) {
        val adapter = ArrayAdapter(requireContext(), android.R.layout.simple_spinner_item, classList)
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        binding.spinnerClass.adapter = adapter

        if (classList.isNotEmpty()) {
            val firstClass = classList[0]
            binding.spinnerClass.setSelection(0)
            val classId = classIdMap[firstClass]
            if (classId != null) {
                loadScheduleForClass(classId)
            }
        }

        binding.spinnerClass.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>, view: View?, position: Int, id: Long) {
                val selectedClass = parent.getItemAtPosition(position).toString()
                val classId = classIdMap[selectedClass]
                if (classId != null) {
                    loadScheduleForClass(classId)
                } else {
                    Toast.makeText(requireContext(), "Invalid class selected", Toast.LENGTH_SHORT).show()
                }
            }

            override fun onNothingSelected(parent: AdapterView<*>) {}
        }
    }

    private fun loadScheduleForClass(classId: String) {
        db.collection("classes").document(classId).get()
            .addOnSuccessListener { document ->
                if (document != null && document.exists()) {
                    val scheduleMap = document.get("schedule") as? Map<*, *>

                    binding.apply {
                        tvMonth.text = scheduleMap?.get("month")?.toString() ?: ""
                        tvWeek.text = scheduleMap?.get("week")?.toString() ?: ""
                        tvMonday.text = scheduleMap?.get("monday")?.toString() ?: ""
                        tvTuesday.text = scheduleMap?.get("tuesday")?.toString() ?: ""
                        tvWednesday.text = scheduleMap?.get("wednesday")?.toString() ?: ""
                        tvThursday.text = scheduleMap?.get("thursday")?.toString() ?: ""
                        tvFriday.text = scheduleMap?.get("friday")?.toString() ?: ""
                        tvSaturday.text = scheduleMap?.get("saturday")?.toString() ?: ""
                        tvSunday.text = scheduleMap?.get("sunday")?.toString() ?: ""
                    }
                } else {
                    Toast.makeText(requireContext(), "No schedule found for this class", Toast.LENGTH_SHORT).show()
                }
            }
            .addOnFailureListener { e ->
                Toast.makeText(requireContext(), "Failed to fetch schedule: ${e.message}", Toast.LENGTH_SHORT).show()
            }
    }





}


