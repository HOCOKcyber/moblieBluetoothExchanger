package com.hocok.mobliebluetoothexchanger

import android.Manifest.permission.ACCESS_COARSE_LOCATION
import android.Manifest.permission.ACCESS_FINE_LOCATION
import android.bluetooth.BluetoothAdapter
import android.bluetooth.BluetoothManager
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.content.pm.PackageManager.PERMISSION_GRANTED
import android.os.Bundle
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


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            MoblieBluetoothExchangerTheme {
                MainScreen(
                    permissionScreenState
                )
            }
            getPermission.launch(arrayOf(ACCESS_FINE_LOCATION, ACCESS_COARSE_LOCATION))
        }
    }

    override fun onResume() {
        super.onResume()
        val filter = IntentFilter(BluetoothAdapter.ACTION_STATE_CHANGED)
        registerReceiver(bluetoothStateReceiver, filter)
    }

    override fun onPause() {
        super.onPause()
        unregisterReceiver(bluetoothStateReceiver)
    }

    val bluetoothAdapter: BluetoothAdapter? by lazy {
        getSystemService(BluetoothManager::class.java).adapter
    }

    private val permissionScreenState: MutableState<PermissionScreenState> by lazy {
        mutableStateOf(getState())
    }

    private val getPermission = registerForActivityResult(ActivityResultContracts.RequestMultiplePermissions()){isGranted  ->
        permissionScreenState.value = getState()
    }

    private fun getState(): PermissionScreenState {
        val isFineLocation = isPermissionGranted(ACCESS_FINE_LOCATION)
        val isCoarseLocation = isPermissionGranted(ACCESS_COARSE_LOCATION)
        val isBluetooth =  bluetoothAdapter?.isEnabled
        return PermissionScreenState(
            permissionEnable = isFineLocation && isCoarseLocation,
            bluetoothEnable = isBluetooth == true
        )
    }

    private fun isPermissionGranted(permission: String) =
        ContextCompat.checkSelfPermission(this,permission) == PERMISSION_GRANTED
}