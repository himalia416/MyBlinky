package com.example.myblinky

import android.bluetooth.BluetoothAdapter
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.runtime.*
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.myblinky.model.BLEManager
import com.example.myblinky.permissions.PermissionManager
import com.example.myblinky.view.HomeView
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        PermissionManager.askPermissions(this)
        val bleManager = BLEManager(this)
        var isBluetoothEnabled = mutableStateOf(false)

        val mReceiver = object : BroadcastReceiver() {
            override fun onReceive(context: Context?, intent: Intent?) {
                if (intent?.action == BluetoothAdapter.ACTION_STATE_CHANGED) {
                    when (intent.getIntExtra(
                        BluetoothAdapter.EXTRA_STATE,
                        BluetoothAdapter.ERROR
                    )) {
                        BluetoothAdapter.STATE_OFF -> {
                            isBluetoothEnabled.value = false
                            Log.i("Bluetooth", "State OFF")
                        }
                        BluetoothAdapter.STATE_ON -> {
                            isBluetoothEnabled.value = true
                            Log.i("Bluetooth", "State ON")
                        }
                    }

                }
            }
        }
        registerReceiver(mReceiver, IntentFilter(BluetoothAdapter.ACTION_STATE_CHANGED))
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
}
