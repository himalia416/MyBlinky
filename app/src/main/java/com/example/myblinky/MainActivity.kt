package com.example.myblinky

import android.bluetooth.BluetoothAdapter
import android.bluetooth.BluetoothManager
import android.content.IntentFilter
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.runtime.*
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.myblinky.model.checkBluetoothStatus
import com.example.myblinky.permissions.PermissionManager
import com.example.myblinky.view.HomeView
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        PermissionManager.askPermissions(this)
        val isBluetoothEnabled = checkInitialBluetoothStatus()
        checkBluetoothStatus(isBluetoothEnabled).apply {
            registerReceiver(this, IntentFilter(BluetoothAdapter.ACTION_STATE_CHANGED))
        }
        setContent {
            val navController = rememberNavController()
            NavHost(
                navController = navController,
                startDestination = NavigationConst.HOME
            )
            {
                composable(NavigationConst.HOME) { HomeView(navController, isBluetoothEnabled) }
            }
        }
    }

    private fun checkInitialBluetoothStatus(): MutableState<Boolean> {
        val isBluetoothEnabled = mutableStateOf(false)
        val bluetoothManager =
            (getSystemService(BLUETOOTH_SERVICE) as BluetoothManager)

        val bluetoothAdapter: BluetoothAdapter by lazy {
            bluetoothManager.adapter
        }
        isBluetoothEnabled.value = bluetoothAdapter.isEnabled
        return isBluetoothEnabled
    }
}
