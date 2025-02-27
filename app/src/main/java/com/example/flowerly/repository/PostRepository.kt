package com.example.flowerly.repository

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.example.flowerly.Post
import com.example.flowerly.R

class PostRepository {
    private val _posts = MutableLiveData<List<Post>>()

    init {
        _posts.value = listOf(
            Post(R.drawable.ic_profile, "Alice", R.drawable.rose1, "Beautiful Rose", "A bright red rose"),
            Post(R.drawable.ic_profile, "Bob", R.drawable.tulip, "Tulip Fields", "A garden full of tulips"),
            Post(R.drawable.ic_profile, "Alice", R.drawable.rose2, "Sunflowers Everywhere", "Sunflowers following the sun")
        )
    }

    fun getPosts(): LiveData<List<Post>> = _posts

    fun getUserPosts(username: String): LiveData<List<Post>> {
        val userPosts = MutableLiveData<List<Post>>()
        _posts.value?.let { postList ->
            userPosts.value = postList.filter { it.username == username }
        }
        return userPosts
    }

    fun deletePost(post: Post) {
        _posts.value = _posts.value?.toMutableList()?.apply { remove(post) }
    }
}
