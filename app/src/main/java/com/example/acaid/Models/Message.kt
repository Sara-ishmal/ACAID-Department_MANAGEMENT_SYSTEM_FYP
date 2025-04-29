package com.example.acaid.Models

data class Message(
    var id: String = "",
    var text: String = "",
    var name:String="",
    var time:Long= 0L,
    val senderName: String = ""
)
