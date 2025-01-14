package com.example.spiicev2.presentation.mainScreen

import android.os.Bundle
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.outlined.ArrowBack
import androidx.compose.material3.Card
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import androidx.navigation.fragment.findNavController
import com.example.spiicev2.presentation.appBase.BaseFragment
import com.example.spiicev2.presentation.theme.SpiiceV2Theme
import kotlinx.coroutines.channels.Channel

class MainScreenFragment : BaseFragment() {
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
    val state by viewModel.state.collectAsState()
    MainScreenScreen(
        state = state,
        //viewModel = viewModel,
        logOut = { viewModel.logOut() },
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun MainScreenScreen(
    state: MainScreenUiState,
    //viewModel: MainScreenViewModel,
    logOut: () -> Unit = {},
) {
    Scaffold(
        topBar = {
            TopAppBar(
                navigationIcon = {
                    IconButton(
                        onClick = { logOut() },
                        modifier = Modifier
                            .size(48.dp)
                    ) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Outlined.ArrowBack,
                            contentDescription = "Вернуться назад",
                        )
                    }
                },
                title = { },
            )
        }
    ) { padding ->
        MainScreenScreenState(
            padding = padding,
            state = state
        )
    }
}

@Composable
private fun MainScreenScreenState(
    padding: PaddingValues = PaddingValues(),
    state: MainScreenUiState
) {

}

@Composable
private fun Note() {
    Card {

    }
}

@Preview(backgroundColor = 0xFFFFFFFF)
@Composable
private fun MainScreenScreenPreview() {
    SpiiceV2Theme {
        MainScreenScreen(
            state = MainScreenUiState()
        )
    }
}