package com.example.pettile.model

data class ChatView (val downloadUrl : String , val username : String, val name : String , val userId : String, val userEmail : String , val followers : List<String>,val following : List<String>) {
}