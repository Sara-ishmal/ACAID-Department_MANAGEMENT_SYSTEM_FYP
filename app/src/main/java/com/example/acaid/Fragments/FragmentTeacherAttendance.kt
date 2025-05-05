package com.example.acaid.Fragments

import android.os.Bundle
import android.os.Handler
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.TextView
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.acaid.Adapters.StudentAdapter
import com.example.acaid.Models.AllStudentsModel
import com.example.acaid.Models.AttendanceModel
import com.example.acaid.Models.User
import com.example.acaid.R
import com.example.acaid.databinding.FragmentTeacherAttendanceBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

class FragmentTeacherAttendance : Fragment(),StudentAdapter.ItemLisenerInterface {

    private var _binding: FragmentTeacherAttendanceBinding? = null
    private val binding get() = _binding!!
    private val db = FirebaseFirestore.getInstance()
    private val userId = FirebaseAuth.getInstance().currentUser?.uid
    var fullName:String=""
    private var selectedClass: String? = null
    private var selectedSubject: String? = null
    private val attendanceList = mutableListOf<AttendanceModel>()
    private val studentList = mutableListOf<User>()


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentTeacherAttendanceBinding.inflate(inflater, container, false)
        activity?.window?.statusBarColor = ContextCompat.getColor(requireContext(), R.color.white)
        activity?.window?.navigationBarColor = ContextCompat.getColor(requireContext(), R.color.white)


        binding.recyclerViewStudents.layoutManager = LinearLayoutManager(requireContext())
        binding.recyclerViewStudents.adapter = StudentAdapter(studentList,this@FragmentTeacherAttendance)
        binding.btnSubmitAttendance.setOnClickListener {
            if (studentList.isEmpty()) {
                Toast.makeText(requireContext(), "No students found!", Toast.LENGTH_SHORT).show()
            } else {
                val studentUsernames = studentList.map { it.username }.toSet()
                val markedUsernames = attendanceList.map { it.studentId }.toSet()

                val unmarkedUsernames = studentUsernames - markedUsernames

                if (unmarkedUsernames.isNotEmpty()) {
                    val remaining = unmarkedUsernames.size
                    Toast.makeText(requireContext(), "Mark attendance for all students! $remaining remaining.", Toast.LENGTH_SHORT).show()
                } else {
                    submitAllAttendance()
                }
            }
        }


        loadUserData()

        return binding.root
    }


    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    private fun loadUserData() {
        userId?.let { uid ->
            db.collection("users").document(uid).get().addOnSuccessListener { doc ->
                if (doc.exists()) {
                    fullName = doc.getString("fullName")!!
                    val subjectsTaught = doc.get("subjectsTaught") as? List<String> ?: emptyList()
                    val classesTaught = doc.get("classesTaught") as? List<String>
                    if (!classesTaught.isNullOrEmpty()) {
                        setClassSpinner(classesTaught)
                    } else {
                        Toast.makeText(requireContext(), "No classes found for this teacher", Toast.LENGTH_SHORT).show()
                    }

                    setSubjectSpinner(subjectsTaught)
                }
            }
        }
    }

    private fun setClassSpinner(classes: List<String>) {
        val classAdapter = ArrayAdapter(requireContext(), android.R.layout.simple_spinner_item, classes)
        classAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        binding.spinnerClass.adapter = classAdapter

        binding.spinnerClass.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parentView: AdapterView<*>, view: View?, position: Int, id: Long) {
                selectedClass = parentView.getItemAtPosition(position) as String
                fetchStudents()
            }

            override fun onNothingSelected(parentView: AdapterView<*>) {
                selectedClass = null
            }
        }
    }

    private fun setSubjectSpinner(subjects: List<String>) {
        val subjectAdapter = ArrayAdapter(requireContext(), android.R.layout.simple_spinner_item, subjects)
        subjectAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        binding.spinnerSubject.adapter = subjectAdapter

        binding.spinnerSubject.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parentView: AdapterView<*>, view: View?, position: Int, id: Long) {
                selectedSubject = parentView.getItemAtPosition(position) as String
                fetchStudents()
            }

            override fun onNothingSelected(parentView: AdapterView<*>) {
                selectedSubject = null
            }
        }
    }

    private fun fetchStudents() {
        if (selectedClass != null) {
            db.collection("users")
                .whereEqualTo("role", "student")
                .whereEqualTo("class", selectedClass!!)
                .get()
                .addOnSuccessListener { result ->
                    studentList.clear()
                    for (document in result) {
                        val student = document.toObject(User::class.java)
                        studentList.add(student)
                    }
                    binding.recyclerViewStudents.adapter = StudentAdapter(studentList,this@FragmentTeacherAttendance)

                }
                .addOnFailureListener { e ->
                    Toast.makeText(requireContext(), "Error fetching students: $e", Toast.LENGTH_SHORT).show()
                }
        }
    }

    private fun submitAllAttendance() {
        for (attendance in attendanceList) {
            db.collection("attendance")
                .add(attendance)
                .addOnFailureListener { e ->
                    Toast.makeText(requireContext(), "Error: $e", Toast.LENGTH_SHORT).show()
                }
        }

        attendanceList.clear()

        showSuccessDialog()
    }
    private fun showSuccessDialog() {
        val dialogView = layoutInflater.inflate(R.layout.dialog_attendance_success, null)

        val txtClassSubject = dialogView.findViewById<TextView>(R.id.txtClassSubject)
        txtClassSubject.text = "Class: $selectedClass\nSubject: $selectedSubject"

        val dialog = android.app.AlertDialog.Builder(requireContext())
            .setView(dialogView)
            .setCancelable(false)
            .create()

        dialog.window?.setBackgroundDrawableResource(android.R.color.transparent)
        dialog.show()

        Handler().postDelayed({
            dialog.dismiss()
            parentFragmentManager.popBackStack()
        }, 3000)
    }

    override fun onAttendanceCheckBoxClick(modelUser: User, status: String) {
        if (selectedClass != null && selectedSubject != null) {

            attendanceList.removeAll { it.studentId == modelUser.username }

            val attendanceModel = AttendanceModel(
                studentId = modelUser.username,
                studentName = modelUser.fullName,
                className = selectedClass!!,
                subjectName = selectedSubject!!,
                status = status,
                teacherId = userId!!,
                teacherName = fullName
            )

            attendanceList.add(attendanceModel)
        } else {
            Toast.makeText(requireContext(), "Select class and subject first", Toast.LENGTH_SHORT).show()
        }
    }



}
