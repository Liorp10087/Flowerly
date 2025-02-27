package com.example.flowerly.model

data class User(
    val id: String = "",
    val username: String = "",
    //TODO: check what is the best default value here
    val profilePictureUrl: String = "ic_profile.png"
)