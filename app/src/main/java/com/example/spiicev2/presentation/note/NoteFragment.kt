package com.example.spiicev2.presentation.note

import android.os.Bundle
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.ime
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.systemBars
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.outlined.ArrowBack
import androidx.compose.material.icons.outlined.Check
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import androidx.navigation.fragment.findNavController
import com.example.spiicev2.presentation.appBase.BaseFragment
import com.example.spiicev2.presentation.appBase.NavigationCommand
import com.example.spiicev2.presentation.appBase.UiProgress
import com.example.spiicev2.presentation.theme.SpiiceV2Theme
import kotlinx.coroutines.channels.Channel

class NoteFragment : BaseFragment() {
    @Composable
    override fun Create(arguments: Bundle?, resultChannel: Channel<Bundle>) {
        val navController = findNavController()
        NoteState(navController)
    }
}

@Composable
private fun NoteState(
    navController: NavController, viewModel: NoteViewModel = viewModel()
) {
    val state by viewModel.state.collectAsState()
    NoteScreen(state = state,
        setText = { viewModel.setText(it) },
        setTitle = { viewModel.setTitle(it) },
        goBack = { navController.popBackStack() },
        incTextSize = { viewModel.incrementTextSize() },
        decTextSize = { viewModel.decrementTextSize() },
        save = { viewModel.saveNote() }
    )

    LaunchedEffect(Unit) {
        viewModel.navigationCommands.collect { command ->
            when (command) {
                is NavigationCommand.GoToMainScreen -> {
                    navController.popBackStack()
                }

                else -> {}
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun NoteScreen(
    state: NoteUiState,
    setText: (String) -> Unit = {},
    setTitle: (String) -> Unit = {},
    goBack: () -> Unit = {},
    incTextSize: () -> Unit = {},
    decTextSize: () -> Unit = {},
    save: () -> Unit = {}
) {

    Scaffold(topBar = {
        TopAppBar(navigationIcon = {
            IconButton(
                onClick = { goBack() }, modifier = Modifier.size(48.dp)
            ) {
                Icon(
                    imageVector = Icons.AutoMirrored.Outlined.ArrowBack,
                    contentDescription = "Вернуться назад",
                )
            }
        }, title = {
            BasicTextField(
                value = state.data.title,
                onValueChange = {
                    setTitle(it)
                },
                modifier = Modifier.fillMaxWidth(),
                decorationBox = { innerTextField ->
                    if (state.data.title.isBlank()) {
                        Text("Введите название...", color = Color.Gray, fontSize = 18.sp)
                    }
                    innerTextField()
                }
            )
        }, actions = {
            IconButton(
                onClick = { save() },
                modifier = Modifier.size(48.dp),
                enabled = state.progress !is UiProgress.Loading
            ) {
                Icon(
                    imageVector = Icons.Outlined.Check,
                    contentDescription = "Сохранить",
                )
            }
        })
    }) { padding ->
        NoteScreenState(
            padding = padding,
            state = state,
            setText = setText,
            incTextSize = incTextSize,
            decTextSize = decTextSize
        )
    }
}

@Composable
private fun NoteScreenState(
    padding: PaddingValues = PaddingValues(),
    state: NoteUiState,
    setText: (String) -> Unit = {},
    incTextSize: () -> Unit = {},
    decTextSize: () -> Unit = {}
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(padding)
            .windowInsetsPadding(
                WindowInsets.ime // Учет области клавиатуры
            )
            .windowInsetsPadding(
                WindowInsets.systemBars
            )
            .padding(16.dp),
    ) {

        HorizontalDivider(modifier = Modifier.padding(bottom = 4.dp))

        Row(
            modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.Center
        ) {
            IconButton(
                onClick = { decTextSize() }, modifier = Modifier.size(30.dp)
            ) {
                Text(
                    text = "–",
                    modifier = Modifier.fillMaxSize(),
                    textAlign = TextAlign.Center,
                    fontSize = 20.sp
                )
            }
            Text(
                modifier = Modifier
                    .width(30.dp)
                    .align(Alignment.CenterVertically),
                text = state.textSize.toString(),
                fontSize = 16.sp,
                textAlign = TextAlign.Center
            )
            IconButton(
                onClick = { incTextSize() }, modifier = Modifier.size(30.dp)
            ) {
                Text(
                    text = "+",
                    modifier = Modifier.fillMaxSize(),
                    textAlign = TextAlign.Center,
                    fontSize = 20.sp
                )
            }
        }

        HorizontalDivider(modifier = Modifier.padding(bottom = 16.dp, top = 4.dp))

        BasicTextField(
            value = state.data.text,
            onValueChange = { setText(it) },
            modifier = Modifier.fillMaxSize(),
            decorationBox = { innerTextField ->
                if (state.data.text.isBlank()) {
                    Text("Введите текст...", color = Color.Gray, fontSize = 18.sp)
                }
                innerTextField()
            },
            textStyle = TextStyle(fontSize = state.textSize.sp)
        )
    }
}


@Preview(backgroundColor = 0xFFFFFFFF)
@Composable
private fun NotePreview() {
    SpiiceV2Theme {
        NoteScreen(
            state = NoteUiState()
        )
    }
}