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
            Post(R.drawable.ic_profile, "Alice", R.drawable.rose1, "Beautiful Rose", "A bright red rose"),
            Post(R.drawable.ic_profile, "Bob", R.drawable.tulip, "Tulip Fields", "A garden full of tulips"),
            Post(R.drawable.ic_profile, "Charlie", R.drawable.rose2, "Sunflowers Everywhere", "Sunflowers following the sun")
        )
    }
}
