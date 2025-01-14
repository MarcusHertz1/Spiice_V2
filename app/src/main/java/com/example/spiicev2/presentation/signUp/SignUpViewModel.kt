package com.example.spiicev2.presentation.signUp

import android.app.Application
import androidx.lifecycle.viewModelScope
import com.example.spiicev2.data.dataStore.DataStoreManager
import com.example.spiicev2.domain.useCase.EmailAuthUseCase
import com.example.spiicev2.presentation.appBase.BaseViewModel
import com.example.spiicev2.presentation.appBase.NavigationCommand
import com.google.firebase.auth.FirebaseAuth
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
internal class SignUpViewModel @Inject constructor(
    application: Application
) : BaseViewModel<SignUpUiState>() {
    override fun createInitialState() = SignUpUiState()

    private val dataStoreManager: DataStoreManager by lazy {
        DataStoreManager(application.applicationContext)
    }

    private val emailAuthUseCase by lazy {
        EmailAuthUseCase(firebaseAuth = FirebaseAuth.getInstance())
    }

    fun emailSet(email: String) {
        setState {
            copy(
                email = email,
                errorMessage = ""
            )
        }
    }

    fun passwordSet(password: String) {
        setState {
            copy(
                password = password,
                errorMessage = ""
            )
        }
    }

    fun signUp() {
        setState {
            copy(
                errorMessage = ""
            )
        }
        val email = state.value.email
        val password = state.value.password
        if (email.isNotBlank() && password.isNotBlank()) {
            emailAuthUseCase.signUpWithEmail(
                email = email,
                password = password,
                onSuccess = { user ->
                    viewModelScope.launch {
                        dataStoreManager.setEmail(user.email.orEmpty())
                        navigateTo(NavigationCommand.GoToMainScreen)
                    }
                },
                onError = { error ->
                    setState {
                        copy(
                            errorMessage = "Firebase error: ${error.message}"
                        )
                    }
                }
            )
        } else {
            setState {
                copy(
                    errorMessage = "Email and password must not be empty"
                )
            }
        }

    }
}