package com.example.spiicev2.presentation.note

import androidx.lifecycle.viewModelScope
import com.example.spiicev2.domain.repository.NotePostRepository
import com.example.spiicev2.presentation.appBase.BaseViewModel
import com.example.spiicev2.presentation.appBase.NavigationCommand
import com.example.spiicev2.presentation.appBase.UiProgress
import com.example.spiicev2.presentation.logIn.LogInErrorType
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
internal class NoteViewModel @Inject constructor(
    private val notePostRepository: NotePostRepository
) : BaseViewModel<NoteUiState>() {
    override fun createInitialState() = NoteUiState()

    fun setText(text: String) {
        setState {
            copy(
                data = data.copy(text = text)
            )
        }
    }

    fun setTitle(text: String) {
        setState {
            copy(
                data = data.copy(title = text)
            )
        }
    }

    fun incrementTextSize() {
        setState {
            copy(
                textSize = textSize.inc()
            )
        }
    }

    fun decrementTextSize() {
        setState {
            copy(
                textSize = textSize.dec()
            )
        }
    }

    fun saveNote() {
        setState {
            copy(
                progress = UiProgress.Loading
            )
        }
        viewModelScope.launch {
            val result = notePostRepository.addNotePost(state.value.data.copy(lastEditDate = System.currentTimeMillis()))
            if (result.isSuccess) navigateTo(NavigationCommand.GoToMainScreen)
            else if(result.isFailure) setState {
                copy (
                    progress = UiProgress.Error(
                        type = LogInErrorType.OtherError,
                        message = result.exceptionOrNull()?.message.orEmpty()
                    )
                )
            }
        }
    }
}