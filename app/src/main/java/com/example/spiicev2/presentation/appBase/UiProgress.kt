package com.example.spiicev2.presentation.appBase

interface UiProgress {
    data object Loading: UiProgress
    data object Success: UiProgress
    data class Error (
        val type: ErrorType,
        val message: String,
    ) : UiProgress
}

interface ErrorType