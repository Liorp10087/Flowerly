package com.example.flowerly.dao

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.flowerly.model.User

@Dao
interface UserDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertUser(user: User)

    @Query("SELECT * FROM User WHERE isCurrentUser = 1 LIMIT 1")
    fun getCurrentUser():User?

    @Query("UPDATE User SET isCurrentUser = 0 WHERE isCurrentUser = 1")
    fun clearCurrentUser()

    @Query("SELECT * FROM User")
    fun getAllUsers(): LiveData<List<User>>

    @Query("SELECT * FROM User WHERE id = :id LIMIT 1")
    fun getUserById(id: String): User?

    @Query("UPDATE User SET username = :username WHERE id = :id")
    fun updateUsername(id: String, username: String)

    @Query("DELETE FROM User")
    fun clearUsers()
}
