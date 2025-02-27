package com.example.flowerly.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import com.example.flowerly.Post
import com.example.flowerly.repository.PostRepository
import com.example.flowerly.repository.ProfileRepository

class ProfileViewModel : ViewModel() {
    private val postRepository = PostRepository()
    private val profileRepository = ProfileRepository()

    val username: LiveData<String> = profileRepository.getUsername()
    val userPosts: LiveData<List<Post>> = postRepository.getUserPosts(username.value ?: "Mock User")

    fun updateUsername(newUsername: String) {
        profileRepository.updateUsername(newUsername)
    }

    fun updateProfileImage(newImageUri: String) {
        profileRepository.updateProfileImage(newImageUri)
    }
}
