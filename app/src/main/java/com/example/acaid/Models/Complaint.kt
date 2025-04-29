package com.example.acaid.Models

data class Complaint(
    val userName: String="",
    val userEmail: String="",
    val complaintDescription: String="",
    val complaintDate: String="",
    var complaintStatus: String=""
)
