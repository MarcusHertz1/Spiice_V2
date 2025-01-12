package com.example.spiicev2.presentation.mainScreen

import androidx.compose.runtime.Immutable
import com.example.spiicev2.presentation.appBase.UiState

@Immutable
data class MainScreenUiState(
    val email: String = "",
) : UiState