package com.example.flowerly.repository

import android.content.Context
import android.net.Uri
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.example.flowerly.model.Post
import com.example.flowerly.dao.PostDao
import com.example.flowerly.model.User
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class PostRepository(context: Context) {
    private val firestore = FirebaseFirestore.getInstance()
    private val storage = FirebaseStorage.getInstance()
    private val postsCollection = firestore.collection("posts")
    private val usersCollection = firestore.collection("users")

    private val postDao: PostDao
    private val coroutineScope = CoroutineScope(Dispatchers.IO)

    private val _posts = MutableLiveData<List<Post>>()
    val posts: LiveData<List<Post>> get() = _posts

    private val _userDetails = MutableLiveData<Map<String, User>>()
    val userDetails: LiveData<Map<String, User>> get() = _userDetails

    init {
        val database = AppDatabase.getDatabase(context)
        postDao = database.postDao()
        fetchPostsFromFirestore()
    }

    fun getAllPosts(): LiveData<List<Post>> = postDao.getAllPosts()

    private fun fetchPostsFromFirestore() {
        postsCollection.get().addOnSuccessListener { snapshot ->
            val postList = mutableListOf<Post>()
            val userIds = mutableSetOf<String>()

            snapshot.documents.forEach { document ->
                val postData = document.data ?: return@forEach
                val userRef = document.getDocumentReference("user")

                val post = Post(
                    id = document.id,
                    title = postData["title"] as? String ?: "",
                    description = postData["description"] as? String ?: "",
                    imagePathUrl = postData["imagePathUrl"] as? String ?: "",
                    userId = userRef?.id ?: ""
                )

                postList.add(post)
                userRef?.id?.let { userIds.add(it) }
            }

            _posts.postValue(postList)
            coroutineScope.launch {
                postDao.insertPosts(postList)
                fetchUsers(userIds)
            }
        }
    }

    private fun fetchUsers(userIds: Set<String>) {
        val userMap = mutableMapOf<String, User>()

        if (userIds.isEmpty()) {
            _userDetails.postValue(userMap)
            return
        }

        userIds.forEach { userId ->
            usersCollection.document(userId).get().addOnSuccessListener { document ->
                val user = document.toObject(User::class.java)
                if (user != null) {
                    userMap[userId] = user
                }

                if (userMap.size == userIds.size) {
                    _userDetails.postValue(userMap)
                }
            }.addOnFailureListener {
                if (userMap.size == userIds.size) {
                    _userDetails.postValue(userMap)
                }
            }
        }
    }

    private fun uploadImageToStorage(imageUri: Uri, callback: (String?) -> Unit) {
        val imageRef = storage.reference.child("images/${System.currentTimeMillis()}.jpg")

        imageRef.putFile(imageUri)
            .addOnSuccessListener {
                imageRef.downloadUrl.addOnSuccessListener { uri ->
                    callback(uri.toString())
                }.addOnFailureListener {
                    callback(null)
                }
            }
            .addOnFailureListener {
                callback(null)
            }
    }


    fun addPost(post: Post, imageUri: Uri) {
        uploadImageToStorage(imageUri) { imageUrl ->
            if (imageUrl != null) {
                val updatedPost = post.copy(imagePathUrl = imageUrl)

                _posts.value = _posts.value?.toMutableList()?.apply { add(0, updatedPost) }

                coroutineScope.launch { postDao.insertPost(updatedPost) }

                val postData = hashMapOf(
                    "id" to post.id,
                    "title" to post.title,
                    "description" to post.description,
                    "imagePathUrl" to imageUrl,
                    "user" to usersCollection.document(post.userId)
                )

                postsCollection.document(post.id).set(postData)
                    .addOnFailureListener {
                        Log.e("PostRepository", "Failed to add post to Firestore")
                    }
            } else {
                Log.e("PostRepository", "Failed to upload image")
            }
        }
    }


    fun deletePost(post: Post) {
        postsCollection.document(post.id).delete().addOnSuccessListener {
            coroutineScope.launch { postDao.deletePost(post) }
        }.addOnFailureListener {
            Log.e("PostRepository", "Failed to delete post from Firestore")
        }
    }

    fun getUserPosts(userId: String): LiveData<List<Post>> {
        return postDao.getUserPosts(userId)
    }
}
