package com.example.spiicev2.presentation.note

import androidx.compose.runtime.Immutable
import com.example.spiicev2.domain.repository.NoteData
import com.example.spiicev2.presentation.appBase.UiProgress
import com.example.spiicev2.presentation.appBase.UiState

@Immutable
data class NoteUiState(
    val textSize: Int = 14,
    val data: NoteData = NoteData(),
    val progress: UiProgress = UiProgress.Success
) : UiState