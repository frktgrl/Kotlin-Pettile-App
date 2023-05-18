package com.example.apppettileapp.model

data class AdoptionPost(
    val downloadUrl: String,
    val title: String,
    val name: String,
    val location: String,
    val userId: String,
    val like: List<String>,
    val favorite: List<String>
)

