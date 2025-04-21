package com.example.acaid.Fragments

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.example.acaid.databinding.FragmentCreateScheduleBinding
import com.google.firebase.firestore.FirebaseFirestore

class CreateScheduleFragment : Fragment() {

    private var _binding: FragmentCreateScheduleBinding? = null
    private val binding get() = _binding!!


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

    private var selectedClassId: String? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentCreateScheduleBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.apply {
            setupSpinner()
            setupTextWatchers()

            saveButton.setOnClickListener {
                saveSchedule()
            }

            saveButton.visibility = View.GONE
        }
    }





    private fun setupSpinner() {
        val classNames = classIdMap.keys.toList()
        val adapter = ArrayAdapter(requireContext(), android.R.layout.simple_spinner_item, classNames)
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        binding.classSpinner.adapter = adapter

        binding.classSpinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(
                parent: AdapterView<*>?,
                view: View?,
                position: Int,
                id: Long
            ) {
                val selectedClass = classNames[position]
                selectedClassId = classIdMap[selectedClass]
                selectedClassId?.let { classId ->
                    fetchScheduleFromFirestore(classId)
                }
            }

            override fun onNothingSelected(parent: AdapterView<*>?) {
                selectedClassId = null
            }
        }
    }

    private fun setupTextWatchers() = binding.apply {
        val editTexts = listOf(
            etMonth, etWeek, etMonday, etTuesday, etWednesday,
            etThursday, etFriday, etSaturday, etSunday
        )

        val textWatcher = object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                checkIfChangesMade()
            }
            override fun afterTextChanged(s: Editable?) {}
        }

        editTexts.forEach { it.addTextChangedListener(textWatcher) }
    }

    private fun checkIfChangesMade() = binding.apply {
        val isAnyFieldFilled = etMonth.text.isNotEmpty() || etWeek.text.isNotEmpty() ||
                etMonday.text.isNotEmpty() || etTuesday.text.isNotEmpty() ||
                etWednesday.text.isNotEmpty() || etThursday.text.isNotEmpty() ||
                etFriday.text.isNotEmpty() || etSaturday.text.isNotEmpty() || etSunday.text.isNotEmpty()

        saveButton.visibility = if (isAnyFieldFilled) View.VISIBLE else View.GONE
    }

    private fun saveSchedule() = binding.apply {
        val classId = selectedClassId
        if (classId == null) {
            Toast.makeText(requireContext(), "Please select a class", Toast.LENGTH_SHORT).show()
            return@apply
        }

        val schedule = mapOf(
            "month" to etMonth.text.toString().trim(),
            "week" to etWeek.text.toString().trim(),
            "monday" to etMonday.text.toString().trim(),
            "tuesday" to etTuesday.text.toString().trim(),
            "wednesday" to etWednesday.text.toString().trim(),
            "thursday" to etThursday.text.toString().trim(),
            "friday" to etFriday.text.toString().trim(),
            "saturday" to etSaturday.text.toString().trim(),
            "sunday" to etSunday.text.toString().trim()
        )
        val scheduleUpdate = mapOf("schedule" to schedule)

        FirebaseFirestore.getInstance().collection("classes").document(classId)
            .update(scheduleUpdate)
            .addOnSuccessListener {
                Toast.makeText(requireContext(), "Schedule saved", Toast.LENGTH_SHORT).show()
                saveButton.visibility = View.GONE
            }
            .addOnFailureListener {
                Toast.makeText(requireContext(), "Error saving schedule", Toast.LENGTH_SHORT).show()
            }
    }
    private fun fetchScheduleFromFirestore(classId: String) {
        val firestore = FirebaseFirestore.getInstance()
        firestore.collection("classes").document(classId)
            .get()
            .addOnSuccessListener { document ->
                if (document != null && document.exists()) {
                    val scheduleMap = document.get("schedule") as? Map<*, *>

                    binding.apply {
                        etMonth.setText(scheduleMap?.get("month")?.toString() ?: "")
                        etWeek.setText(scheduleMap?.get("week")?.toString() ?: "")
                        etMonday.setText(scheduleMap?.get("monday")?.toString() ?: "")
                        etTuesday.setText(scheduleMap?.get("tuesday")?.toString() ?: "")
                        etWednesday.setText(scheduleMap?.get("wednesday")?.toString() ?: "")
                        etThursday.setText(scheduleMap?.get("thursday")?.toString() ?: "")
                        etFriday.setText(scheduleMap?.get("friday")?.toString() ?: "")
                        etSaturday.setText(scheduleMap?.get("saturday")?.toString() ?: "")
                        etSunday.setText(scheduleMap?.get("sunday")?.toString() ?: "")
                    }
                } else {
                    Toast.makeText(requireContext(), "No schedule found for this class", Toast.LENGTH_SHORT).show()
                }
            }
            .addOnFailureListener { e ->
                Toast.makeText(requireContext(), "Failed to fetch schedule: ${e.message}", Toast.LENGTH_SHORT).show()
            }
    }


    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}