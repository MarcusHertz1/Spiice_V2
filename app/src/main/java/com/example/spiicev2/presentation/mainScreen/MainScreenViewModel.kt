package com.example.spiicev2.presentation.mainScreen

import android.app.Application
import androidx.lifecycle.viewModelScope
import com.example.spiicev2.data.dataStore.DataStoreManager
import com.example.spiicev2.presentation.appBase.BaseViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
internal class MainScreenViewModel @Inject constructor(
    application: Application
) : BaseViewModel<MainScreenUiState>() {
    override fun createInitialState() = MainScreenUiState()

    private val dataStoreManager: DataStoreManager by lazy {
        DataStoreManager(application.applicationContext)
    }

    fun logOut() {
        viewModelScope.launch {
            dataStoreManager.setEmail("")
        }
    }
}