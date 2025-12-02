package com.hocok.mobliebluetoothexchanger.domain.model

import android.bluetooth.BluetoothDevice

data class MainScreenState(
    val permissionEnable: Boolean = false,
    val bluetoothEnable: Boolean = false,
    val isScannable: Boolean = false,
    val deviceSet: Set<BluetoothDevice> = setOf()
)