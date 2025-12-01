package com.hocok.mobliebluetoothexchanger

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import com.hocok.mobliebluetoothexchanger.presentation.MainScreen
import com.hocok.mobliebluetoothexchanger.ui.theme.MoblieBluetoothExchangerTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            MoblieBluetoothExchangerTheme {
                MainScreen()
            }
        }
    }
}