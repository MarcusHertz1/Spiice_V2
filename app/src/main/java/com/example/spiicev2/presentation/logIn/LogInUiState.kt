package com.example.spiicev2.presentation.logIn

import androidx.compose.runtime.Immutable
import com.example.spiicev2.presentation.appBase.UiState

@Immutable
data class LogInUiState (
    val email: String = "",
    val password: String = "",
    val errorMessage: String = "",
) : UiState