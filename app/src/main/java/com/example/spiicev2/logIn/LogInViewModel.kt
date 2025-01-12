package com.example.spiicev2.logIn

import com.example.spiicev2.appBase.BaseViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
internal class LogInViewModel @Inject constructor() : BaseViewModel<LogInUiState>() {
    override fun createInitialState() = LogInUiState()
}