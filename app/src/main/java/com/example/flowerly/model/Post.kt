package com.example.flowerly.model

import android.os.Parcelable
import androidx.room.Embedded
import androidx.room.Entity
import androidx.room.PrimaryKey
import androidx.room.Relation
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


data class PostWithUser(
    @Embedded val post: Post,
    @Relation(
        parentColumn = "userId",
        entityColumn = "id"
    )
    val user: User?
)