package com.example.spiicev2.presentation.signUp

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
                email = email
            )
        }
    }

    fun passwordSet(password: String) {
        setState {
            copy(
                password = password
            )
        }
    }

    fun confirmPasswordSet(password: String) {
        setState {
            copy(
                confirmPassword = password
            )
        }
    }

    fun signUp() {
        setState {
            copy(
                progress = UiProgress.Loading
            )
        }
        val email = state.value.email
        val password = state.value.password
        val confirmPassword = state.value.confirmPassword

        when {
            email.isBlank() -> {
                setState {
                    copy(
                        progress = UiProgress.Error(
                            type = SignUpErrorType.EmailError,
                            message = "Email must not be empty"
                        )
                    )
                }
            }

            password.isBlank() -> {
                setState {
                    copy(
                        progress = UiProgress.Error(
                            type = SignUpErrorType.PasswordError,
                            message = "Password must not be empty"
                        )
                    )
                }
            }

            password != confirmPassword -> {
                setState {
                    copy(
                        progress = UiProgress.Error(
                            type = SignUpErrorType.PasswordAndConfirmPasswordError,
                            message = "Password and confirm password must be the same"
                        )
                    )
                }
            }

            else -> {
                println("email: $email, password: $password")
                emailAuthUseCase.signUpWithEmail(
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
                                    type = SignUpErrorType.OtherError,
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