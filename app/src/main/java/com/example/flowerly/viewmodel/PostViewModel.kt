package com.example.flowerly.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import com.example.flowerly.Post
import com.example.flowerly.repository.PostRepository
import android.net.Uri

class PostViewModel : ViewModel() {
    private val repository = PostRepository()
    val posts: LiveData<List<Post>> = repository.posts

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
