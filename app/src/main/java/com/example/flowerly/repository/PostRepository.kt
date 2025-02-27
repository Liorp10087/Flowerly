package com.example.flowerly.repository

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.example.flowerly.Post
import com.example.flowerly.model.User
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.toObject

class PostRepository {
    private val firestore = FirebaseFirestore.getInstance()
    private val postsCollection = firestore.collection("posts")
    private val _posts = MutableLiveData<List<Post>>()
    val posts: LiveData<List<Post>> get() = _posts

    init {
        fetchPosts()
    }

    private fun fetchPosts() {
        postsCollection.get().addOnSuccessListener { snapshot ->
            val postList = mutableListOf<Post>()

            snapshot.documents.forEach { document ->
                val postData = document.data
                if (postData != null) {
                    val userRef = document.getDocumentReference("user")

                    userRef?.get()?.addOnSuccessListener { userSnapshot ->
                        val user = userSnapshot.toObject(User::class.java)

                        val post = Post(
                            id = document.id,
                            title = postData["title"] as? String ?: "",
                            description = postData["description"] as? String ?: "",
                            imagePathUrl = postData["imagePathUrl"] as? String ?: "",
                            user = user ?: User()
                        )

                        postList.add(post)
                        _posts.value = postList
                    }
                }
            }
        }
    }





    fun addPost(post: Post) {
        val postData = hashMapOf(
            "id" to post.id,
            "title" to post.title,
            "description" to post.description,
            "imagePathUrl" to post.imagePathUrl,
            "user" to hashMapOf(
                "id" to post.user.id,
                "username" to post.user.username,
                "profilePictureUrl" to post.user.profilePictureUrl
            )
        )

        postsCollection.add(postData)
    }

    fun deletePost(post: Post) {
        postsCollection.document(post.id).delete()
    }

    fun getUserPosts(username: String): LiveData<List<Post>> {
        val userPosts = MutableLiveData<List<Post>>()
        _posts.value?.let { postList ->
            userPosts.value = postList.filter { it.user.username == username }
        }
        return userPosts
    }
}

