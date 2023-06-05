package com.example.pettile.model

data class Chat(
    val text: String, val userEmail: String,
    val name: String?, val username: String?,
    val downloadUrl: String?
){
}