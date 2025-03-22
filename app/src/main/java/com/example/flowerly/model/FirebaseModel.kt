package com.example.flowerly.model

import android.content.Context
import android.net.Uri
import android.util.Log
import android.widget.Toast
import androidx.lifecycle.LiveData
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.firestore.FirebaseFirestoreSettings
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.FirebaseStorage

object FirebaseModel {
    private val db by lazy { Firebase.firestore }
    private const val USERS_COLLECTION = "users"
    private const val POSTS_COLLECTION = "posts"
    private val auth: FirebaseAuth = FirebaseAuth.getInstance()
    private val storage = FirebaseStorage.getInstance()

    init {
        val settings = FirebaseFirestoreSettings.Builder()
            .setPersistenceEnabled(false)
            .build()
        db.firestoreSettings = settings
    }

    val firebaseUserLiveData: LiveData<FirebaseUser?> = object : LiveData<FirebaseUser?>() {
        private val authListener = FirebaseAuth.AuthStateListener { auth ->
            value = auth.currentUser
        }

        override fun onActive() {
            super.onActive()
            value = auth.currentUser
            auth.addAuthStateListener(authListener)
        }

        override fun onInactive() {
            super.onInactive()
            auth.removeAuthStateListener(authListener)
        }
    }

    fun getCurrentUser(): FirebaseUser? = auth.currentUser

