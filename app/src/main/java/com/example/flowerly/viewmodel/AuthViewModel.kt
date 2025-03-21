package com.example.flowerly.viewmodel

import android.util.Log
import androidx.lifecycle.*
import com.example.flowerly.dao.UserDao
import com.example.flowerly.model.Model
import com.example.flowerly.model.User
import com.google.firebase.auth.FirebaseAuth
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class AuthViewModel(private val userDao: UserDao) : ViewModel() {

    private val auth: FirebaseAuth = FirebaseAuth.getInstance()
    private val _authUser = MutableLiveData<User?>()

    val user: LiveData<User?> = _authUser.switchMap { authUser ->
        authUser?.let { userDao. getUser(it.id) } ?: MutableLiveData(null)
    }

    val authResult = MutableLiveData<Boolean>()
    val validationMessage = MutableLiveData<String>()
    val email = MutableLiveData<String>()
    val password = MutableLiveData<String>()

    init {
        loadLocalUser()
    }

    private fun loadLocalUser() {
        viewModelScope.launch(Dispatchers.IO) {
            val firebaseUser = auth.currentUser ?: return@launch
            val localUser = userDao.getUser(firebaseUser.uid)?.value
            val allUsers = userDao.getAllUsers().value

            Log.d("Tag", "Users $allUsers")

            if (localUser != null) {
                _authUser.postValue(localUser)
            } else {
                val user = User(firebaseUser.uid, firebaseUser.email ?: "Unknown User", "ic_profile.png")
                saveUserLocally(user)
                _authUser.postValue(user)
            }
        }
    }

    fun login() {
        val emailValue = email.value?.trim().orEmpty()
        val passwordValue = password.value.orEmpty()

        if (!isValidEmail(emailValue)) {
            validationMessage.value = "Invalid email format"
            return
        }

        auth.signInWithEmailAndPassword(emailValue, passwordValue)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    auth.currentUser?.let { firebaseUser ->
                        val newUser = User(firebaseUser.uid, firebaseUser.email.orEmpty(), "ic_profile.png")
                        _authUser.postValue(newUser)
                    }
                }
                Model.instance.refreshAllUsers()
                authResult.value = task.isSuccessful
            }
    }

    fun validateAndSignup() {
        val emailValue = email.value?.trim().orEmpty()
        val passwordValue = password.value.orEmpty()

        when {
            !isValidEmail(emailValue) -> validationMessage.value = "Invalid email format"
            passwordValue.length < 6 -> validationMessage.value = "Password must be at least 6 characters long"
            else -> signup(emailValue, passwordValue)
        }
    }

    private fun signup(email: String, password: String) {
        auth.createUserWithEmailAndPassword(email, password)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    auth.currentUser?.let { firebaseUser ->
                        val newUser = User(firebaseUser.uid, email, "ic_profile.png")
                        saveUserLocally(newUser)
                        _authUser.postValue(newUser)
                    }
                }
                authResult.value = task.isSuccessful
            }
    }

    private fun saveUserLocally(user: User) {
        viewModelScope.launch(Dispatchers.IO) {
            userDao.insertUser(user)
        }
    }

    fun logout() {
        auth.signOut()
        _authUser.postValue(null)
    }

    fun updateUsername(newUsername: String) {
        viewModelScope.launch(Dispatchers.IO) {
            _authUser.value?.let { currentUser ->
                userDao.updateUsername(currentUser.id, newUsername) // ✅ Only update the username
                _authUser.postValue(currentUser.copy(username = newUsername)) // ✅ Update LiveData
            }
        }
    }

    private fun isValidEmail(email: String): Boolean {
        return email.isNotEmpty() && android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()
    }
}
