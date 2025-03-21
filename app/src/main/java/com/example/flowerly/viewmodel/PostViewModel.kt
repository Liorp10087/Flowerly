package com.example.flowerly.viewmodel

import android.app.Application
import android.net.Uri
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import com.example.flowerly.model.Post
import com.example.flowerly.model.User
import com.example.flowerly.repository.PostRepository

class PostViewModel(application: Application) : AndroidViewModel(application) {
    private val repository = PostRepository(application)
    val posts: LiveData<List<Post>> = repository.getAllPosts()
    val userDetails: LiveData<Map<String, User>> = repository.userDetails

    fun getUserPosts(userId: String): LiveData<List<Post>> {
        return repository.getUserPosts(userId)
    }

    fun deletePost(post: Post) {
        repository.deletePost(post)
    }

    fun addPost(post: Post, imageUri: Uri) {
        repository.addPost(post, imageUri)
    }
}
