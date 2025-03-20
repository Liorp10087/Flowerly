package com.example.flowerly.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
data class User(
    @PrimaryKey var id: String = "",
    var username: String = "",
    var profilePictureUrl: String = "ic_profile.png",
)