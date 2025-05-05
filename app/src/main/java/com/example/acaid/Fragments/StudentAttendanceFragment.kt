package com.example.acaid.Fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.acaid.Adapter.AttendanceAdapter
import com.example.acaid.Model.SubjectAttendance
import com.example.acaid.databinding.FragmentStudentAttendanceBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

class StudentAttendanceFragment : Fragment() {

    private lateinit var binding: FragmentStudentAttendanceBinding
    private val db = FirebaseFirestore.getInstance()
    private val userId = FirebaseAuth.getInstance().currentUser?.uid
    private val attendanceMap = mutableMapOf<String, SubjectAttendance>()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentStudentAttendanceBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onStart() {
        super.onStart()
        loadUserData()
    }

    private fun loadUserData() {
        userId?.let { uid ->
            db.collection("users").document(uid).get().addOnSuccessListener { doc ->
                if (doc.exists()) {
                    val username = doc.getString("username") ?: return@addOnSuccessListener
                    val className = doc.getString("class") ?: return@addOnSuccessListener
                    loadAttendanceData(username, className)
                }
            }
        }
    }

    private fun loadAttendanceData(username: String, className: String) {
        Toast.makeText(requireContext(), username + className, Toast.LENGTH_SHORT).show()
        db.collection("attendance")
            .whereEqualTo("studentId", username)
            .whereEqualTo("className", className)
            .get()
            .addOnSuccessListener { snapshot ->
                for (doc in snapshot) {
                    val subject = doc.getString("subjectName") ?: continue
                    val status = doc.getString("status") ?: continue
                    val current = attendanceMap[subject] ?: SubjectAttendance(subject)

                    when (status.lowercase()) {
                        "present" -> current.present++
                        "absent" -> current.absent++
                        "leave" -> current.leave++
                    }

                    attendanceMap[subject] = current
                }

                val list = attendanceMap.values.toList()
                binding.rvSubjects.layoutManager = LinearLayoutManager(requireContext())
                binding.rvSubjects.adapter = AttendanceAdapter(list)

                updateOverallStats(list)
            }
    }

    private fun updateOverallStats(subjects: List<SubjectAttendance>) {
        var totalPresent = 0
        var totalAbsent = 0
        subjects.forEach {
            totalPresent += it.present
            totalAbsent += it.absent
        }
        val total = totalPresent + totalAbsent

        val percentage = if (total > 0) (totalPresent * 100 / total) else 0
        binding.totalPresent.text = "Present: $totalPresent"
        binding.totalAbsent.text = "Absent: $totalAbsent"
        binding.totalPercentage.text = "$percentage%"

        val attendanceMessage = when {
            percentage > 90 -> "Great job! Your attendance is excellent. Keep up the consistency!"
            percentage in 75..90 -> "You're on track! Stay committed to maintaining a strong presence."
            percentage in 50..74 -> "Your attendance could improve. Aim for better consistency!"
            else -> "Attendance is low—make sure to stay engaged and present!"
        }

        binding.msgAccordingToAttendance.text = attendanceMessage
    }

}
