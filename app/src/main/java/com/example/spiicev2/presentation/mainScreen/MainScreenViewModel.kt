package com.example.spiicev2.presentation.mainScreen

import com.example.spiicev2.presentation.appBase.BaseViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
internal class MainScreenViewModel @Inject constructor(
) : BaseViewModel<MainScreenUiState>() {
    override fun createInitialState() = MainScreenUiState()
}