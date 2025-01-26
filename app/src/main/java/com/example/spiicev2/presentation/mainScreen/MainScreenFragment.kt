package com.example.spiicev2.presentation.mainScreen

import android.os.Bundle
import android.widget.Toast
import androidx.activity.compose.BackHandler
import androidx.compose.animation.core.Animatable
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectHorizontalDragGestures
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ExitToApp
import androidx.compose.material.icons.automirrored.outlined.ArrowBack
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.outlined.Search
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonColors
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Checkbox
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.material3.pulltorefresh.PullToRefreshBox
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
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import androidx.navigation.fragment.findNavController
import com.example.spiicev2.MainActivity
import com.example.spiicev2.R
import com.example.spiicev2.domain.Converters.toDate
import com.example.spiicev2.domain.repository.NoteData
import com.example.spiicev2.presentation.appBase.Arguments.NOTE_DATA
import com.example.spiicev2.presentation.appBase.BaseFragment
import com.example.spiicev2.presentation.appBase.NavigationCommand
import com.example.spiicev2.presentation.appBase.UiProgress
import com.example.spiicev2.presentation.logIn.LogInErrorType
import com.example.spiicev2.presentation.theme.SpiiceV2Theme
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.filterNotNull
import kotlinx.coroutines.launch
import kotlin.math.roundToInt

class MainScreenFragment : BaseFragment() {
    @Composable
    override fun Create(arguments: Bundle?, resultChannel: Channel<Bundle>) {
        val navController = findNavController()
        MainScreenState(navController){
            (activity as? MainActivity)?.finish()
        }
    }
}

