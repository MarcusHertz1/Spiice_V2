package com.example.spiicev2.domain.repository

data class NoteData(
    val id: String = "",
    val title: String = "",
    val text: String = "",
    val lastEditDate: Long = 0L
)
