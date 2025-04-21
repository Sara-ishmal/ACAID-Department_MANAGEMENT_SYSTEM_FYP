package com.example.acaid.Fragments

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.Toast
import com.example.acaid.databinding.FragmentNoticeBinding
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore


class NoticeFragment : Fragment() {
    private var _binding: FragmentNoticeBinding? = null
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


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val classNames = classIdMap.keys.toList()
        val adapter = ArrayAdapter(requireContext(), android.R.layout.simple_spinner_item, classNames)
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        binding.apply {
            spinnerClasses.adapter = adapter
            btnUploadNotice.setOnClickListener {
                val selectedClassName = spinnerClasses.selectedItem.toString()
                val classId = classIdMap[selectedClassName]
                val noticeText = etNotice.text.toString().trim()

                if (classId.isNullOrEmpty() || noticeText.isEmpty()) {
                    Toast.makeText(
                        requireContext(),
                        "Please select a class and enter notice",
                        Toast.LENGTH_SHORT
                    ).show()
                    return@setOnClickListener
                }

                uploadNoticeToFirestore(classId, noticeText)
            }
        }
    }

    private fun uploadNoticeToFirestore(classId: String, noticeText: String) {
        val firestore = FirebaseFirestore.getInstance()
        val noticeData = mapOf(
            "notice" to noticeText,
            "timestamp" to FieldValue.serverTimestamp()
        )

        firestore.collection("classes")
            .document(classId)
            .collection("notices")
            .add(noticeData)
            .addOnSuccessListener {
                Toast.makeText(requireContext(), "Notice uploaded successfully", Toast.LENGTH_SHORT).show()
                binding.etNotice.text.clear()
            }
            .addOnFailureListener { e ->
                Toast.makeText(requireContext(), "Error uploading notice: ${e.message}", Toast.LENGTH_SHORT).show()
            }
    }


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentNoticeBinding.inflate(inflater, container, false)
        return binding.root
    }

}