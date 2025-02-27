package com.example.flowerly.repository

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.example.flowerly.model.User
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

class ProfileRepository {
    private val firestore = FirebaseFirestore.getInstance()
    private val auth = FirebaseAuth.getInstance()
    private val _user = MutableLiveData<User>()
    val user: LiveData<User> get() = _user

    init {
        fetchUserData()
    }

    private fun fetchUserData() {
        val currentUserId = auth.currentUser?.uid ?: return
        firestore.collection("users").document(currentUserId)
            .addSnapshotListener { snapshot, error ->
                if (error != null) return@addSnapshotListener
                snapshot?.toObject(User::class.java)?.let { _user.value = it }
            }
    }

    fun updateUsername(newUsername: String) {
        val currentUserId = auth.currentUser?.uid ?: return
        firestore.collection("users").document(currentUserId)
            .update("username", newUsername)
    }
}
