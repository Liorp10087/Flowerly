package com.example.travelshare.dao

import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.example.flowerly.base.MyApplication
import com.example.flowerly.dao.UserDao
import com.example.flowerly.model.User

@Database(entities = [User::class], version = 8)
abstract class AppLocalDbRepository : RoomDatabase() {
    abstract fun userDao(): UserDao
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