package com.example.flowerly.viewmodel

import android.util.Patterns
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.google.firebase.Firebase
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.auth

class AuthViewModel : ViewModel() {
    val user: LiveData<FirebaseUser?> get() = _user
    val authResult = MutableLiveData<Boolean>()
    val validationMessage = MutableLiveData<String>()
    private val auth: FirebaseAuth = Firebase.auth
    val email = MutableLiveData<String>()
    val password = MutableLiveData<String>()
    private val _user = MutableLiveData<FirebaseUser?>()

    init {
        _user.value = auth.currentUser
    }

    fun login() {
        val emailValue = email.value ?: return
        val passwordValue = password.value ?: return
        auth.signInWithEmailAndPassword(emailValue, passwordValue)
            .addOnCompleteListener { task ->
                _user.value = auth.currentUser
                authResult.value = task.isSuccessful
            }
    }

    fun validateAndSignup() {
        val emailValue = email.value?.trim() ?: ""
        val passwordValue = password.value ?: ""

        if (!isValidEmail(emailValue)) {
            validationMessage.value = "Invalid email format"
            return
        }

        if (passwordValue.length < 6) {
            validationMessage.value = "Password must be at least 6 characters long"
            return
        }

        signup(emailValue, passwordValue)
    }

    private fun isValidEmail(email: String): Boolean {
        return email.isNotEmpty() && Patterns.EMAIL_ADDRESS.matcher(email).matches()
    }

    private fun signup(email: String, password: String) {
        auth.createUserWithEmailAndPassword(email, password)
            .addOnCompleteListener { task ->
                _user.value = auth.currentUser
                authResult.value = task.isSuccessful
            }
    }

    fun logout() {
        auth.signOut()
        _user.value = null
    }
}