package com.example.flowerly.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "posts")
data class Post(
    @PrimaryKey(autoGenerate = false) var id: String = "",
    var imagePathUrl: String = "",
    var title: String = "",
    var description: String = "",
    var userId: String = ""
)
