package com.example.acaid.Ui

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.ArrayAdapter
import android.widget.MultiAutoCompleteTextView
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.acaid.R
import com.example.acaid.databinding.ActivityTeacherInfoBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

class TeacherInfoActivity : AppCompatActivity() {
    private lateinit var binding: ActivityTeacherInfoBinding
    private lateinit var auth: FirebaseAuth
    private lateinit var firestore: FirebaseFirestore
    private var selectedClasses = mutableListOf<String>()
    private var selectedSubjects = mutableListOf<String>()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityTeacherInfoBinding.inflate(layoutInflater)
        setContentView(binding.root)

        auth = FirebaseAuth.getInstance()
        firestore = FirebaseFirestore.getInstance()

        setupButtons()
        setupTeacherTypeSpinner()


        binding.btnSaveTeacher.setOnClickListener {
            binding.btnSaveTeacher.isEnabled = false
            binding.btnSaveTeacher.text = "Saving..."
            binding.progressBar.visibility = View.VISIBLE
            saveTeacherInfo()
        }
    }

    private fun setupTeacherTypeSpinner() {
        //regular or visiting teacher
        val teacherTypes= listOf("Select","Regular","Visiting")
        val adapter = ArrayAdapter(this, android.R.layout.simple_spinner_dropdown_item, teacherTypes)
        binding.spinnerTeacherType.adapter = adapter
    }

    private fun setupButtons() {
        val classesList = listOf(
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

        val subjectsList = listOf(
            "OOP", "Data Structures", "Algorithms", "Web Development",
            "Mobile App Development", "Software Engineering", "DBMS",
            "Operating Systems", "AI", "Machine Learning", "Cyber Security"
        )

        binding.btnSelectClasses.setOnClickListener {
            showMultiChoiceDialog("Select Classes", classesList, selectedClasses)
        }

        binding.btnSelectSubjects.setOnClickListener {
            showMultiChoiceDialog("Select Subjects", subjectsList, selectedSubjects)
        }
    }

    private fun showMultiChoiceDialog(title: String, itemList: List<String>, selectedList: MutableList<String>) {
        val selectedItems = BooleanArray(itemList.size) { index ->
            selectedList.contains(itemList[index])
        }

        val dialog = AlertDialog.Builder(this)
            .setTitle(title)
            .setMultiChoiceItems(itemList.toTypedArray(), selectedItems) { _, which, isChecked ->
                if (isChecked) {
                    if (!selectedList.contains(itemList[which])) {
                        selectedList.add(itemList[which])
                    }
                } else {
                    selectedList.remove(itemList[which])
                }
            }
            .setPositiveButton("OK") { _, _ ->
                val selectedText = selectedList.joinToString(", ")
                Toast.makeText(this, "$title: $selectedText", Toast.LENGTH_SHORT).show()
            }
            .setNegativeButton("Cancel", null)
            .create()

        dialog.show()
    }


    private fun saveTeacherInfo() {
        val uid = auth.currentUser?.uid ?: return

        val name = binding.etTeacherName.text.toString().trim()
        val type = binding.spinnerTeacherType.selectedItem.toString()
        val classesTaught = selectedClasses
        val subjectsTaught = selectedSubjects

        val employeeId = binding.etEmployeeId.text.toString().trim()

        if (name.isEmpty() || type == "Select Type" || classesTaught.isEmpty() || subjectsTaught.isEmpty()) {
            Toast.makeText(this, "Please fill all required fields", Toast.LENGTH_SHORT).show()
            binding.btnSaveTeacher.isEnabled = true
            binding.progressBar.visibility = View.GONE
            binding.btnSaveTeacher.text = "Save"
            return
        }

        val teacherData = hashMapOf(
            "fullName" to name,
            "type" to type,
            "classesTaught" to classesTaught,
            "subjectsTaught" to subjectsTaught,
            "employeeId" to employeeId
        )

        firestore.collection("users").document(uid)
            .update(teacherData as Map<String, Any>)
            .addOnSuccessListener {
                Toast.makeText(this, "Teacher info saved successfully", Toast.LENGTH_SHORT).show()
               val sharedPref= getSharedPreferences("UserPrefs", MODE_PRIVATE)
                sharedPref.edit().putBoolean("isProfileComplete", true).apply()
               startActivity(Intent(this, TeacherDashboardActivity::class.java))
                finish()

            }
            .addOnFailureListener {
                Toast.makeText(this, "Failed to save teacher info", Toast.LENGTH_SHORT).show()
                binding.btnSaveTeacher.isEnabled = true
                binding.progressBar.visibility = View.GONE
                binding.btnSaveTeacher.text = "Save"
            }
    }
}