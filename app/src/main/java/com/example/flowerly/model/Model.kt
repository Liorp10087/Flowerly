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
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.util.concurrent.Executors

class Model private constructor() {
    private val firebase = FirebaseModel
    private val db = AppLocalDatabase.db
    private val executor = Executors.newSingleThreadExecutor()
    private val handler = HandlerCompat.createAsync(Looper.getMainLooper())

    private val _posts = MutableLiveData<List<Post>>()
    val posts: LiveData<List<Post>> get() = _posts

    private val _userDetails = MutableLiveData<Map<String, User>>()

    companion object {
        val instance = Model()
    }

    private suspend fun getCurrentUserFromFirebase(): User? {
        return firebase.getCurrentUser()
    }

    private fun getCurrentUserFromCache(): LiveData<User?> {
        return db.userDao().getCurrentUser()
    }


    suspend fun getCurrentUser(): LiveData<User?> {
        val liveData = MutableLiveData<User?>()

        withContext(Dispatchers.IO) {
            val currentUser = getCurrentUserFromCache().value

            withContext(Dispatchers.Main) {
                if (currentUser != null) {
                    liveData.value = currentUser
                } else {
                    val firebaseUser = getCurrentUserFromFirebase()
                    if (firebaseUser != null) {
                        setCurrentUser(firebaseUser)
                        liveData.value = firebaseUser
                    } else {
                        liveData.value = null
                    }
                }
            }
        }

        return liveData
    }


    fun login(email: String, password: String, context: Context, callback: (Boolean) -> Unit) {
        firebase.signIn(context, email, password) { success, user ->
            if (success && user != null) {
                firebase.getUserById(user.uid) { fetchedUser ->
                    val username = fetchedUser?.username ?: email
                    val newUser = User(user.uid, user.email ?: "", profilePictureUrl = "ic_profile.png", username = username)
                    addUserToLocalAndFirebase(newUser)
                    setCurrentUser(newUser)
                }
            }
            callback(success)
        }


    }

    fun signup(email: String, password: String, context: Context, callback: (Boolean) -> Unit) {
        firebase.signUp(context, email, password) { success, user ->
            if (success && user != null) {
                val newUser = User(user.uid, email = email, profilePictureUrl = "ic_profile.png", username = email )
                addUserToLocalAndFirebase(newUser)
                setCurrentUser(newUser)
            }
            callback(success)
        }
    }

    private fun setCurrentUser(user: User) {
        executor.execute {
            db.userDao().clearCurrentUser()
            db.userDao().insertUser(user.copy(isCurrentUser = true))
        }
    }

    fun updateUserUsername(userId: String, newUsername: String, context: Context, callback: () -> Unit) {
        FirebaseModel.updateUserUsername(context, userId, newUsername) { updatedUser ->
            executor.execute {
                if (updatedUser != null) {
                    db.userDao().insertUser(updatedUser)
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

    fun refreshAllUsers() {
        firebase.getAllUsers { users ->
            executor.execute {
                users.forEach { user ->
                    val currentUser = getCurrentUserFromCache().value
                    val userToInsert = if (user.id == currentUser?.id) {
                        user.copy(isCurrentUser = true)
                    } else {
                        user.copy(isCurrentUser = false)
                    }
                    db.userDao().insertUser(userToInsert)
                }
            }
        }
    }



    fun getAllPosts(): LiveData<List<PostWithUser>> = db.postDao().getAllPosts()

    fun getUserPosts(userId: String): LiveData<List<PostWithUser>> = db.postDao().getUserPosts(userId)

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
            executor.execute { db.postDao().deletePost(post) } // Remove from Room
        }, {
            Log.e("Model", "Failed to delete post from Firestore")
        })
    }

    fun updatePost(post: Post, imageUri: Uri?, onSuccess: () -> Unit, onFailure: () -> Unit) {
        if (imageUri != null) {
            firebase.uploadImage(imageUri) { imageUrl ->
                if (imageUrl != null) {
                    val updatedPost = post.copy(imagePathUrl = imageUrl)
                    firebase.updatePostInFirestore(updatedPost, {
                        executor.execute { db.postDao().updatePost(updatedPost) } // Update in Room
                        onSuccess()
                    }, {
                        Log.e("Model", "Failed to update post in Firestore")
                        onFailure()
                    })
                } else {
                    Log.e("Model", "Failed to upload image")
                    onFailure()
                }
            }
        } else {
            firebase.updatePostInFirestore(post, {
                executor.execute { db.postDao().updatePost(post) } // Update in Room
                onSuccess()
            }, {
                Log.e("Model", "Failed to update post in Firestore")
                onFailure()
            })
        }
    }

    fun updateProfilePicture(user: User, imageUri: Uri?, onSuccess: () -> Unit, onFailure: () -> Unit) {
        if (imageUri != null) {
            firebase.uploadImage(imageUri) { imageUrl ->
                if (imageUrl != null) {
                    val updatedUser = user.copy(profilePictureUrl = imageUrl)

                    firebase.updateUserProfilePicture(updatedUser) {
                        executor.execute { db.userDao().insertUser(updatedUser) }

                        onSuccess()
                    }
                } else {
                    Log.e("Model", "Failed to upload image")
                    onFailure()
                }
            }
        }
    }
}

