package com.example.flowerly.viewmodel

import android.app.Application
import android.net.Uri
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import com.example.flowerly.model.Model
import com.example.flowerly.model.Post
import com.example.flowerly.model.PostWithUser

class PostViewModel(application: Application) : AndroidViewModel(application) {
    val posts: LiveData<List<PostWithUser>> = Model.instance.getAllPosts()

    fun getUserPosts(userId: String): LiveData<List<PostWithUser>> {
        return Model.instance.getUserPosts(userId)
    }

    fun deletePost(post: Post) {
        Model.instance.deletePost(post)
    }

    fun addPost(post: Post, imageUri: Uri) {
        Model.instance.addPost(post, imageUri)
    }

    fun updatePost(post: Post, imageUri: Uri?, onSuccess: () -> Unit, onFailure: () -> Unit) {
        Model.instance.updatePost(post, imageUri, {
            onSuccess()
        }, {
            onFailure()
        })
    }
}
