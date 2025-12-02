package com.hocok.mobliebluetoothexchanger.presentation

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
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
import com.hocok.mobliebluetoothexchanger.domain.model.MainScreenState

@Composable
fun MainScreen(
    state: MainScreenState,
    startDiscovery: () -> Unit,
    startDiscoverable: () -> Unit,
){
    val message = remember{ mutableStateOf("") }
    val isAllEnable = state.bluetoothEnable && state.permissionEnable

    Scaffold { innerPadding ->
        Column(
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier.padding(innerPadding).fillMaxSize()
        ) {
            TextField(
                value = message.value,
                onValueChange = { message.value = it},
                enabled = isAllEnable
            )
            Button(
                onClick = { exchange(message) },
                enabled = isAllEnable
            ) {
                Text(
                    text = stringResource(R.string.send)
                )
            }

            when{
                !state.permissionEnable -> {
                    Text(stringResource(R.string.need_get_permission))
                }
                !state.bluetoothEnable -> {
                    Text(stringResource(R.string.need_on_bluetooth))
                }
            }

            Button(
                onClick = startDiscovery
            ) {
                Text(stringResource(R.string.start_discovery))
            }

            if (state.permissionEnable){
                LazyColumn {
                    items(state.deviceSet.toList()){device ->
                        Text(device.name)
                    }
                }
            }

            Button(
                onClick = startDiscoverable,
                enabled = !state.isScannable
            ) {
                if (state.isScannable){
                    Text("Доступно для обнаружения")
                } else {
                    Text("Сделать доступным для обнаружения")
                }
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
    MainScreen(
        MainScreenState(),
        startDiscovery = {},
        startDiscoverable = {}
        )
}