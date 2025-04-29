package com.example.acaid.Models

import com.google.firebase.Timestamp

data class Notice (
    var notice:String="",
    val timestamp: Timestamp? = null
)