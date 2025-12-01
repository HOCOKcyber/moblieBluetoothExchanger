package com.hocok.mobliebluetoothexchanger

import android.Manifest
import android.Manifest.permission.ACCESS_COARSE_LOCATION
import android.Manifest.permission.ACCESS_FINE_LOCATION
import android.Manifest.permission.BLUETOOTH_ADVERTISE
import android.Manifest.permission.BLUETOOTH_CONNECT
import android.Manifest.permission.BLUETOOTH_SCAN
import android.bluetooth.BluetoothAdapter
import android.bluetooth.BluetoothDevice
import android.bluetooth.BluetoothManager
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.content.pm.PackageManager.PERMISSION_GRANTED
import android.os.Build
import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.core.content.ContextCompat
import com.hocok.mobliebluetoothexchanger.domain.model.PermissionScreenState
import com.hocok.mobliebluetoothexchanger.presentation.MainScreen
import com.hocok.mobliebluetoothexchanger.ui.theme.MoblieBluetoothExchangerTheme

class MainActivity : ComponentActivity() {
    private val bluetoothStateReceiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context, intent: Intent) {
            when (intent.action) {
                BluetoothAdapter.ACTION_STATE_CHANGED -> {
                    val state = intent.getIntExtra(
                        BluetoothAdapter.EXTRA_STATE,
                        BluetoothAdapter.ERROR
                    )
                    if (state == BluetoothAdapter.STATE_OFF || state == BluetoothAdapter.STATE_ON){
                        permissionScreenState.value = getState()
                    }
                }
            }
        }
    }

    private val receiver = object : BroadcastReceiver() {

        override fun onReceive(context: Context, intent: Intent) {
            val action: String? = intent.action
            when(action) {
                BluetoothDevice.ACTION_FOUND -> {
                    val device: BluetoothDevice? =
                        intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE)

                    device?.name?.let {
                        deviceListState.value = deviceListState.value + setOf(device)
                    }
                }
            }
        }
    }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            MoblieBluetoothExchangerTheme {
                MainScreen(
                    permissionScreenState.value,
                    startDiscovery = ::startDiscovery,
                    listDevice = deviceListState.value
                )
            }
            when{
                Build.VERSION.SDK_INT >= Build.VERSION_CODES.S -> {
                    getPermission.launch(
                        arrayOf(BLUETOOTH_ADVERTISE,
                            BLUETOOTH_SCAN,
                            BLUETOOTH_CONNECT))
                }
                else -> {
                    getPermission.launch(arrayOf(
                        Manifest.permission.ACCESS_FINE_LOCATION,
                        Manifest.permission.BLUETOOTH,
                        Manifest.permission.BLUETOOTH_ADMIN
                    ))
                }
            }
        }
    }

    override fun onResume() {
        super.onResume()
        val filterBtSate = IntentFilter(BluetoothAdapter.ACTION_STATE_CHANGED)
        registerReceiver(bluetoothStateReceiver, filterBtSate)

        val filterFound = IntentFilter(BluetoothDevice.ACTION_FOUND)
        registerReceiver(receiver, filterFound)
    }

    override fun onPause() {
        super.onPause()
        unregisterReceiver(bluetoothStateReceiver)
        unregisterReceiver(receiver)
    }

    private val bluetoothAdapter: BluetoothAdapter? by lazy {
        getSystemService(BluetoothManager::class.java).adapter
    }

    private fun startDiscovery(){
        deviceListState.value = setOf()
        val pairedDevices: Set<BluetoothDevice> = bluetoothAdapter?.bondedDevices ?: setOf()
        deviceListState.value = deviceListState.value + pairedDevices

        val discoveryStarted = bluetoothAdapter?.startDiscovery() ?: false
        Log.d("Bluetooth", "startDiscovery() returned: $discoveryStarted")
    }

    private val deviceListState = mutableStateOf(setOf<BluetoothDevice>())

    private val permissionScreenState: MutableState<PermissionScreenState> by lazy {
        mutableStateOf(getState())
    }

    private val getPermission = registerForActivityResult(ActivityResultContracts.RequestMultiplePermissions()){isGranted  ->
        permissionScreenState.value = getState()
    }

    private fun getState(): PermissionScreenState {
        val isPermission = when{
            Build.VERSION.SDK_INT >= Build.VERSION_CODES.S -> {
                isPermissionGranted(BLUETOOTH_ADVERTISE) &&
                        isPermissionGranted(BLUETOOTH_SCAN) &&
                        isPermissionGranted(BLUETOOTH_CONNECT)
            }
            else -> {
                isPermissionGranted(ACCESS_COARSE_LOCATION) &&
                        isPermissionGranted(ACCESS_FINE_LOCATION) &&
                        isPermissionGranted(Manifest.permission.BLUETOOTH) &&
                        isPermissionGranted(Manifest.permission.BLUETOOTH_ADMIN)
            }
        }
        val isBluetooth =  bluetoothAdapter?.isEnabled
        return PermissionScreenState(
            permissionEnable = isPermission,
            bluetoothEnable = isBluetooth == true
        )
    }

    private fun isPermissionGranted(permission: String) =
        ContextCompat.checkSelfPermission(this,permission) == PERMISSION_GRANTED
}