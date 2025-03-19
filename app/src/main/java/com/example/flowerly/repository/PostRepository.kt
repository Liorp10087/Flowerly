package com.example.flowerly.repository

import android.net.Uri
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.example.flowerly.Post
import com.example.flowerly.model.User
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage

class PostRepository {
    private val firestore = FirebaseFirestore.getInstance()
    private val storage = FirebaseStorage.getInstance()
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

    private fun uploadImageToStorage(imageUri: Uri, callback: (String?) -> Unit) {
        val imageRef = storage.reference.child("images/${System.currentTimeMillis()}.jpg")
        imageRef.putFile(imageUri)
            .addOnSuccessListener { taskSnapshot ->
                val fileName = imageRef.name
                callback(fileName)
            }
            .addOnFailureListener {
                callback(null)
            }
    }

    fun addPost(post: Post, imageUri: Uri) {
        uploadImageToStorage(imageUri) { fileName ->
            if (fileName != null) {
                val postData = hashMapOf(
                    "id" to post.id,
                    "title" to post.title,
                    "description" to post.description,
                    "imagePathUrl" to fileName,
                    "user" to firestore.collection("users").document(post.user.id)
                )

                postsCollection.add(postData)
            } else {
                Log.e("PostRepository", "Failed to upload image")
            }
        }
    }


    fun deletePost(post: Post) {
        postsCollection.document(post.id).delete()
    }

    fun getUserPosts(userId: String): LiveData<List<Post>> {
        val userPostsLiveData = MutableLiveData<List<Post>>()
        val userRef = FirebaseFirestore.getInstance().collection("users").document(userId)

        postsCollection
            .whereEqualTo("user", userRef)
            .get()
            .addOnSuccessListener { snapshot ->
                val postList = snapshot.documents.mapNotNull { document ->
                    val postData = document.data ?: return@mapNotNull null
                    val userRef = document.getDocumentReference("user") ?: return@mapNotNull null

                    Post(
                        id = document.id,
                        title = postData["title"] as? String ?: "",
                        description = postData["description"] as? String ?: "",
                        imagePathUrl = postData["imagePathUrl"] as? String ?: "",
                        user = User(id = userRef.id)
                    )
                }

                userPostsLiveData.value = postList
            }
            .addOnFailureListener { exception ->
                Log.e("PostRepository", "Error fetching posts: ", exception)
                userPostsLiveData.value = emptyList()
            }

        return userPostsLiveData
    }
}
