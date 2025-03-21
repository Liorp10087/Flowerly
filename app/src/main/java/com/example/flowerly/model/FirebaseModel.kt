package com.example.flowerly.model

import android.content.Context
import android.widget.Toast
import androidx.lifecycle.LiveData
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.firestore.FirebaseFirestoreSettings
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase

object FirebaseModel {
    private val db by lazy { Firebase.firestore }
    private const val USERS_COLLECTION = "users"
    private val auth: FirebaseAuth = FirebaseAuth.getInstance()

    init {
        val settings = FirebaseFirestoreSettings.Builder()
            .setPersistenceEnabled(false)
            .build()
        db.firestoreSettings = settings
    }

    val firebaseUserLiveData: LiveData<FirebaseUser?> = object : LiveData<FirebaseUser?>() {
        private val authListener = FirebaseAuth.AuthStateListener { auth ->
            value = auth.currentUser
        }

        override fun onActive() {
            super.onActive()
            value = auth.currentUser
            auth.addAuthStateListener(authListener)
        }

        override fun onInactive() {
            super.onInactive()
            auth.removeAuthStateListener(authListener)
        }
    }

    fun getCurrentUser(): FirebaseUser? = auth.currentUser

    fun signIn(
        context: Context,
        email: String,
        password: String,
        callback: (Boolean, FirebaseUser?) -> Unit
    ) {
        auth.signInWithEmailAndPassword(email, password)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    callback(true, auth.currentUser)
                } else {
                    showToast(context, task.exception?.localizedMessage ?: "Login failed")
                    callback(false, null)
                }
            }
    }

    fun signUp(
        context: Context,
        email: String,
        password: String,
        callback: (Boolean, FirebaseUser?) -> Unit
    ) {
        auth.createUserWithEmailAndPassword(email, password)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    callback(true, auth.currentUser)
                } else {
                    showToast(context, task.exception?.localizedMessage ?: "Signup failed")
                    callback(false, null)
                }
            }
    }

    fun signOut() {
        auth.signOut()
    }

    fun addUserToDB(user: User, callback: () -> Unit) {
        db.collection(USERS_COLLECTION).document(user.id).set(user.json)
            .addOnSuccessListener { callback() }
    }

    fun isUsernameExists(username: String, callback: (Boolean) -> Unit) {
        db.collection(USERS_COLLECTION)
            .whereEqualTo("username", username)
            .get()
            .addOnSuccessListener { callback(!it.isEmpty) }
            .addOnFailureListener { callback(false) }
    }

    fun updateUserUsername(
        context: Context,
        userId: String,
        newUsername: String,
        callback: (User?) -> Unit
    ) {
        if (newUsername.isBlank()) {
            showToast(context, "Username cannot be empty")
            callback(null)
            return
        }

        isUsernameExists(newUsername) { exists ->
            if (exists) {
                showToast(context, "Username is already taken")
                callback(null)
            } else {
                val userRef = db.collection(USERS_COLLECTION).document(userId)
                userRef.update(
                    mapOf(
                        "username" to newUsername,
                        "lastUpdated" to System.currentTimeMillis()
                    )
                )
                    .addOnSuccessListener {
                        userRef.get().addOnSuccessListener {
                            val updatedUser = it.data?.let(User::fromJSON)
                            callback(updatedUser)
                        }.addOnFailureListener { e ->
                            showToast(context, "Failed to retrieve updated user")
                            callback(null)
                        }
                    }
                    .addOnFailureListener { e ->
                        showToast(context, "Failed to update username")
                        callback(null)
                    }
            }
        }
    }

    fun getAllUsers(callback: (List<User>) -> Unit) {
        db.collection(USERS_COLLECTION).get()
            .addOnSuccessListener { snapshot ->
                callback(snapshot.mapNotNull { User.fromJSON(it.data) })
            }.addOnFailureListener {
                callback(emptyList())
            }
    }

    fun getUserById(id: String, callback: (User?) -> Unit) {
        db.collection(USERS_COLLECTION).document(id).get()
            .addOnSuccessListener {
                callback(it.data?.let(User::fromJSON))
            }.addOnFailureListener {
                callback(null)
            }
    }

    private fun showToast(context: Context, message: String) {
        Toast.makeText(context, message, Toast.LENGTH_SHORT).show()
    }
}
