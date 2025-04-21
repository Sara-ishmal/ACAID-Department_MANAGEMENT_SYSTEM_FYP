package com.example.acaid.Fragments

import android.R
import android.content.Intent
import android.os.Bundle
import android.util.Base64
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.acaid.databinding.FragmentUploadResourcesBinding
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore


class UploadResourcesFragment : Fragment() {
    private var _binding: FragmentUploadResourcesBinding? = null
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



    private var selectedFileBytes: ByteArray? = null
    private var selectedMimeType: String = "*/*"
    private var selectedResourceType: String = ""

    private val firestore = FirebaseFirestore.getInstance()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)


        setupSpinners()
       binding.apply {
        btnSelectResource.setOnClickListener {
            when (spinnerResourceType.selectedItem.toString()) {
                "PDF (.pdf)" -> selectedMimeType = "application/pdf"
                "PowerPoint (.ppt/.pptx)" -> selectedMimeType = "application/vnd.ms-powerpoint"
                "Word Document (.doc/.docx)" -> selectedMimeType = "application/msword"
                "Image (.jpg/.png)" -> selectedMimeType = "image/*"
                "Text File (.txt)" -> selectedMimeType = "text/plain"
                "Any File" -> selectedMimeType = "*/*"
            }

            selectedResourceType = spinnerResourceType.selectedItem.toString()
            selectFile()
        }

        btnUploadResource.setOnClickListener {
            uploadToFirestore()

        }
    }}

    private fun setupSpinners() {
        val classNames = classIdMap.keys.toList()
        binding.spinnerClasses.adapter = ArrayAdapter(requireContext(), R.layout.simple_spinner_dropdown_item, classNames)
        val resourceTypes = listOf(
            "PDF (.pdf)",
            "PowerPoint (.ppt/.pptx)",
            "Word Document (.doc/.docx)",
            "Image (.jpg/.png)",
            "Text File (.txt)",
            "Any File"
        )

        binding.spinnerResourceType.adapter = ArrayAdapter(requireContext(), R.layout.simple_spinner_dropdown_item, resourceTypes)
    }

    private fun selectFile() {
        val selectedType = binding.spinnerResourceType.selectedItem.toString()
        val intent = Intent(Intent.ACTION_GET_CONTENT)
        intent.addCategory(Intent.CATEGORY_OPENABLE)

        val mimeTypes = when (selectedType) {
            "PDF (.pdf)" -> arrayOf("application/pdf")
            "PowerPoint (.ppt/.pptx)" -> arrayOf(
                "application/vnd.ms-powerpoint",
                "application/vnd.openxmlformats-officedocument.presentationml.presentation"
            )
            "Word Document (.doc/.docx)" -> arrayOf(
                "application/msword",
                "application/vnd.openxmlformats-officedocument.wordprocessingml.document"
            )
            "Image (.jpg/.png)" -> arrayOf("image/*")
            "Text File (.txt)" -> arrayOf("text/plain")
            "Any File" -> arrayOf("*/*")
            else -> arrayOf("*/*")
        }

        intent.type = if (mimeTypes.size == 1) mimeTypes[0] else "*/*"
        if (mimeTypes.size > 1 || mimeTypes[0] != "*/*") {
            intent.putExtra(Intent.EXTRA_MIME_TYPES, mimeTypes)
        }

        startActivityForResult(Intent.createChooser(intent, "Select File"), 1001)
    }


    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == 1001 && resultCode == AppCompatActivity.RESULT_OK) {
            val uri = data?.data
            if (uri != null) {
                selectedFileBytes = requireContext().contentResolver.openInputStream(uri)?.readBytes()

                val fileName = getFileNameFromUri(uri)
                binding.tvSelectedFile.text = "Selected: $fileName"

                Toast.makeText(requireContext(), "File selected", Toast.LENGTH_SHORT).show()
            }
        }
    }
    private fun getFileNameFromUri(uri: android.net.Uri): String {
        val cursor = requireContext().contentResolver.query(uri, null, null, null, null)
        return cursor?.use {
            val nameIndex = it.getColumnIndex(android.provider.OpenableColumns.DISPLAY_NAME)
            it.moveToFirst()
            it.getString(nameIndex)
        } ?: "Unknown file"
    }



    private fun uploadToFirestore() {
        val title = binding.etResourceTitle.text.toString().trim()
        val selectedClass = binding.spinnerClasses.selectedItem.toString()
        val classId = classIdMap[selectedClass]

        if (title.isEmpty() || selectedFileBytes == null || classId == null) {
            Toast.makeText(requireContext(), "Please fill all fields and select a file", Toast.LENGTH_SHORT).show()
            return
        }else{
            binding.progressBar.visibility = View.VISIBLE
            binding.btnUploadResource.isEnabled = false
            binding.btnUploadResource.text = "Uploading..."
            val encodedFile = Base64.encodeToString(selectedFileBytes, Base64.DEFAULT)
            val resource = hashMapOf(
                "title" to title,
                "type" to selectedResourceType,
                "fileBase64" to encodedFile,
                "timestamp" to FieldValue.serverTimestamp()
            )

            firestore.collection("classes")
                .document(classId)
                .collection("resources")
                .add(resource)
                .addOnSuccessListener {
                    Toast.makeText(requireContext(), "Resource uploaded successfully", Toast.LENGTH_SHORT).show()
                    binding.etResourceTitle.text.clear()
                    selectedFileBytes = null
                    binding.progressBar.visibility = View.GONE
                    binding.btnUploadResource.isEnabled = true
                    binding.btnUploadResource.text = "Upload"
                    binding.tvSelectedFile.text = "No file selected"
                }
                .addOnFailureListener {
                    binding.progressBar.visibility = View.GONE
                    binding.btnUploadResource.isEnabled = true
                    binding.btnUploadResource.text = "Upload"
                    binding.tvSelectedFile.text = "No file selected"
                    selectedFileBytes = null
                    Toast.makeText(requireContext(), "Upload failed", Toast.LENGTH_LONG).show()
                }
        }


    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View{
        _binding = FragmentUploadResourcesBinding.inflate(inflater, container, false)
        return binding.root
    }

}