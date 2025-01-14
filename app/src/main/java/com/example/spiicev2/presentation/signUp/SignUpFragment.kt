package com.example.spiicev2.presentation.signUp

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
import androidx.compose.runtime.LaunchedEffect
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
import com.example.spiicev2.presentation.appBase.BaseFragment
import com.example.spiicev2.presentation.appBase.NavigationCommand
import com.example.spiicev2.presentation.theme.SpiiceV2Theme
import kotlinx.coroutines.channels.Channel

class SignUpFragment : BaseFragment()  {

    @Composable
    override fun Create(arguments: Bundle?, resultChannel: Channel<Bundle>) {
        val navController = findNavController()
        SignUpState(navController)
    }
}

@Composable
private fun SignUpState(
    navController: NavController,
    viewModel: SignUpViewModel = viewModel()
) {
    val state by viewModel.state.collectAsState()
    //val context = LocalContext.current

    LaunchedEffect(Unit) {
        viewModel.navigationCommands.collect { command ->
            when (command) {
                is NavigationCommand.GoToMainScreen -> {
                    navController.navigate(
                        R.id.action_signUpFragment_to_mainScreenFragment,
                    )
                }
            }
        }
    }

    SignUpScreen(
        state = state,
        viewModel = viewModel
    )
}

@Composable
private fun SignUpScreen(
    state: SignUpUiState,
    viewModel: SignUpViewModel,
) {
    Scaffold { padding ->
        SignUpScreenState(
            padding = padding,
            state = state,
            onEmailSet = { viewModel.emailSet(it) },
            onPasswordSet = { viewModel.passwordSet(it) },
            onSignUpClick = {viewModel.signUp()}
        )
    }
}

@Composable
private fun SignUpScreenState(
    padding: PaddingValues = PaddingValues(),
    state: SignUpUiState,
    onEmailSet: (String) -> Unit = {},
    onPasswordSet: (String) -> Unit = {},
    onSignUpClick: () -> Unit = {}
) {
    Column(
        modifier = Modifier
            .padding(padding)
            .fillMaxSize(),
        verticalArrangement = Arrangement.spacedBy(8.dp, Alignment.CenterVertically),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        OutlinedTextField(
            value = state.email,
            onValueChange = { onEmailSet(it) },
            label = { Text(stringResource(R.string.email)) }
        )
        OutlinedTextField(
            value = state.password,
            onValueChange = { onPasswordSet(it) },
            label = { Text(stringResource(R.string.password)) }
        )
        Button(
            onClick = { onSignUpClick() }
        ) {
            Text(stringResource(R.string.signUp))
        }
    }
}

@Preview(backgroundColor = 0xFFFFFFFF)
@Composable
private fun SignUpScreenPreview() {
    SpiiceV2Theme {
        SignUpScreenState(
            state = SignUpUiState(
                email = "jhdafgjh"
            )
        )
    }
}