package com.hocok.mobliebluetoothexchanger.presentation

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import com.hocok.mobliebluetoothexchanger.R

@Composable
fun MainScreen(){
    val message = remember{ mutableStateOf("") }

    Scaffold { innerPadding ->
        Column(
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier.padding(innerPadding).fillMaxSize()
        ) {
            TextField(
                value = message.value,
                onValueChange = { message.value = it}
            )
            Button(
                onClick = { exchange(message) }
            ) {
                Text(
                    text = stringResource(R.string.send)
                )
            }
        }
    }
}

fun exchange(message: MutableState<String>){
    message.value = "Отправлено"
}

@Preview
@Composable
fun MainScreenPreview(){
    MainScreen()
}