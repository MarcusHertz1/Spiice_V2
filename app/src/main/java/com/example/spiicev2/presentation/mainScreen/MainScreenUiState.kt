package com.example.spiicev2.presentation.mainScreen

import androidx.compose.runtime.Immutable
import com.example.spiicev2.domain.repository.NoteData
import com.example.spiicev2.presentation.appBase.UiProgress
import com.example.spiicev2.presentation.appBase.UiState

@Immutable
data class MainScreenUiState(
    val email: String = "",
    val checkedNotes: List<String> = emptyList(),
    val progress: UiProgress = UiProgress.Loading,
    val data: List<NoteData> = emptyList(),
    val searchValue: String = ""
) : UiState