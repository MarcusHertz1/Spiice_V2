package com.example.spiicev2.presentation.appBase

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

abstract class BaseViewModel<STATE: UiState> : ViewModel() {

    private val initialState: STATE by lazy { createInitialState() }
    protected abstract fun createInitialState(): STATE

    private val _state: MutableStateFlow<STATE> = MutableStateFlow(initialState)
    val state: StateFlow<STATE> = _state.asStateFlow()

    protected fun setState(newState: STATE.() -> STATE) {
        _state.update {
            newState.invoke(it)
        }
    }

    private val _navigationCommands = MutableSharedFlow<NavigationCommand>()
    val navigationCommands: SharedFlow<NavigationCommand> get() = _navigationCommands

    fun navigateTo(command: NavigationCommand) {
        viewModelScope.launch {
            _navigationCommands.emit(command)
        }
    }
}

sealed class NavigationCommand {
    data object GoToMainScreen : NavigationCommand()
    data object GoToLogInScreen : NavigationCommand()
}