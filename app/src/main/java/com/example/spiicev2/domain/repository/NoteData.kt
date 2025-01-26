package com.example.spiicev2.domain.repository

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class NoteData(
    val id: String = "",
    val title: String = "",
    val text: String = "",
    val lastEditDate: Long = 0L,
    val userId: String = ""
) : Parcelable
