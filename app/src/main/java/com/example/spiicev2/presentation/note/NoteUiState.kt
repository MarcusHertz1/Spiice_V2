package com.example.spiicev2.presentation.note

import androidx.compose.runtime.Immutable
import com.example.spiicev2.presentation.appBase.UiState

@Immutable
data class NoteUiState(
    val text: String = "",
) : UiState