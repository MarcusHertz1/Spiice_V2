package com.example.spiicev2.domain.useCase

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser

class EmailAuthUseCase(private val firebaseAuth: FirebaseAuth) {

    fun logInWithEmail(
        email: String,
        password: String,
        onSuccess: (FirebaseUser) -> Unit,
        onError: (Exception) -> Unit
    ) {
        try {
            firebaseAuth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener { task ->
                    if (task.isSuccessful) {
                        onSuccess(firebaseAuth.currentUser!!)
                    } else {
                        onError(task.exception ?: Exception("Authentication failed"))
                    }
                }
        } catch (e: Exception) {
            onError(e)
        }
    }

    fun signUpWithEmail(
        email: String,
        password: String,
        onSuccess: (FirebaseUser) -> Unit,
        onError: (Exception) -> Unit
    ) {
        try {
            firebaseAuth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener { task ->
                    if (task.isSuccessful) {
                        onSuccess(firebaseAuth.currentUser!!)
                    } else {
                        onError(task.exception ?: Exception("Registration failed"))
                    }
                }
        } catch (e: Exception) {
            onError(e)
        }
    }
}
