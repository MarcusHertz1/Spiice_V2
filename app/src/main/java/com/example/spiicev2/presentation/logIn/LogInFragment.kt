package com.example.spiicev2.presentation.logIn

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
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.runtime.snapshotFlow
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import androidx.navigation.fragment.findNavController
import com.example.spiicev2.R
import com.example.spiicev2.data.dataStore.DataStoreManager
import com.example.spiicev2.presentation.appBase.BaseFragment
import com.example.spiicev2.presentation.appBase.NavigationCommand
import com.example.spiicev2.presentation.appBase.UiProgress
import com.example.spiicev2.presentation.theme.SpiiceV2Theme
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.filterNotNull
import kotlinx.coroutines.launch

class LogInFragment : BaseFragment() {
    //Create - единственная Composable функция, которая находится в теле фрагмента
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val navController = findNavController()
        lifecycleScope.launch {

            val email = DataStoreManager(requireContext()).getEmail()
            println("email = $email")
            if (!email.isNullOrBlank()) {
                navController.navigate(
                    R.id.action_logInFragment_to_mainScreenFragment
                )
            }

        }
    }

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
    val context = LocalContext.current
    LaunchedEffect(Unit) {
        snapshotFlow { state.progress }
            .filterNotNull()
            .collectLatest {
                (it as? UiProgress.Error)?.let { error ->
                    if (error.type == LogInErrorType.OtherError)
                        Toast.makeText(
                            context,
                            error.message,
                            Toast.LENGTH_LONG
                        ).show()
                }
            }
    }

    LaunchedEffect(Unit) {
        viewModel.navigationCommands.collect { command ->
            when (command) {
                is NavigationCommand.GoToMainScreen -> {
                    navController.navigate(
                        R.id.action_logInFragment_to_mainScreenFragment,
                    )
                }

                else -> {}
            }
        }
    }

    LogInScreen(
        state = state,
        viewModel = viewModel,
        goToSignUp = {
            navController.navigate(
                R.id.action_logInFragment_to_signUpFragment,
            )
        }
    )
}

//Screen - Composable функция, где прописывается Scaffold
@Composable
private fun LogInScreen(
    state: LogInUiState,
    viewModel: LogInViewModel,
    goToSignUp: () -> Unit = {}
) {
    Scaffold { padding ->
        LogInScreenState(
            padding = padding,
            state = state,
            onEmailChange = { viewModel.emailChange(it) },
            onPasswordChange = { viewModel.passwordChange(it) },
            onLogInClick = { viewModel.logIn() },
            goToSignUp = goToSignUp
        )
    }
}

//ScreenState - Composable функция, где прописывается ТЕЛО Scaffold (его добавляют в превью)
@Composable
private fun LogInScreenState(
    padding: PaddingValues = PaddingValues(),
    state: LogInUiState,
    onEmailChange: (String) -> Unit = {},
    onPasswordChange: (String) -> Unit = {},
    onLogInClick: () -> Unit = {},
    goToSignUp: () -> Unit = {}
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
            onValueChange = { onEmailChange(it) },
            label = { Text(stringResource(R.string.email)) },
            supportingText = {
                if (error != null && error.type == LogInErrorType.EmailError)
                    Text(
                        text = error.message,
                        color = Color.Red
                    )
            },
            isError = error != null && error.type == LogInErrorType.EmailError
        )
        var passwordVisibility: Boolean by remember { mutableStateOf(false) }
        OutlinedTextField(
            visualTransformation = if (passwordVisibility) VisualTransformation.None else PasswordVisualTransformation(),
            trailingIcon = {
                IconButton(onClick = {
                    passwordVisibility = !passwordVisibility
                }) {
                    Icon(
                        painter = painterResource(
                            if (!passwordVisibility)
                                R.drawable.eye_outline
                            else
                                R.drawable.eye_off_outline
                        ),
                        contentDescription = "Show hide password"
                    )
                }
            },
            value = state.password,
            onValueChange = { onPasswordChange(it) },
            label = { Text(stringResource(R.string.password)) },
            supportingText = {
                if (error != null && error.type == LogInErrorType.PasswordError)
                    Text(
                        text = error.message,
                        color = Color.Red
                    )
            },
            isError = error != null && error.type == LogInErrorType.PasswordError
        )
        Button(
            modifier = Modifier.padding(top = 15.dp),
            onClick = { onLogInClick() },
            enabled = state.progress !is UiProgress.Loading
        ) {
            if (state.progress !is UiProgress.Loading)
                Text(stringResource(R.string.logIn))
            else CircularProgressIndicator(modifier = Modifier.size(20.dp))
        }
        Text(
            modifier = Modifier.clickable {
                goToSignUp()
            },
            text = stringResource(R.string.logInToSignUp)
        )
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