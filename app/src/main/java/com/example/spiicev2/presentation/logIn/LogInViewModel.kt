package com.example.spiicev2.presentation.logIn

import android.app.Application
import androidx.lifecycle.viewModelScope
import com.example.spiicev2.data.dataStore.DataStoreManager
import com.example.spiicev2.domain.useCase.EmailAuthUseCase
import com.example.spiicev2.presentation.appBase.BaseViewModel
import com.example.spiicev2.presentation.appBase.NavigationCommand
import com.example.spiicev2.presentation.appBase.UiProgress
import com.google.firebase.auth.FirebaseAuth
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
internal class LogInViewModel @Inject constructor(
    application: Application
) : BaseViewModel<LogInUiState>() {
    override fun createInitialState() = LogInUiState()

    private val dataStoreManager: DataStoreManager by lazy {
        DataStoreManager(application.applicationContext)
    }

    private val emailAuthUseCase by lazy {
        EmailAuthUseCase(firebaseAuth = FirebaseAuth.getInstance())
    }

    fun emailChange(email: String) {
        setState {
            copy(
                email = email
            )
        }
    }

    fun passwordChange(password: String) {
        setState {
            copy(
                password = password
            )
        }
    }

    fun logIn() {
        setState {
            copy(
                progress = UiProgress.Loading
            )
        }
        val email = state.value.email
        val password = state.value.password

        when {
            email.isBlank() -> {
                setState {
                    copy(
                        progress = UiProgress.Error(
                            type = LogInErrorType.EmailError,
                            message = "Email must not be empty"
                        )
                    )
                }
            }

            password.isBlank() -> {
                setState {
                    copy(
                        progress = UiProgress.Error(
                            type = LogInErrorType.PasswordError,
                            message = "Password must not be empty"
                        )
                    )
                }
            }

            else -> {
                println("email: $email, password: $password")
                emailAuthUseCase.logInWithEmail(
                    email = email,
                    password = password,
                    onSuccess = { user ->
                        println("Success")
                        viewModelScope.launch {
                            dataStoreManager.setEmail(user.email.orEmpty())
                            navigateTo(NavigationCommand.GoToMainScreen)
                            setState {
                                copy(
                                    progress = UiProgress.Success
                                )
                            }
                        }
                    },
                    onError = { error ->
                        println("error")
                        setState {
                            copy(
                                progress = UiProgress.Error(
                                    type = LogInErrorType.OtherError,
                                    message = "Firebase error: ${error.message}"
                                )
                            )
                        }
                    }
                )
            }
        }
    }
}