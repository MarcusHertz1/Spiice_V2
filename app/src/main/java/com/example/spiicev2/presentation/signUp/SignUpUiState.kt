package com.example.spiicev2.presentation.signUp

import androidx.compose.runtime.Immutable
import com.example.spiicev2.presentation.appBase.UiState

@Immutable
data class SignUpUiState(
    val email: String = "",
    val password: String = "",
    val errorMessage: String = "",
) : UiState