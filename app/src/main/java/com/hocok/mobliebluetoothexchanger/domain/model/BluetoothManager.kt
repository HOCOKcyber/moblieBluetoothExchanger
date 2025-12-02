package com.hocok.mobliebluetoothexchanger.domain.model

import android.bluetooth.BluetoothAdapter
import android.bluetooth.BluetoothManager
import android.content.Context

class BluetoothHelper(context: Context) {
    private val bluetoothAdapter: BluetoothAdapter by lazy {
        context.getSystemService(BluetoothManager::class.java).adapter
    }

    fun startDiscovery(){
        bluetoothAdapter.startDiscovery()
    }

    fun getPairedDevices() = bluetoothAdapter.bondedDevices ?: setOf()


    fun isEnable() = bluetoothAdapter.isEnabled
}