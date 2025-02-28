package com.example.flowerly.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import com.example.flowerly.model.User
import com.example.flowerly.repository.UserRepository

class UserViewModel : ViewModel() {
    private val repository = UserRepository()
    val user: LiveData<User> = repository.user

    fun getCurrentUser() {
        repository.fetchUserData()
    }

    fun updateUsername(newUsername: String) {
        repository.updateUsername(newUsername)
    }
}
