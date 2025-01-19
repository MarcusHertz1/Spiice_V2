package com.example.spiicev2.presentation.note

import com.example.spiicev2.presentation.appBase.BaseViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
internal class NoteViewModel @Inject constructor(
) : BaseViewModel<NoteUiState>() {
    override fun createInitialState() = NoteUiState()

    fun setText(text:String){
        setState {
            copy(
                text = text
            )
        }
    }
}