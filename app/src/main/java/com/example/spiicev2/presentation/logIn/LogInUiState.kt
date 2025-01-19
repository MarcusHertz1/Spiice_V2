package com.example.spiicev2.presentation.logIn

import androidx.compose.runtime.Immutable
import com.example.spiicev2.presentation.appBase.UiProgress
import com.example.spiicev2.presentation.appBase.UiState

@Immutable
data class LogInUiState (
    val email: String = "",
    val password: String = "",
    val progress : UiProgress = UiProgress.Success
) : UiState