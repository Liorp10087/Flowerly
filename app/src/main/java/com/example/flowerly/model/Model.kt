package com.example.flowerly.model

import android.content.Context
import android.net.Uri
import android.os.Looper
import android.util.Log
import androidx.core.os.HandlerCompat
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.example.flowerly.dao.AppLocalDatabase
import com.google.firebase.auth.FirebaseUser
import java.util.concurrent.Executors

class Model private constructor() {
    private val firebase = FirebaseModel
    private val db = AppLocalDatabase.db
    private val executor = Executors.newSingleThreadExecutor()
    private val handler = HandlerCompat.createAsync(Looper.getMainLooper())

    private val _posts = MutableLiveData<List<Post>>()
    val posts: LiveData<List<Post>> get() = _posts

    private val _userDetails = MutableLiveData<Map<String, User>>()
    val userDetails: LiveData<Map<String, User>> get() = _userDetails

    companion object {
        val instance = Model()
    }

    fun getCurrentUser(): FirebaseUser? {
        return firebase.getCurrentUser()
    }

    fun login(email: String, password: String, context: Context, callback: (Boolean) -> Unit) {
        firebase.signIn(context, email, password) { success, user ->
            if (success && user != null) {
                firebase.getUserById(user.uid) { fetchedUser ->
                    if (fetchedUser != null) {
                        executor.execute {
                            db.userDao().insertUser(fetchedUser)
                        }
                    }
                    callback(fetchedUser != null)
                }
            } else {
                callback(false)
            }
        }
    }

    fun signup(email: String, password: String, context: Context, callback: (Boolean) -> Unit) {
        firebase.signUp(context, email, password) { success, user ->
            if (success && user != null) {
                val newUser = User(user.uid, email, "ic_profile.png")
                addUserToLocalAndFirebase(newUser)
            }
            callback(success)
        }
    }

    fun updateUserUsername(
        userId: String,
        newUsername: String,
        context: Context,
        callback: () -> Unit
    ) {
        FirebaseModel.updateUserUsername(context, userId, newUsername) { user ->
            executor.execute {
                if (user != null) {
                    db.userDao().insertUser(user)
                }
                handler.post {
                    callback()
                }
            }
            refreshAllUsers()
        }
    }

    private fun addUserToLocalAndFirebase(user: User) {
        executor.execute {
            db.userDao().insertUser(user)
            firebase.addUserToDB(user) {}
        }
    }

    private fun refreshAllUsers() {
        firebase.getAllUsers { users ->
            executor.execute {
                users.forEach { db.userDao().insertUser(it) }
            }
        }
    }

    fun getUserPosts(userId: String): LiveData<List<Post>> = db.postDao().getUserPosts(userId)

    fun refreshPosts() {
        firebase.getAllPosts { posts, userIds ->
            _posts.postValue(posts)
            executor.execute {
                db.postDao().insertPosts(posts)
            }
            firebase.getUsersByIds(userIds) { userMap ->
                _userDetails.postValue(userMap)
            }
        }
    }

    fun addPost(post: Post, imageUri: Uri) {
        firebase.uploadImage(imageUri) { url ->
            if (url != null) {
                val finalPost = post.copy(imagePathUrl = url)
                executor.execute {
                    db.postDao().insertPost(finalPost)
                    firebase.addPostToFirestore(finalPost) {
                        Log.e("Model", "Failed to add post to Firestore")
                    }
                }
            } else {
                Log.e("Model", "Failed to upload image")
            }
        }
    }

    fun deletePost(post: Post) {
        firebase.deletePostFromFirestore(post.id, {
            executor.execute { db.postDao().deletePost(post) }
        }, {
            Log.e("Model", "Failed to delete post from Firestore")
        })
    }
}