@Composable
private fun MainScreenState(
    navController: NavController,
    viewModel: MainScreenViewModel = viewModel(),
    finishActivity: () -> Unit
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
                is NavigationCommand.GoToLogInScreen -> {
                    navController.navigate(R.id.action_mainScreenFragment_to_logInFragment)
                }

                else -> {}
            }
        }
    }

    BackHandler {
        if (viewModel.onBackPressed()) finishActivity()
    }

    MainScreenScreen(
        state = state,
        actionHandler = remember {
            { action ->
                when (action) {
                    is MainScreenActions.LogOut -> viewModel.logOut()
                    is MainScreenActions.ToNote -> navController.navigate(
                        R.id.action_mainScreenFragment_to_noteFragment,
                        Bundle().apply {
                            putParcelable(NOTE_DATA, action.data)
                        })

                    is MainScreenActions.ChangeChecked -> viewModel.changeChecked(
                        id = action.id,
                        isChecked = action.isChecked
                    )

                    is MainScreenActions.AddToChecked -> viewModel.addToChecked(
                        id = action.id
                    )

                    is MainScreenActions.DeleteSingleChecked -> viewModel.deleteSingleNote(
                        id = action.id
                    )

                    is MainScreenActions.DeleteAllChecked -> viewModel.deleteCheckedNotes()
                    is MainScreenActions.GetAllNotes -> viewModel.getAllNotes()
                    is MainScreenActions.SetSearchValue -> viewModel.setSearchValue(
                        text = action.text
                    )
                }
            }
        }
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun MainScreenScreen(
    state: MainScreenUiState,
    actionHandler: (MainScreenActions) -> Unit = {}
) {

    Scaffold(
        modifier = Modifier.fillMaxSize()
    )
    { padding ->
        PullToRefreshBox(
            isRefreshing = state.progress is UiProgress.Loading,
            onRefresh = {
                actionHandler(MainScreenActions.GetAllNotes)
            },
            modifier = Modifier
                .padding(padding)
                .fillMaxSize()
        ) {
            MainScreenScreenState(
                state = state,
                actionHandler = actionHandler
            )
        }
    }
}

@Composable
private fun MainScreenScreenState(
    state: MainScreenUiState,
    actionHandler: (MainScreenActions) -> Unit = {}
) {
    var active by remember { mutableStateOf(false) }

    val focusRequester = remember { FocusRequester() }
    val focusManager = LocalFocusManager.current

    Box(
        modifier = Modifier
            .fillMaxSize()
    )
    {
        Column(
            Modifier
                .fillMaxSize()
        ) {
            Row(
                modifier = Modifier
                    .padding(8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                IconButton(
                    onClick = { actionHandler(MainScreenActions.LogOut) },
                    modifier = Modifier
                        .size(48.dp)
                ) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.ExitToApp,
                        contentDescription = "Выйти",
                    )
                }
                TextField(
                    colors = TextFieldDefaults.colors(
                        focusedIndicatorColor = Color.Transparent,
                        unfocusedIndicatorColor = Color.Transparent,
                        disabledIndicatorColor = Color.Transparent
                    ),
                    modifier = Modifier
                        .focusRequester(focusRequester)
                        .onFocusChanged { focusState ->
                            active = focusState.isFocused
                        }
                        .weight(1f),
                    shape = RoundedCornerShape(30.dp),
                    value = state.searchValue,
                    onValueChange = { actionHandler(MainScreenActions.SetSearchValue(it)) },
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
                            Icon(
                                imageVector = Icons.Outlined.Search,
                                contentDescription = "Поиск",
                            )
                        }
                    },
                    trailingIcon = {
                        if (state.searchValue.isNotEmpty()) {
                            IconButton(onClick = { actionHandler(MainScreenActions.SetSearchValue("")) }) {
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
                IconButton(
                    onClick = { },
                    modifier = Modifier
                        .size(48.dp)
                ) {
                    Icon(
                        painter = painterResource(R.drawable.sort),
                        contentDescription = "Выйти",
                    )
                }
            }

            if (state.data.isEmpty()) {
                Text(
                    text = stringResource(R.string.noNotes),
                    color = Color.Gray,
                    modifier = Modifier
                        .fillMaxSize()
                        .wrapContentHeight()
                        .padding(horizontal = 12.dp),
                    textAlign = TextAlign.Center
                )
            }

            LazyColumn(
                modifier = Modifier
                    .fillMaxSize(),
                contentPadding = PaddingValues(8.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp),
            ) {
                items(
                    if (state.searchValue.isNotBlank()) {
                        state.data.filter { it.title.contains(state.searchValue) }
                    } else {
                        state.data
                    }
                ) { note ->
                    Note(
                        noteData = note,
                        state = state,
                        onCheckedChange = {
                            actionHandler(
                                MainScreenActions.ChangeChecked(
                                    id = note.id,
                                    isChecked = it
                                )
                            )
                        },
                        onLongPress = {
                            actionHandler(
                                MainScreenActions.AddToChecked(
                                    id = note.id
                                )
                            )
                        },
                        onClickDelete = {
                            actionHandler(
                                MainScreenActions.DeleteSingleChecked(
                                    id = note.id
                                )
                            )
                        },
                        onPress = {
                            actionHandler(
                                MainScreenActions.ToNote(
                                    data = note
                                )
                            )
                        }
                    )
                }
            }
        }
        FloatingActionButton(
            onClick = { actionHandler(MainScreenActions.ToNote(null)) },
            modifier = Modifier
                .align(alignment = Alignment.BottomEnd)
                .padding(16.dp)
        ) {
            Icon(Icons.Filled.Add, "Floating action button.")
        }
        if (state.checkedNotes.isNotEmpty())
            Box(
                modifier = Modifier.fillMaxWidth(),
                contentAlignment = Alignment.BottomCenter
            ) {
                Button(
                    onClick = {
                        actionHandler(MainScreenActions.DeleteAllChecked)
                    },
                    colors = ButtonColors(
                        containerColor = Color.Red,
                        contentColor = Color.White,
                        disabledContainerColor = Color.Gray,
                        disabledContentColor = Color.White,
                    ),
                    modifier = Modifier
                        .fillMaxHeight()
                        .wrapContentHeight(align = Alignment.Bottom)
                        .padding(16.dp)
                ) {
                    Text(
                        stringResource(R.string.deleteAllChecked)
                    )
                }
            }
    }
}

@Composable
private fun Note(
    modifier: Modifier = Modifier,
    noteData: NoteData,
    state: MainScreenUiState,
    onCheckedChange: (Boolean) -> Unit = {},
    onLongPress: () -> Unit = {},
    onClickDelete: () -> Unit = {},
    onPress: () -> Unit = {}
) {
    val offsetXAnim = remember { Animatable(0f) }

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .wrapContentHeight()
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp)
                .wrapContentHeight(),
            horizontalArrangement = Arrangement.End,
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconButton(onClick = {
                CoroutineScope(Dispatchers.Main).launch {
                    onClickDelete()
                    offsetXAnim.snapTo(0f)
                }
            }) {
                Icon(Icons.Default.Delete, contentDescription = "")
            }
        }
        ElevatedCard(
            elevation = CardDefaults.cardElevation(
                defaultElevation = 6.dp
            ),
            modifier = modifier
                .offset { IntOffset(offsetXAnim.value.roundToInt(), 0) }
                .fillMaxWidth()
                .pointerInput(Unit) {
                    detectTapGestures(
                        onLongPress = {
                            onLongPress()
                        }
                    )
                }
                .pointerInput(Unit) {
                    detectHorizontalDragGestures { _, dragAmount ->
                        CoroutineScope(Dispatchers.Main).launch {
                            offsetXAnim.snapTo((offsetXAnim.value + dragAmount).coerceIn(-200f, 0f))
                        }
                    }
                }
                .clickable {
                    onPress()
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
}

private sealed interface MainScreenActions {
    data object LogOut : MainScreenActions
    data class ToNote(val data: NoteData?) : MainScreenActions
    data class AddToChecked(val id: String) : MainScreenActions
    data class ChangeChecked(val id: String, val isChecked: Boolean) : MainScreenActions
    data object DeleteAllChecked : MainScreenActions
    data class DeleteSingleChecked(val id: String) : MainScreenActions
    data object GetAllNotes : MainScreenActions
    data class SetSearchValue(val text: String) : MainScreenActions
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