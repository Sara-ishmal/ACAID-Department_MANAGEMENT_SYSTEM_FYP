package com.example.acaid.Models

import com.google.firebase.Timestamp

data class AttendanceModel(
    val studentId: String = "",
    val studentName: String = "",
    val className: String = "",
    val subjectName: String = "",
    val date: Timestamp? = Timestamp.now(),
    val status: String = "",
    val teacherId: String = "",
    val teacherName: String = ""
)
