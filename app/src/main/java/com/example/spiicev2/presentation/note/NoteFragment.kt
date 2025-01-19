package com.example.spiicev2.presentation.note

import android.os.Bundle
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.ime
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.systemBars
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.outlined.ArrowBack
import androidx.compose.material.icons.outlined.Check
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import androidx.navigation.fragment.findNavController
import com.example.spiicev2.R
import com.example.spiicev2.presentation.appBase.BaseFragment
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
    navController: NavController,
    viewModel: NoteViewModel = viewModel()
) {
    val state by viewModel.state.collectAsState()
    NoteScreen(
        state = state,
        setText = {
            viewModel.setText(it)
        }
    ) {
        navController.popBackStack()
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun NoteScreen(
    state: NoteUiState,
    setText: (String) -> Unit = {},
    goBack: () -> Unit = {},
) {

    Scaffold(
        topBar = {
            TopAppBar(
                navigationIcon = {
                    IconButton(
                        onClick = { goBack() },
                        modifier = Modifier
                            .size(48.dp)
                    ) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Outlined.ArrowBack,
                            contentDescription = "Вернуться назад",
                        )
                    }
                },
                title = { Text(stringResource(R.string.noteScreenTitle)) },
                actions = {
                    IconButton(
                        onClick = {},
                        modifier = Modifier
                            .size(48.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Outlined.Check,
                            contentDescription = "Вернуться назад",
                        )
                    }
                }
            )
        }
    )
    { padding ->
        NoteScreenState(
            padding = padding,
            state = state,
            setText = setText
        )
    }
}

@Composable
private fun NoteScreenState(
    padding: PaddingValues = PaddingValues(),
    state: NoteUiState,
    setText: (String) -> Unit = {},
) {
    BasicTextField(
        value = state.text,
        onValueChange = { setText(it) },
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
        decorationBox = { innerTextField ->
            if (state.text.isBlank()) {
                Text("Введите текст...", color = Color.Gray, fontSize = 18.sp)
            }
            innerTextField()
        },
    )
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