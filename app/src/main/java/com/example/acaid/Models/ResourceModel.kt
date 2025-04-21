package com.example.acaid.Models
import com.google.firebase.Timestamp

data class ResourceModel(
    val title : String = "",
    val timestamp: Timestamp? = null,
    val type: String = "",
    val fileBase64 : String = ""
)
