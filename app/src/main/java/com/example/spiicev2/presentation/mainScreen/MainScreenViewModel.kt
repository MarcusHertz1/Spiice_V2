package com.example.spiicev2.presentation.mainScreen

import android.app.Application
import androidx.lifecycle.viewModelScope
import com.example.spiicev2.data.dataStore.DataStoreManager
import com.example.spiicev2.domain.repository.NotePostRepository
import com.example.spiicev2.presentation.appBase.BaseViewModel
import com.example.spiicev2.presentation.appBase.NavigationCommand
import com.example.spiicev2.presentation.appBase.UiProgress
import com.example.spiicev2.presentation.logIn.LogInErrorType
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
internal class MainScreenViewModel @Inject constructor(
    application: Application,
    private val notePostRepository: NotePostRepository
) : BaseViewModel<MainScreenUiState>() {
    override fun createInitialState() = MainScreenUiState()

    init {
        getAllNotes()
    }

    private val dataStoreManager: DataStoreManager by lazy {
        DataStoreManager(application.applicationContext)
    }

    fun logOut() {
        viewModelScope.launch {
            dataStoreManager.setEmail("")
            navigateTo(NavigationCommand.GoToLogInScreen)
            println("empty email = ${dataStoreManager.getEmail()}")
        }
    }

    fun onBackPressed(): Boolean {
        return if (state.value.checkedNotes.isNotEmpty()) {
            setState {
                copy(
                    checkedNotes = emptyList()
                )
            }
            false
        } else true
    }

    fun setSearchValue(text: String) {
        setState {
            copy(
                searchValue = text
            )
        }
        filterData()
    }

    private var getAllNotesJob: Job? = null
    fun getAllNotes() {
        getAllNotesJob?.cancel()
        getAllNotesJob = viewModelScope.launch {
            setState {
                copy(
                    progress = UiProgress.Loading
                )
            }
            notePostRepository.getAllNotes().collect { result ->
                setState {
                    if (result.isSuccess) {
                        copy(
                            progress = UiProgress.Success,
                            data = result.getOrNull() ?: emptyList()
                        )
                    } else {
                        copy(
                            progress = UiProgress.Error(
                                type = LogInErrorType.OtherError,
                                message = result.exceptionOrNull()?.message.orEmpty()
                            )
                        )
                    }
                }
                filterData()
            }
        }
    }

    fun deleteCheckedNotes() {
        viewModelScope.launch {
            setState {
                copy(
                    progress = UiProgress.Loading
                )
            }
            state.value.checkedNotes.forEach {
                notePostRepository.deleteNotePost(it)
            }
            setState {
                copy(
                    progress = UiProgress.Success,
                    checkedNotes = emptyList()
                )
            }
            getAllNotes()
        }
    }

    fun deleteSingleNote(id: String) {
        viewModelScope.launch {
            setState {
                copy(
                    progress = UiProgress.Loading
                )
            }
            notePostRepository.deleteNotePost(id)
            setState {
                copy(
                    progress = UiProgress.Success,
                    checkedNotes = emptyList()
                )
            }
            getAllNotes()
        }
    }

    fun changeChecked(id: String, isChecked: Boolean) {
        val checkedNotes = state.value.checkedNotes.toMutableList()

        if (isChecked) checkedNotes.remove(id)
        else checkedNotes.add(id)
        setState {
            copy(
                checkedNotes = checkedNotes.toList()
            )
        }
    }

    fun addToChecked(id: String) {
        setState {
            copy(
                checkedNotes = listOf(id)
            )
        }
    }

    fun setSort(sort: Sort) {
        setState {
            copy(
                sort = sort
            )
        }
        filterData()
    }

    private fun filterData(){
        val stateValue = state.value
        val sort = stateValue.sort
        val searchValue = stateValue.searchValue
        var result = stateValue.data.filter { it.title.contains(searchValue, ignoreCase = true) }

        result = when (sort) {
            is Sort.ByTitle -> if (sort.increment) result.sortedBy { it.title }
            else result.sortedByDescending { it.title }

            is Sort.ByDate -> if (!sort.increment) result.sortedBy { it.lastEditDate }
            else result.sortedByDescending { it.lastEditDate }
        }

        println("MR: result.title = ${result.map { it.title }}")
        println("MR: result.date = ${result.map { it.lastEditDate }}")
        setState {
            copy (
                sortedData = result
            )
        }
    }

}