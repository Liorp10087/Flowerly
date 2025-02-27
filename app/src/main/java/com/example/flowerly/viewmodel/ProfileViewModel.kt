package com.example.flowerly.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import com.example.flowerly.Post
import com.example.flowerly.repository.ProfileRepository

class ProfileViewModel : ViewModel() {
    private val repository = ProfileRepository()

    val userPosts: LiveData<List<Post>> = repository.getUserPosts()
    val username: LiveData<String> = repository.getUsername()
    val profileImage: LiveData<String> = repository.getProfileImage()

    fun updateUsername(newUsername: String) {
        repository.updateUsername(newUsername)
    }

    fun updateProfileImage(newImageUri: String) {
        repository.updateProfileImage(newImageUri)
    }
}
