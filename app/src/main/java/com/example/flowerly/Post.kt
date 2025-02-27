package com.example.flowerly

import com.example.flowerly.model.User

data class Post(
    val id: String = "",
    val imagePathUrl: String = "",
    val title: String = "",
    val description: String = "",
    val user: User = User()
)

