package com.example.myblinky

import android.bluetooth.BluetoothAdapter
import android.bluetooth.BluetoothManager
import android.content.IntentFilter
import android.os.Build
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.annotation.RequiresApi
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.example.myblinky.model.checkBluetoothStatus
import com.example.myblinky.view.ConnectDeviceView
import com.example.myblinky.view.HomeView
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    @RequiresApi(Build.VERSION_CODES.M)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
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
                composable(
                    "${NavigationConst.CONNECT_DEVICE}/{devices}",
                    arguments = listOf(navArgument("devices") { type = NavType.StringType })
                ) { backStackEntry ->
                    backStackEntry.arguments?.getString("devices")?.let {
                        ConnectDeviceView(
                            navController = navController,
                            it
                        )
                    }
                }
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
