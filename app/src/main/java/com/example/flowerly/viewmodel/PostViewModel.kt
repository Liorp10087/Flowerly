package com.example.flowerly.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.flowerly.Post
import com.example.flowerly.repository.PostRepository

class PostViewModel : ViewModel() {
    private val repository = PostRepository()
    val posts: LiveData<List<Post>> = repository.posts

    fun getUserPosts(username: String): LiveData<List<Post>> {
        return repository.getUserPosts(username)
    }

    fun deletePost(post: Post) {
        repository.deletePost(post)
    }

//    fun editPost(post: Post, newTitle: String, newDesc: String) {
//        repository.editPost(post, newTitle, newDesc)
//    }
}
