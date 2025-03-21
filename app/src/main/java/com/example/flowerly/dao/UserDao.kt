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

    @Query("SELECT * FROM User")
    fun getAllUsers(): LiveData<List<User>>

    @Query("SELECT * FROM User WHERE id = :userId LIMIT 1")
    fun getUser(userId: String): LiveData<User?>

    @Query("UPDATE User SET username = :newUsername WHERE id = :userId")
    fun updateUsername(userId: String, newUsername: String)

    @Query("DELETE FROM User")
    fun clearUsers()
}
