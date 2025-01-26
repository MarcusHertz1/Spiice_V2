package com.example.spiicev2.presentation.mainScreen

sealed interface Sort {
    data class ByTitle (val increment: Boolean) : Sort
    data class ByDate (val increment: Boolean) : Sort
}