package com.example.flowerly.dao

import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.example.flowerly.base.MyApplication
import com.example.flowerly.dao.UserDao
import com.example.flowerly.dao.PostDao
import com.example.flowerly.model.User
import com.example.flowerly.model.Post


@Database(entities = [User::class, Post::class], version = 20)
abstract class AppLocalDbRepository : RoomDatabase() {
    abstract fun userDao(): UserDao
    abstract fun postDao(): PostDao
}

object AppLocalDatabase {

    val db: AppLocalDbRepository by lazy {

        val context = MyApplication.Globals.appContext
            ?: throw IllegalStateException("Application context not available")

        Room.databaseBuilder(
            context,
            AppLocalDbRepository::class.java,
            "dbFileName.db"
        )
            .fallbackToDestructiveMigration()
            .build()
    }
}

