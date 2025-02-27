package com.example.flowerly.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import com.example.flowerly.model.User
import com.example.flowerly.repository.ProfileRepository

class ProfileViewModel : ViewModel() {
    private val repository = ProfileRepository()
    val user: LiveData<User> = repository.user

    fun updateUsername(newUsername: String) {
        repository.updateUsername(newUsername)
    }

//    fun updateProfileImage(newImageUri: String) {
//        repository.updateProfileImage(newImageUri)
//    }
}
