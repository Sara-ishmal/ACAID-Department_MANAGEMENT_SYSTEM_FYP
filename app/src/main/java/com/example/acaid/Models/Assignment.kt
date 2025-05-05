package com.example.acaid.Models

data class Assignment(
    val className: String = "",
    val subject: String = "",
    val title: String = "",
    val description: String = "",
    val teacherName: String = "",
    val teacherId: String = "",
    val deadline: String = "",
    val timestamp: Long = System.currentTimeMillis()
)
