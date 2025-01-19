package com.example.spiicev2.presentation.signUp

import android.os.Bundle
import android.widget.Toast
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.snapshotFlow
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import androidx.navigation.fragment.findNavController
import com.example.spiicev2.R
import com.example.spiicev2.presentation.appBase.BaseFragment
import com.example.spiicev2.presentation.appBase.NavigationCommand
import com.example.spiicev2.presentation.appBase.UiProgress
import com.example.spiicev2.presentation.theme.SpiiceV2Theme
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.filterNotNull

class SignUpFragment : BaseFragment() {

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
    val context = LocalContext.current

    LaunchedEffect(Unit) {
        snapshotFlow { state.progress }
            .filterNotNull()
            .collectLatest {
                (it as? UiProgress.Error)?.let { error ->
                    if (error.type == SignUpErrorType.OtherError)
                        Toast.makeText(
                            context,
                            error.message,
                            Toast.LENGTH_LONG
                        ).show()
                }
            }

        viewModel.navigationCommands.collect { command ->
            when (command) {
                is NavigationCommand.GoToMainScreen -> {
                    navController.navigate(
                        R.id.action_signUpFragment_to_mainScreenFragment,
                    )
                }

                else -> {}
            }
        }
    }

    SignUpScreen(
        state = state,
        viewModel = viewModel,
        goBack = {
            navController.popBackStack()
        }
    )
}

@Composable
private fun SignUpScreen(
    state: SignUpUiState,
    viewModel: SignUpViewModel,
    goBack: () -> Unit = {}
) {
    Scaffold { padding ->
        SignUpScreenState(
            padding = padding,
            state = state,
            onEmailSet = { viewModel.emailSet(it) },
            onPasswordSet = { viewModel.passwordSet(it) },
            onConfirmPasswordSet = { viewModel.confirmPasswordSet(it) },
            onSignUpClick = { viewModel.signUp() },
            goBack = goBack
        )
    }
}

@Composable
private fun SignUpScreenState(
    padding: PaddingValues = PaddingValues(),
    state: SignUpUiState,
    onEmailSet: (String) -> Unit = {},
    onPasswordSet: (String) -> Unit = {},
    onConfirmPasswordSet: (String) -> Unit = {},
    onSignUpClick: () -> Unit = {},
    goBack: () -> Unit = {}
) {
    Column(
        modifier = Modifier
            .padding(padding)
            .fillMaxSize(),
        verticalArrangement = Arrangement.spacedBy(8.dp, Alignment.CenterVertically),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        val error = state.progress as? UiProgress.Error
        OutlinedTextField(
            value = state.email,
            onValueChange = { onEmailSet(it) },
            label = { Text(stringResource(R.string.email)) },
            supportingText = {
                if (error != null && error.type == SignUpErrorType.EmailError)
                    Text(
                        text = error.message,
                        color = Color.Red
                    )
            },
            isError = error != null && error.type == SignUpErrorType.EmailError
        )
        OutlinedTextField(
            value = state.password,
            onValueChange = { onPasswordSet(it) },
            label = { Text(stringResource(R.string.password)) },
            supportingText = {
                if (error != null && (error.type == SignUpErrorType.PasswordError ||
                            error.type == SignUpErrorType.PasswordAndConfirmPasswordError)
                )
                    Text(
                        text = error.message,
                        color = Color.Red
                    )
            },
            isError = error != null && (error.type == SignUpErrorType.PasswordError ||
                    error.type == SignUpErrorType.PasswordAndConfirmPasswordError)
        )
        OutlinedTextField(
            value = state.confirmPassword,
            onValueChange = { onConfirmPasswordSet(it) },
            label = { Text(stringResource(R.string.confirmPassword)) },
            supportingText = {
                if (error != null && error.type == SignUpErrorType.PasswordAndConfirmPasswordError)
                    Text(
                        text = error.message,
                        color = Color.Red
                    )
            },
            isError = error != null && error.type == SignUpErrorType.PasswordAndConfirmPasswordError
        )
        Button(
            onClick = { onSignUpClick() },
            enabled = state.progress !is UiProgress.Loading
        ) {
            if (state.progress !is UiProgress.Loading)
                Text(stringResource(R.string.signUp))
            else CircularProgressIndicator(modifier = Modifier.size(20.dp))
        }
        Text(
            modifier = Modifier.clickable {
                goBack()
            },
            text = stringResource(R.string.signUpToLogIn)
        )
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

@Preview(backgroundColor = 0xFFFFFFFF)
@Composable
private fun SignUpScreenErrorPreview() {
    SpiiceV2Theme {
        SignUpScreenState(
            state = SignUpUiState(
                email = "jhdafgjh",
                progress = UiProgress.Error(
                    type = SignUpErrorType.EmailError,
                    message = "Email must not be empty"
                )
            )
        )
    }
}