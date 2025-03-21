package com.example.flowerly.dao

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.flowerly.model.Post

@Dao
interface PostDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
     fun insertPost(post: Post)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
     fun insertPosts(posts: List<Post>)

    @Query("SELECT * FROM posts ORDER BY id DESC")
    fun getAllPosts(): LiveData<List<Post>>

    @Query("SELECT * FROM posts WHERE userId = :userId")
    fun getUserPosts(userId: String): LiveData<List<Post>>

    @Delete
     fun deletePost(post: Post)
}
