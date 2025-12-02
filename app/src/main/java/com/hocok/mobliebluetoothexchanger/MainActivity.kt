package com.hocok.mobliebluetoothexchanger

import android.Manifest
import android.bluetooth.BluetoothAdapter
import android.bluetooth.BluetoothDevice
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.content.pm.PackageManager.PERMISSION_GRANTED
import android.os.Build
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.runtime.mutableStateOf
import androidx.core.content.ContextCompat
import com.hocok.mobliebluetoothexchanger.domain.model.BluetoothHelper
import com.hocok.mobliebluetoothexchanger.domain.model.MainScreenState
import com.hocok.mobliebluetoothexchanger.presentation.MainScreen
import com.hocok.mobliebluetoothexchanger.ui.theme.MoblieBluetoothExchangerTheme

class MainActivity : ComponentActivity() {
    private val combinedBluetoothReceiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context, intent: Intent) {
            when (intent.action) {
                BluetoothAdapter.ACTION_STATE_CHANGED -> {
                    val state = intent.getIntExtra(
                        BluetoothAdapter.EXTRA_STATE,
                        BluetoothAdapter.ERROR
                    )
                    if (state == BluetoothAdapter.STATE_OFF || state == BluetoothAdapter.STATE_ON) {
                        mainScreenState.value = getState()
                    }
                }

                BluetoothDevice.ACTION_FOUND -> {
                    val device: BluetoothDevice? =
                        intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE)

                    device?.name?.let {
                        mainScreenState.value = mainScreenState.value.copy(
                            deviceSet = mainScreenState.value.deviceSet + setOf(device)
                        )
                    }
                }

                BluetoothAdapter.ACTION_SCAN_MODE_CHANGED -> {
                    val state = intent.getIntExtra(
                        BluetoothAdapter.EXTRA_SCAN_MODE,
                        BluetoothAdapter.ERROR
                    )

                    mainScreenState.value = mainScreenState.value.copy(
                        isScannable = state == BluetoothAdapter.SCAN_MODE_CONNECTABLE_DISCOVERABLE
                    )
                }
            }
        }
    }

    private val getPermission = registerForActivityResult(ActivityResultContracts.RequestMultiplePermissions()){isGranted  ->
        mainScreenState.value = getState()
    }

    private val getScanResponse = registerForActivityResult(ActivityResultContracts.StartActivityForResult()){ result ->
        mainScreenState.value = mainScreenState.value.copy(
            isScannable = result.resultCode != RESULT_CANCELED
        )
    }

    private val mainScreenState by lazy { mutableStateOf(MainScreenState()) }

    private val bluetoothHelper = BluetoothHelper(this)

    private val permissions =  when{
        Build.VERSION.SDK_INT >= Build.VERSION_CODES.S -> {
            arrayOf(
                Manifest.permission.BLUETOOTH_ADVERTISE,
                Manifest.permission.BLUETOOTH_SCAN,
                Manifest.permission.BLUETOOTH_CONNECT
            )
        }
        else -> {
            arrayOf(
                Manifest.permission.ACCESS_FINE_LOCATION,
                Manifest.permission.BLUETOOTH,
                Manifest.permission.BLUETOOTH_ADMIN
            )
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            MoblieBluetoothExchangerTheme {
                MainScreen(
                    mainScreenState.value,
                    startDiscovery = ::getDevices,
                    startDiscoverable = ::startDiscoverable
                )
            }
            getPermission.launch(permissions)
        }
    }

    override fun onResume() {
        super.onResume()
        val filter = IntentFilter().apply {
            addAction(BluetoothAdapter.ACTION_SCAN_MODE_CHANGED)
            addAction(BluetoothAdapter.ACTION_STATE_CHANGED)
            addAction(BluetoothDevice.ACTION_FOUND)
        }
        registerReceiver(combinedBluetoothReceiver, filter)
    }

    override fun onPause() {
        super.onPause()
        unregisterReceiver(combinedBluetoothReceiver)
    }

    fun startDiscoverable(){
        val discoverableIntent: Intent = Intent(BluetoothAdapter.ACTION_REQUEST_DISCOVERABLE).apply {
            putExtra(BluetoothAdapter.EXTRA_DISCOVERABLE_DURATION, 10)
        }
        getScanResponse.launch(discoverableIntent)
    }

    private fun getDevices() =
        bluetoothHelper.run {
            mainScreenState.value = mainScreenState.value.copy(
                deviceSet = getPairedDevices()
            )
            startDiscovery()
        }

    private fun getState(): MainScreenState {
        val isPermission = isPermissionGranted(permissions)
        val isBluetooth =  bluetoothHelper.isEnable()
        return mainScreenState.value.copy(
            permissionEnable = isPermission,
            bluetoothEnable = isBluetooth
        )
    }

    private fun isPermissionGranted(permission: Array<String>) =
        permission.all {  ContextCompat.checkSelfPermission(this, it) == PERMISSION_GRANTED }
}