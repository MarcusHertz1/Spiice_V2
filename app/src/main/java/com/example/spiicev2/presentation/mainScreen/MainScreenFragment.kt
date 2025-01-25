package com.example.spiicev2.presentation.mainScreen

import android.os.Bundle
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ExitToApp
import androidx.compose.material.icons.automirrored.outlined.ArrowBack
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Checkbox
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SearchBar
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import androidx.navigation.fragment.findNavController
import com.example.spiicev2.R
import com.example.spiicev2.domain.Converters.toDate
import com.example.spiicev2.domain.repository.NoteData
import com.example.spiicev2.presentation.appBase.BaseFragment
import com.example.spiicev2.presentation.appBase.NavigationCommand
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
    LaunchedEffect(Unit) {
        viewModel.navigationCommands.collect { command ->
            when (command) {
                is NavigationCommand.GoToLogInScreen -> {
                    navController.navigate(R.id.action_mainScreenFragment_to_logInFragment)
                }

                else -> {}
            }
        }
    }
    MainScreenScreen(
        state = state,
        logOut = {
            viewModel.logOut()
        },
        toNotes = {
            navController.navigate(R.id.action_mainScreenFragment_to_noteFragment)
        }
    )
}

@Composable
private fun MainScreenScreen(
    state: MainScreenUiState,
    logOut: () -> Unit = {},
    toNotes: () -> Unit = {},
) {

    Scaffold()
    { padding ->
        MainScreenScreenState(
            padding = padding,
            state = state,
            logOut = logOut,
            toNotes = toNotes,
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun MainScreenScreenState(
    padding: PaddingValues = PaddingValues(),
    state: MainScreenUiState,
    logOut: () -> Unit = {},
    toNotes: () -> Unit = {},
) {
    var query by remember { mutableStateOf("") }
    var active by remember { mutableStateOf(false) }

    val focusRequester = remember { FocusRequester() }
    val focusManager = LocalFocusManager.current

    Box(
        modifier = Modifier
            .padding(padding)
            .fillMaxSize()
    )
    {
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
            onExpandedChange = { active = it }
        ) {
            // Содержимое, отображаемое при активном состоянии
            Text("Результаты поиска появятся здесь.")
        }

        FloatingActionButton(
            onClick = { toNotes() },
            modifier = Modifier
                .align(alignment = Alignment.BottomEnd)
                .padding(16.dp)
        ) {
            Icon(Icons.Filled.Add, "Floating action button.")
        }
    }
}

@Composable
private fun Note(
    modifier: Modifier = Modifier,
    noteData: NoteData,
    state: MainScreenUiState,
    onCheckedChange: (Boolean) -> Unit = {},
    onLongPress: () -> Unit = {}
) {
    ElevatedCard(
        elevation = CardDefaults.cardElevation(
            defaultElevation = 6.dp
        ),
        modifier = modifier
            .fillMaxWidth()
            .pointerInput(Unit) {
                detectTapGestures(
                    onLongPress = {
                        onLongPress()
                    }
                )
            },
    ) {
        Row(
            horizontalArrangement = Arrangement.spacedBy(4.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            if (state.checkedNotes.isNotEmpty()) {
                val isChecked = state.checkedNotes.contains(noteData.id)
                Checkbox(
                    checked = isChecked,
                    onCheckedChange = { onCheckedChange(isChecked) }
                )
            }
            Column(
                verticalArrangement = Arrangement.spacedBy(16.dp),
                modifier = Modifier
                    .padding(16.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Text(
                        text = noteData.title.ifBlank { stringResource(R.string.emptyTitle) },
                        modifier = Modifier
                            .weight(1f),
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                    if (noteData.lastEditDate > 0)
                        Text(
                            text = noteData.lastEditDate.toDate(),
                            fontSize = 14.sp,
                        )
                }
                Text(
                    text = noteData.text,
                    modifier = Modifier,
                    fontSize = 14.sp,
                    maxLines = 3,
                    overflow = TextOverflow.Ellipsis
                )
            }
        }
    }
}

/*@Preview(backgroundColor = 0xFFFFFFFF)
@Composable
private fun MainScreenScreenPreview() {
    SpiiceV2Theme {
        MainScreenScreen(
            state = MainScreenUiState()
        )
    }
}*/

@Preview(backgroundColor = 0xFFFFFFFF)
@Composable
private fun NotePreview() {
    SpiiceV2Theme {
        Note(
            noteData = NoteData(
                title = "Title",
                text = "kjhfdggkjfdhs",
                lastEditDate = 179999956889
            ),
            state = MainScreenUiState(checkedNotes = listOf("bdsghfdgd"))
        )
    }
}