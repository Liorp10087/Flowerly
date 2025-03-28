package com.example.flowerly.viewmodel

import android.app.Application
import android.content.Context
import android.net.Uri
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.example.flowerly.model.FirebaseModel
import com.example.flowerly.model.Model
import com.example.flowerly.model.User
import kotlinx.coroutines.launch

class UserViewModel(application: Application) : AndroidViewModel(application) {

    private val _currentUser = MutableLiveData<User?>()
    val currentUser: LiveData<User?> get() = _currentUser

    private val _loginStatus = MutableLiveData<Boolean>()
    val loginStatus: LiveData<Boolean> get() = _loginStatus

    private val _signupStatus = MutableLiveData<Boolean>()
    val signupStatus: LiveData<Boolean> get() = _signupStatus

    init {
        loadCurrentUser()
    }
    fun loadCurrentUser() {
        Model.instance.getCurrentUserFromCache().observeForever { cachedUser ->
            if (cachedUser != null) {
                _currentUser.value = cachedUser
            } else {

                FirebaseModel.firebaseUserLiveData.observeForever { firebaseUser ->
                    if (firebaseUser != null) {
                        fetchUserDataFromFirebase()
                    } else {
                        _currentUser.value = null
                    }
                }
            }
        }
    }

    private fun fetchUserDataFromFirebase() {
        viewModelScope.launch {
            val firebaseUserData = Model.instance.getCurrentUserFromFirebase()
            if (firebaseUserData != null) {
                Model.instance.setCurrentUser(firebaseUserData)
                _currentUser.value = firebaseUserData
            } else {
                _currentUser.value = null
            }
        }
    }

    fun updateCurrentUser(user: User) {
        _currentUser.value = user
        Model.instance.setCurrentUser(user)
    }

    fun updateUserUsername(userId: String, newUsername: String, context: Context, callback: () -> Unit) {
        Model.instance.updateUserUsername(userId, newUsername, context) {
            callback()
        }
    }

    fun updateProfilePicture(user: User, imageUri: Uri?, onSuccess: () -> Unit, onFailure: () -> Unit) {
        Model.instance.updateProfilePicture(user, imageUri, onSuccess, onFailure)
    }

    fun signOut() {
        Model.instance.signOut()
        _currentUser.value = null
    }

    fun refreshAllUsers() {
        Model.instance.refreshAllUsers()
    }

    fun login(email: String, password: String, context: Context) {
        Model.instance.login(email, password, context) { success ->
            _loginStatus.value = success
        }
    }

    fun signup(email: String, password: String, context: Context) {
        Model.instance.signup(email, password, context) { success ->
            _signupStatus.value = success
        }
    }

}
