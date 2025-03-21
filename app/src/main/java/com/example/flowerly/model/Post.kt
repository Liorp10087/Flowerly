package com.example.flowerly.model

import android.os.Parcelable
import androidx.room.Entity
import androidx.room.PrimaryKey
import kotlinx.parcelize.Parcelize

@Entity(tableName = "posts")
@Parcelize
data class Post(
    @PrimaryKey(autoGenerate = false) var id: String = "",
    var imagePathUrl: String = "",
    var title: String = "",
    var description: String = "",
    var userId: String = ""
) : Parcelable
