package com.example.spiicev2.presentation.note

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.viewModelScope
import com.example.spiicev2.domain.repository.NoteData
import com.example.spiicev2.domain.repository.NotePostRepository
import com.example.spiicev2.presentation.appBase.Arguments.NOTE_DATA
import com.example.spiicev2.presentation.appBase.BaseViewModel
import com.example.spiicev2.presentation.appBase.NavigationCommand
import com.example.spiicev2.presentation.appBase.UiProgress
import com.example.spiicev2.presentation.logIn.LogInErrorType
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
internal class NoteViewModel @Inject constructor(
    private val notePostRepository: NotePostRepository,
    savedStateHandle: SavedStateHandle
) : BaseViewModel<NoteUiState>() {
    override fun createInitialState() = NoteUiState()

    private val noteData by lazy {
        savedStateHandle.get<NoteData?>(NOTE_DATA)
    }

    init {
        noteData?.let {
            setState {
                copy (
                    data = it
                )
            }
        }
    }

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
            val data = state.value.data
            val result =
                if(data.id.isBlank())
                    notePostRepository.addNotePost(data.copy(lastEditDate = System.currentTimeMillis()))
            else notePostRepository.updateNotePost(data.id, data)
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