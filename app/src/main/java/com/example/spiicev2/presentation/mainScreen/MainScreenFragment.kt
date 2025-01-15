package com.example.spiicev2.presentation.mainScreen

import android.os.Bundle
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ExitToApp
import androidx.compose.material.icons.automirrored.outlined.ArrowBack
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.Card
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SearchBar
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import androidx.navigation.fragment.findNavController
import com.example.spiicev2.R
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
        logOut = { viewModel.logOut()
navController.navigate(R.id.action_mainScreenFragment_to_logInFragment)
                 },
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun MainScreenScreen(
    state: MainScreenUiState,
    //viewModel: MainScreenViewModel,
    logOut: () -> Unit = {},
) {
    /*val textFieldState = rememberTextFieldState()
    val expanded = remember {
        mutableStateOf(false)
    }*/

    Scaffold(
        /*navigationIcon = {
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
        title = { },*/
    ) { padding ->
        MainScreenScreenState(
            padding = padding,
            state = state,
            logOut = logOut,
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun MainScreenScreenState(
    padding: PaddingValues = PaddingValues(),
    state: MainScreenUiState,
    logOut: () -> Unit = {},
) {
    var query by remember { mutableStateOf("") }
    var active by remember { mutableStateOf(false) }

    val focusRequester = remember { FocusRequester() }
    val focusManager = LocalFocusManager.current



    Column(

    ) {
        SearchBar(
            modifier = Modifier
                .fillMaxWidth()
                .padding(8.dp),
            inputField = {
                TextField(
                    modifier = Modifier
                        .focusRequester(focusRequester)
                        .onFocusChanged { focusState ->
                            active = focusState.isFocused
                        },
                    value = query,
                    onValueChange = { query = it },
                    placeholder = { Text("Поиск...") },
                    singleLine = true,
                    leadingIcon = {
                        if (active) {
                            IconButton(
                                onClick = {
                                    active = false
                                    focusManager.clearFocus()
                                }
                            ) {
                                Icon(
                                    Icons.AutoMirrored.Outlined.ArrowBack,
                                    contentDescription = "Назад"
                                )
                            }
                        } else {
                            IconButton(
                                onClick = { logOut() },
                                modifier = Modifier
                                    .size(48.dp)
                            ) {
                                Icon(
                                    imageVector = Icons.AutoMirrored.Filled.ExitToApp,
                                    contentDescription = "Выйти",
                                )
                            }
                            //Icon(Icons.Default.Search, contentDescription = "Иконка поиска")
                        }
                    },
                    trailingIcon = {
                        if (query.isNotEmpty()) {
                            IconButton(onClick = { query = "" }) {
                                Icon(Icons.Default.Close, contentDescription = "Очистить поиск")
                            }
                        }
                    },
                    keyboardOptions = KeyboardOptions.Default.copy(
                        imeAction = ImeAction.Search
                    ),
                    keyboardActions = KeyboardActions(
                        onSearch = {
                            active = false
                            focusManager.clearFocus()
                        }
                    )
                )
            },
            expanded = active,
            onExpandedChange = { active = it },
        ) {
            // Содержимое, отображаемое при активном состоянии
            Text("Результаты поиска появятся здесь.")
        }
        println("act;ive = $active")
        println("focusManager = $focusManager")
    }
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