package com.example.spiicev2.logIn

import android.os.Bundle
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.tooling.preview.Preview
import androidx.navigation.NavController
import androidx.navigation.fragment.findNavController
import com.example.spiicev2.appBase.BaseFragment
import com.example.spiicev2.ui.theme.SpiiceV2Theme
import kotlinx.coroutines.channels.Channel

class LogInFragment :BaseFragment() {
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
) {
    //val state by viewModel.state.collectAsState()
    LogInScreen{ navController.popBackStack() }
}

//Screen - Composable функция, где прописывается Scaffold
@Composable
private fun LogInScreen(
    goBack: () -> Unit = {},
){
    //Scaffold(){
    Text("Hi")
    //}
}

@Preview(backgroundColor = 0xFFFFFFFF)
@Composable
private fun LogInScreenPreview() {
    SpiiceV2Theme {
        LogInScreen()
    }
}