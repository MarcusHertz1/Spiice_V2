package com.example.spiicev2.logIn

import androidx.compose.runtime.Immutable
import com.example.spiicev2.appBase.UiState

@Immutable
data class LogInUiState (
    val email: String = "",
    val password: String = "",
) : UiState