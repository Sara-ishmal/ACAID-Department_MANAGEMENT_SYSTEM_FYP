package com.example.acaid.Ui

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.acaid.databinding.ActivityStudentInfoBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore

class StudentInfoActivity : AppCompatActivity() {
    private lateinit var binding: ActivityStudentInfoBinding
    private val db = FirebaseFirestore.getInstance()
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
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityStudentInfoBinding.inflate(layoutInflater)
        setContentView(binding.root)
        setupClassSelection()
        binding.btnSaveStudent.setOnClickListener {
            binding.btnSaveStudent.isEnabled = false
            binding.btnSaveStudent.text = "Saving..."
            binding.progressBar.visibility = View.VISIBLE
            saveStudentInfo()

        }


    }

    private fun saveStudentInfo() {
        val uid = FirebaseAuth.getInstance().currentUser?.uid
        if (uid == null) {
            Toast.makeText(this, "User not logged in", Toast.LENGTH_SHORT).show()
            resetForm()
            return
        }

        val name = binding.etStudentName.text.toString().trim()
        val rollNumber = binding.etStudentRoll.text.toString().trim()
        val classSelection = binding.spinnerStudentClass.selectedItem.toString().trim()

        if (name.isEmpty() || rollNumber.isEmpty() || classSelection == "Select Your Class") {
            Toast.makeText(this, "Please fill all fields", Toast.LENGTH_SHORT).show()
            resetForm()
            return
        }

        val studentData = hashMapOf(
            "fullName" to name,
            "rollNumber" to rollNumber,
            "class" to classSelection
        )

        val firestore = FirebaseFirestore.getInstance()

        firestore.collection("users").document(uid)
            .update(studentData as Map<String, Any>)
            .addOnSuccessListener {
                addStudentToClass(uid, name, rollNumber, classSelection)
                Toast.makeText(this, "Student info saved", Toast.LENGTH_SHORT).show()
                val sharedPref= getSharedPreferences("UserPrefs", MODE_PRIVATE)
                sharedPref.edit().putBoolean("isProfileComplete", true).apply()
                startActivity(Intent(this, StudentDashboardActivity::class.java))
                finish()
            }
            .addOnFailureListener {
                Toast.makeText(this, "Failed to save info", Toast.LENGTH_SHORT).show()
            }
            .addOnCompleteListener {
                resetForm()
            }
    }

    private fun addStudentToClass(uid: String, name: String, rollNumber: String, classSelection: String) {
        val classId = classIdMap[classSelection] ?: return
        val studentData = hashMapOf(
            "studentId" to uid,
            "studentName" to name,
            "studentRoll" to rollNumber,
            "studentClass" to classSelection
        )
        db.collection("classes").document(classId)
            .update("students", FieldValue.arrayUnion(studentData))


    }

    private fun resetForm() {
        binding.btnSaveStudent.isEnabled = true
        binding.btnSaveStudent.text = "Save Information"
        binding.progressBar.visibility = View.GONE
    }

    private fun setupClassSelection() {
        val classes = listOf(
            "Select Your Class",
            "BSSE Regular 1 (2025-2029) - Semester 1",
            "BSSE Self Support 2 (2023-2027) - Semester 4",
            "BSSE Self Support 2 (2022-2026) - Semester 6",
            "BSSE Regular 1 (2024-2028) - Semester 2",
            "BSSE Self Support 1 (2022-2026) - Semester 6",
            "BSSE Self Support 2 (2024-2028) - Semester 2",
            "BSSE Regular 1 (2022-2026) - Semester 6",
            "BSSE Self Support 1 (2021-2025) - Semester 8",
            "BSSE Regular 1 (2021-2025) - Semester 8",
            "BSSE Self Support 1 (2023-2027) - Semester 4",
            "BSSE Regular 1 (2023-2027) - Semester 4",
            "BSSE Regular 1 (2024-2028) - Semester 3",
            "BSSE Self Support 1 (2024-2028) - Semester 2",
            "MSSE Self Support 1 (2024-2026) - Semester 2",
            "BSSE Self Support 1 (2025-2029) - Semester 1"
        )

        val adapter = ArrayAdapter(this, android.R.layout.simple_spinner_dropdown_item, classes)
        binding.spinnerStudentClass.adapter = adapter
    }
}


