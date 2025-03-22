package com.example.flowerly.dao

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Transaction
import androidx.room.Update
import com.example.flowerly.model.Post
import com.example.flowerly.model.PostWithUser

@Dao
interface PostDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
     fun insertPost(post: Post)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
     fun insertPosts(posts: List<Post>)

    @Transaction
    @Query("SELECT * FROM posts ORDER BY id DESC")
    fun getAllPosts(): LiveData<List<PostWithUser>>

    @Transaction
    @Query("SELECT * FROM posts WHERE userId = :userId")
    fun getUserPosts(userId: String): LiveData<List<PostWithUser>>

    @Delete
     fun deletePost(post: Post)

    @Update
    fun updatePost(post: Post)
}


