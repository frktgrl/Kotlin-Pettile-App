package com.example.apppettileapp.model

data class AdoptionFavorite(
    val downloadUrl: String,
    val title: String,
    val name: String,
    val location: String, val userId: String,
    val like: List<String>, val favorite: List<Map<String, Any>>?)