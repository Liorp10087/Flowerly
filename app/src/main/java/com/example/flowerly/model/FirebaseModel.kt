package com.example.flowerly.model

import android.content.Context
import android.util.Log
import com.google.firebase.firestore.FirebaseFirestoreSettings
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase

object FirebaseModel {
    private val db by lazy { Firebase.firestore }
    private const val USERS_COLLECTION = "users"

    init {
        val settings = FirebaseFirestoreSettings.Builder().setPersistenceEnabled(false).build()

        db.firestoreSettings = settings
    }

    fun addUserToDB(user: User, context: Context, callback: () -> Unit) {
        isUsernameExists(user.username) { exists ->
            if (!exists) {
                db.collection(USERS_COLLECTION).document(user.id).set(user.json)
                    .addOnSuccessListener {
                        callback()
                    }
                    .addOnFailureListener { exception ->
                        Log.e("addUser", "failed setting user", exception)
                    }
            } else {
                callback()
            }
        }
    }

    fun isUsernameExists(username: String, callback: (Boolean) -> Unit) {
        db.collection(USERS_COLLECTION)
            .whereEqualTo("username", username)
            .get()
            .addOnSuccessListener { querySnapshot ->
                callback(!querySnapshot.isEmpty)
            }
            .addOnFailureListener { exception ->
                Log.e("isUsernameExists", "failed to check username availability", exception)
                callback(false)
            }
    }

    fun updateUserUsername(
        userId: String,
        newUsername: String,
        callback: (User?) -> Unit
    ) {
        isUsernameExists(newUsername) { exists ->
            if (!exists) {
                val userRef = db.collection(USERS_COLLECTION).document(userId)
                userRef.update(
                    mapOf(
                        "username" to newUsername,
                        "lastUpdated" to System.currentTimeMillis()
                    )
                )
                    .addOnSuccessListener {
                        userRef.get().addOnSuccessListener { documentSnapshot ->
                            val updatedUser = documentSnapshot.toObject(User::class.java)
                            callback(updatedUser)
                            Log.i("updateUser", "Succeeded to update user")
                        }.addOnFailureListener { e ->
                            Log.e("updateUser", "Failed to get updated user", e)
                            callback(null)
                        }
                    }
                    .addOnFailureListener { e ->
                        Log.e("updateUser", "Failed to update user", e)
                        callback(null)
                    }
            } else {
                callback(null)
            }
        }
    }

    fun getAllUsers(since: Long, callback: (ArrayList<User>) -> Unit) {
        db.collection(USERS_COLLECTION)
            .whereGreaterThanOrEqualTo("lastUpdated", since).get()
            .addOnCompleteListener {
                if (it.isSuccessful) {
                    val users = ArrayList<User>()
                    for (json in it.result) {
                        users.add((User.fromJSON(json.data)))
                    }
                    callback(users)
                } else {
                    callback(arrayListOf<User>())
                }
            }
    }

    fun getUserById(id: String, callback: (User) -> Unit) {
        db.collection(USERS_COLLECTION).document(id).get()
            .addOnCompleteListener {
                if (it.isSuccessful) {
                    val user = it.result.data?.let { data -> User.fromJSON(data) }
                    if (user != null) {
                        callback(user)
                    }
                }
            }
    }
}