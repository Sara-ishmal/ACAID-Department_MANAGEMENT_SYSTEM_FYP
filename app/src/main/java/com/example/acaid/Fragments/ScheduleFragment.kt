package com.example.acaid.Fragments

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.content.ContextCompat
import com.example.acaid.R
import com.example.acaid.databinding.FragmentScheduleBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore


class ScheduleFragment : Fragment() {
    private var _binding: FragmentScheduleBinding? = null
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
        _binding = FragmentScheduleBinding.inflate(inflater, container, false)
        activity?.window?.statusBarColor = ContextCompat.getColor(requireContext(), R.color.white)
        activity?.window?.navigationBarColor = ContextCompat.getColor(requireContext(), R.color.white)
        setTimeTable()
        return binding.root
    }



    private fun setTimeTable() {
        val userId=auth.currentUser?.uid
        db.collection("users").document(userId!!).get().addOnSuccessListener {doc->
            val className=doc.getString("class")
            val classId=classIdMap[className]
            if (classId != null) {
                db.collection("classes").document(classId)
                    .get()
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
        }
    }


