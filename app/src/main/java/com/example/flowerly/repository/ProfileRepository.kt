package com.example.flowerly.repository

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.example.flowerly.Post
import com.example.flowerly.R

class ProfileRepository {
    private val _userPosts = MutableLiveData<List<Post>>()
    private val _username = MutableLiveData<String>()
    private val _profileImageUri = MutableLiveData<String>()

    init {
        // Mock Data with profile pictures and usernames
        _userPosts.value = listOf(
            Post(R.drawable.ic_profile, "Mock User", R.drawable.rose1, "My Rose", "A beautiful red rose"),
            Post(R.drawable.ic_profile, "Mock User", R.drawable.tulip, "Tulip Love", "Bright and colorful tulips"),
            Post(R.drawable.ic_profile, "Mock User", R.drawable.rose2, "Sunflower Joy", "Sunflowers follow the sun")
        )

        _username.value = "Mock User"
        _profileImageUri.value = "" // Empty for now
    }

    fun getUserPosts(): LiveData<List<Post>> = _userPosts
    fun getUsername(): LiveData<String> = _username
    fun getProfileImage(): LiveData<String> = _profileImageUri

    fun updateUsername(newUsername: String) {
        _username.value = newUsername
    }

    fun updateProfileImage(newImageUri: String) {
        _profileImageUri.value = newImageUri
    }
}
