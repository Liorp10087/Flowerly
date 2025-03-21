package com.example.flowerly.model

import android.content.Context
import android.os.Looper
import androidx.core.os.HandlerCompat
import com.example.flowerly.dao.AppLocalDatabase
import java.util.concurrent.Executors

class Model private constructor() {
    private val firebase = FirebaseModel
    private val db = AppLocalDatabase.db
    private val executor = Executors.newSingleThreadExecutor()
    private val handler = HandlerCompat.createAsync(Looper.getMainLooper())

    companion object {
        val instance = Model()
    }

    fun login(email: String, password: String, context: Context, callback: (Boolean) -> Unit) {
        firebase.signIn(context, email, password) { success, user ->
            if (success && user != null) {
                val newUser = User(user.uid, user.email ?: "", "ic_profile.png")
                addUserToLocalAndFirebase(newUser)
            }
            callback(success)
        }
    }

    fun signup(email: String, password: String, context: Context, callback: (Boolean) -> Unit) {
        firebase.signUp(context, email, password) { success, user ->
            if (success && user != null) {
                val newUser = User(user.uid, email, "ic_profile.png")
                addUserToLocalAndFirebase(newUser)
            }
            callback(success)
        }
    }

    fun updateUserUsername(
        userId: String,
        newUsername: String,
        context: Context,
        callback: () -> Unit
    ) {
        FirebaseModel.updateUserUsername(context, userId, newUsername) { user ->
            executor.execute {
                if (user != null) {
                    db.userDao().insertUser(user)
                }
                handler.post {
                    callback()
                }
            }
            refreshAllUsers()
        }
    }

    private fun addUserToLocalAndFirebase(user: User) {
        executor.execute {
            db.userDao().insertUser(user)
            firebase.addUserToDB(user) {}
        }
    }

    fun refreshAllUsers() {
        firebase.getAllUsers { users ->
            executor.execute {
                users.forEach { db.userDao().insertUser(it) }
            }
        }
    }
}
