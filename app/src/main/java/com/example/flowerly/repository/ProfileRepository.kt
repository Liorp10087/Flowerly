package com.example.flowerly.repository

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData

class ProfileRepository {
    private val _username = MutableLiveData<String>()
    private val _profileImageUri = MutableLiveData<String>()

    init {
        _username.value = "Alice"
    }

    fun getUsername(): LiveData<String> = _username

    fun updateUsername(newUsername: String) {
        _username.value = newUsername
    }

    fun getProfileImage(): LiveData<String> = _profileImageUri

    fun updateProfileImage(newImageUri: String) {
        _profileImageUri.value = newImageUri
    }
}