    fun signIn(
        context: Context,
        email: String,
        password: String,
        callback: (Boolean, FirebaseUser?) -> Unit
    ) {
        auth.signInWithEmailAndPassword(email, password)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    callback(true, auth.currentUser)
                } else {
                    showToast(context, task.exception?.localizedMessage ?: "Login failed")
                    callback(false, null)
                }
            }
    }

    fun signUp(
        context: Context,
        email: String,
        password: String,
        callback: (Boolean, FirebaseUser?) -> Unit
    ) {
        auth.createUserWithEmailAndPassword(email, password)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    callback(true, auth.currentUser)
                } else {
                    showToast(context, task.exception?.localizedMessage ?: "Signup failed")
                    callback(false, null)
                }
            }
    }

    fun signOut() {
        auth.signOut()
    }

    fun addUserToDB(user: User, callback: () -> Unit) {
        db.collection(USERS_COLLECTION).document(user.id).set(user.json)
            .addOnSuccessListener { callback() }
    }

    fun isUsernameExists(username: String, callback: (Boolean) -> Unit) {
        db.collection(USERS_COLLECTION)
            .whereEqualTo("username", username)
            .get()
            .addOnSuccessListener { callback(!it.isEmpty) }
            .addOnFailureListener { callback(false) }
    }

    fun updateUserUsername(
        context: Context,
        userId: String,
        newUsername: String,
        callback: (User?) -> Unit
    ) {
        if (newUsername.isBlank()) {
            showToast(context, "Username cannot be empty")
            callback(null)
            return
        }

        isUsernameExists(newUsername) { exists ->
            if (exists) {
                showToast(context, "Username is already taken")
                callback(null)
            } else {
                val userRef = db.collection(USERS_COLLECTION).document(userId)
                userRef.update(
                    mapOf(
                        "username" to newUsername,
                        "lastUpdated" to System.currentTimeMillis()
                    )
                )
                    .addOnSuccessListener {
                        userRef.get().addOnSuccessListener {
                            val updatedUser = it.data?.let(User::fromJSON)
                            callback(updatedUser)
                        }.addOnFailureListener { e ->
                            showToast(context, "Failed to retrieve updated user")
                            callback(null)
                        }
                    }
                    .addOnFailureListener { e ->
                        showToast(context, "Failed to update username")
                        callback(null)
                    }
            }
        }
    }

    fun getAllUsers(callback: (List<User>) -> Unit) {
        db.collection(USERS_COLLECTION).get()
            .addOnSuccessListener { snapshot ->
                callback(snapshot.mapNotNull { User.fromJSON(it.data) })
            }.addOnFailureListener {
                callback(emptyList())
            }
    }

    fun getUserById(id: String, callback: (User?) -> Unit) {
        db.collection(USERS_COLLECTION).document(id).get()
            .addOnSuccessListener {
                callback(it.data?.let(User::fromJSON))
            }.addOnFailureListener {
                callback(null)
            }
    }

    private fun showToast(context: Context, message: String) {
        Toast.makeText(context, message, Toast.LENGTH_SHORT).show()
    }

    fun getAllPosts(callback: (List<Post>, Set<String>) -> Unit) {
        db.collection(POSTS_COLLECTION).get().addOnSuccessListener { snapshot ->
            val posts = mutableListOf<Post>()
            val userIds = mutableSetOf<String>()
            snapshot.documents.forEach { doc ->
                val data = doc.data ?: return@forEach
                val userRef = doc.getDocumentReference("user")
                posts.add(Post(
                    id = doc.id,
                    title = data["title"] as? String ?: "",
                    description = data["description"] as? String ?: "",
                    imagePathUrl = data["imagePathUrl"] as? String ?: "",
                    userId = userRef?.id ?: ""
                ))
                userRef?.id?.let { userIds.add(it) }
            }
            callback(posts, userIds)
        }
    }

    fun uploadImage(imageUri: Uri, onComplete: (String?) -> Unit) {
        val ref = storage.reference.child("images/${System.currentTimeMillis()}.jpg")
        ref.putFile(imageUri)
            .addOnSuccessListener { ref.downloadUrl.addOnSuccessListener { uri -> onComplete(uri.toString()) } }
            .addOnFailureListener { onComplete(null) }
    }

    fun addPostToFirestore(post: Post, onFailure: () -> Unit) {
        val postData = hashMapOf(
            "id" to post.id,
            "title" to post.title,
            "description" to post.description,
            "imagePathUrl" to post.imagePathUrl,
            "user" to db.collection("users").document(post.userId)
        )
        db.collection(POSTS_COLLECTION).document(post.id).set(postData)
            .addOnFailureListener { onFailure() }
    }

    fun deletePostFromFirestore(postId: String, onSuccess: () -> Unit, onFailure: () -> Unit) {
        db.collection(POSTS_COLLECTION).document(postId).delete()
            .addOnSuccessListener { onSuccess() }
            .addOnFailureListener { onFailure() }
    }

    fun getUsersByIds(ids: Set<String>, callback: (Map<String, User>) -> Unit) {
        val userMap = mutableMapOf<String, User>()
        if (ids.isEmpty()) {
            callback(userMap)
            return
        }
        ids.forEach { id ->
            db.collection(USERS_COLLECTION).document(id).get()
                .addOnSuccessListener { doc ->
                    doc.toObject(User::class.java)?.let { userMap[id] = it }
                    if (userMap.size == ids.size) callback(userMap)
                }
                .addOnFailureListener {
                    if (userMap.size == ids.size) callback(userMap)
                }
        }
    }

    fun updatePostInFirestore(post: Post, onSuccess: () -> Unit, onFailure: () -> Unit) {
        val postData = hashMapOf(
            "id" to post.id,
            "title" to post.title,
            "description" to post.description,
            "imagePathUrl" to post.imagePathUrl,
            "user" to db.collection("users").document(post.userId)
        )

        db.collection(POSTS_COLLECTION).document(post.id).set(postData)
            .addOnSuccessListener {
                onSuccess()
                Log.d("FirebaseModel", "Post successfully updated in Firestore")
            }
            .addOnFailureListener {
                onFailure()
                Log.e("FirebaseModel", "Failed to update post in Firestore")
            }
    }

    fun updateUserProfilePicture(updatedUser: User, callback: () -> Unit) {
        val userRef = Firebase.firestore.collection("users").document(updatedUser.id)
        userRef.update("profilePictureUrl", updatedUser.profilePictureUrl)
            .addOnSuccessListener { callback() }
            .addOnFailureListener {
                Log.d("FirebaseModel", "Picture successfully updated in Firestore")
            }
    }

}
