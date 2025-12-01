package com.hocok.mobliebluetoothexchanger

import android.Manifest.permission.ACCESS_COARSE_LOCATION
import android.Manifest.permission.ACCESS_FINE_LOCATION
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
    private val getPermission = registerForActivityResult(ActivityResultContracts.RequestMultiplePermissions()){isGranted  ->
        permissionScreenState.value = getState()
    }

    private val permissionScreenState: MutableState<PermissionScreenState> by lazy {
        mutableStateOf(getState())
    }

    private fun getState(): PermissionScreenState {
        val isFineLocation = isPermissionGranted(ACCESS_FINE_LOCATION)
        val isCoarseLocation = isPermissionGranted(ACCESS_COARSE_LOCATION)
        return if (isCoarseLocation && isFineLocation){
                    PermissionScreenState(enable = true)
                }else {
                    PermissionScreenState(enable = false)
                }
    }

    private fun isPermissionGranted(permission: String) =
        ContextCompat.checkSelfPermission(this,permission) == PERMISSION_GRANTED

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            MoblieBluetoothExchangerTheme {
                MainScreen(
                    permissionScreenState.value
                )
            }
            getPermission.launch(arrayOf(ACCESS_FINE_LOCATION, ACCESS_COARSE_LOCATION))
        }
    }
}