package com.example.acaid.Model

data class SubjectAttendance(
    val subject: String = "",
    var present: Int = 0,
    var absent: Int = 0,
    var leave: Int = 0
)
