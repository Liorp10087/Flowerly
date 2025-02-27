package com.example.flowerly.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.flowerly.Post
import com.example.flowerly.R

class PostViewModel : ViewModel() {
    private val _posts = MutableLiveData<List<Post>>()
    val posts: LiveData<List<Post>> = _posts

    init {
        loadPosts()
    }

    private fun loadPosts() {
        _posts.value = listOf(
            Post(R.drawable.rose1, "Rose", "A beautiful red rose"),
            Post(R.drawable.tulip, "Tulip", "Bright and colorful tulips"),
            Post(R.drawable.rose2, "Sunflower", "Sunflowers follow the sun")
        )
    }
}
