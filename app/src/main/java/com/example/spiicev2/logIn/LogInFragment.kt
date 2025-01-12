package com.example.spiicev2.logIn

import android.os.Bundle
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import androidx.navigation.fragment.findNavController
import com.example.spiicev2.R
import com.example.spiicev2.appBase.BaseFragment
import com.example.spiicev2.ui.theme.SpiiceV2Theme
import kotlinx.coroutines.channels.Channel

class LogInFragment : BaseFragment() {
    //Create - единственная Composable функция, которая находится в теле фрагмента
    @Composable
    override fun Create(arguments: Bundle?, resultChannel: Channel<Bundle>) {
        val navController = findNavController()
        LogInState(navController)
    }

}

//State - Composable функция где определяется ui state
@Composable
private fun LogInState( //
    navController: NavController,
    viewModel: LogInViewModel = viewModel()
) {
    val state by viewModel.state.collectAsState()
    LogInScreen(
        state = state,
        viewModel = viewModel
    )
}

//Screen - Composable функция, где прописывается Scaffold
@Composable
private fun LogInScreen(
    state: LogInUiState,
    viewModel: LogInViewModel,
) {
    Scaffold { padding ->
        LogInScreenState(
            padding = padding,
            state = state,
            onEmailChange = { viewModel.emailChange(it) },
            onPasswordChange = { viewModel.passwordChange(it) },
            onLogInClick = {}
        )
    }
}

@Composable
private fun LogInScreenState(
    padding: PaddingValues = PaddingValues(),
    state: LogInUiState,
    onEmailChange: (String) -> Unit = {},
    onPasswordChange: (String) -> Unit = {},
    onLogInClick: () -> Unit = {}
) {
    Column(
        modifier = Modifier.padding(padding)
            .fillMaxSize()
            ,
        verticalArrangement = Arrangement.spacedBy(8.dp, Alignment.CenterVertically),
        horizontalAlignment = Alignment.CenterHorizontally

    ) {
        OutlinedTextField(
            value = state.email,
            onValueChange = { onEmailChange(it) },
            label = { Text(stringResource(R.string.email)) }
        )
        OutlinedTextField(
            value = state.password,
            onValueChange = { onPasswordChange(it) },
            label = { Text(stringResource(R.string.password)) }
        )
        Button(
            onClick = { onLogInClick() }
        ) {
            Text(stringResource(R.string.logIn))
        }
    }
}

@Preview(backgroundColor = 0xFFFFFFFF)
@Composable
private fun LogInScreenPreview() {
    SpiiceV2Theme {
        LogInScreenState(
            state = LogInUiState(
                email = "jhdafgjh"
            )
        )
    }
}