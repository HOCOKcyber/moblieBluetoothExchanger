package com.hocok.mobliebluetoothexchanger.presentation

import android.bluetooth.BluetoothDevice
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
import com.hocok.mobliebluetoothexchanger.domain.model.PermissionScreenState

@Composable
fun MainScreen(
    permissionScreenState: PermissionScreenState,
    startDiscovery: () -> Unit,
    listDevice: Set<BluetoothDevice>
){
    val message = remember{ mutableStateOf("") }
    val isAllEnable = permissionScreenState.bluetoothEnable && permissionScreenState.permissionEnable

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
                !permissionScreenState.permissionEnable -> {
                    Text(stringResource(R.string.need_get_permission))
                }
                !permissionScreenState.bluetoothEnable -> {
                    Text(stringResource(R.string.need_on_bluetooth))
                }
            }

            Button(
                onClick = startDiscovery
            ) {
                Text(stringResource(R.string.start_discovery))
            }

            if (permissionScreenState.permissionEnable){
                LazyColumn {
                    items(listDevice.toList()){device ->
                        Text(device.name)
                    }
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
        PermissionScreenState(),
        startDiscovery = {},
        listDevice = setOf()
        )
}