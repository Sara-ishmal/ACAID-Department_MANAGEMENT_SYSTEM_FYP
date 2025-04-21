package com.example.acaid.Models

data class AllClassesModel(
    val classId: String="",
    val className: String="",
    val students: List<AllStudentsModel> = listOf(),
)
