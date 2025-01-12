package com.example.spiicev2.presentation.mainScreen

import android.os.Bundle
import androidx.compose.runtime.Composable
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import androidx.navigation.fragment.findNavController
import com.example.spiicev2.presentation.appBase.BaseFragment
import kotlinx.coroutines.channels.Channel

class MainScreenFragment: BaseFragment() {
    @Composable
    override fun Create(arguments: Bundle?, resultChannel: Channel<Bundle>) {
        val navController = findNavController()
        MainScreenState(navController)
    }
}

@Composable
private fun MainScreenState(
    navController: NavController,
    viewModel: MainScreenViewModel = viewModel()
) {

}