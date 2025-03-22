package com.example.flowerly.viewmodel

import android.app.Application
import android.content.Context
import android.net.Uri
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.example.flowerly.model.Model
import com.example.flowerly.model.User
import kotlinx.coroutines.launch

class UserViewModel(application: Application) : AndroidViewModel(application) {

    private val _currentUser = MutableLiveData<User?>()
    val currentUser: LiveData<User?> get() = _currentUser

    init {
        loadCurrentUser()
    }
    fun loadCurrentUser() {
        viewModelScope.launch {
            val user = Model.instance.getCurrentUser()
            _currentUser.value = user.value
        }
    }

    fun updateCurrentUser(user: User) {
        _currentUser.value = user
    }

    fun updateUserUsername(userId: String, newUsername: String, context: Context, callback: () -> Unit) {
        Model.instance.updateUserUsername(userId, newUsername, context) {
            callback()
        }
    }

    fun updateProfilePicture(user: User, imageUri: Uri?, onSuccess: () -> Unit, onFailure: () -> Unit) {
        Model.instance.updateProfilePicture(user, imageUri, onSuccess, onFailure)
    }

    fun logout() {

    }

    fun refreshAllUsers() {
        Model.instance.refreshAllUsers()
    }
}
