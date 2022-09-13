package com.example.myblinky

import android.bluetooth.BluetoothAdapter
import android.bluetooth.BluetoothManager
import android.content.*
import android.os.Build
import android.os.Bundle
import android.os.IBinder
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.annotation.RequiresApi
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.mutableStateOf
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.example.myblinky.adapter.BlinkyAPI
import com.example.myblinky.adapter.BluetoothLeService
import com.example.myblinky.model.checkBluetoothStatus
import com.example.myblinky.view.ConnectDeviceView
import com.example.myblinky.view.HomeView
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    private var blinky: BlinkyAPI? = null
    private val TAG = "BluetoothLeService"

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
            ) {
                composable(NavigationConst.HOME) { HomeView(navController, isBluetoothEnabled) }
                composable(
                    "${NavigationConst.CONNECT_DEVICE}/{devices}",
                    arguments = listOf(navArgument("devices") { type = NavType.StringType })
                ) { backStackEntry ->
                    backStackEntry.arguments?.getString("devices")?.let { deviceAddress ->
                        LaunchedEffect(deviceAddress) {
                            connect(deviceAddress)
                        }
                        ConnectDeviceView(
                            navController = navController,
                            deviceAddress,
                            onLedChange = { blinky?.turnLed(it) },
                            blinky?.getButtonState()?.collectAsState(false)?.value ?: false
                        )
                    }
                }
            }
        }
    }

    private var deviceAddress: String? = null

    private fun connect(deviceAddress: String) {
        this.deviceAddress = deviceAddress

        val intent = Intent(this, BluetoothLeService::class.java)
        intent.putExtra("ADDRESS", deviceAddress)
        startService(intent)
        bindService(intent, serviceConnection, Context.BIND_AUTO_CREATE)
    }

    private val serviceConnection: ServiceConnection = object : ServiceConnection {
        override fun onServiceConnected(
            componentName: ComponentName,
            service: IBinder
        ) {
            blinky = service as BlinkyAPI
            blinky?.let { bluetooth ->
                // call functions on service to check connection and connect to devices
                if (!bluetooth.initialize()) {
                    Log.e(TAG, "Unable to initialize Bluetooth")
                    finish()
                }
            }
        }

        override fun onServiceDisconnected(componentName: ComponentName?) {
            blinky = null
        }

        override fun onBindingDied(name: ComponentName?) {
            super.onBindingDied(name)
        }

        override fun onNullBinding(name: ComponentName?) {
            super.onNullBinding(name)
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

    private var updateConnectionState: Boolean = false
    private val gattUpdateReceiver: BroadcastReceiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context, intent: Intent) {
            when (intent.action) {
                BluetoothLeService.ACTION_GATT_CONNECTED -> {
                    updateConnectionState = true
                }
                BluetoothLeService.ACTION_GATT_DISCONNECTED -> {
                    updateConnectionState = false
                }
            }
        }
    }

    override fun onResume() {
        super.onResume()
        registerReceiver(gattUpdateReceiver, makeGattUpdateIntentFilter())
        if (blinky != null) {
            intent.getStringExtra("ADDRESS")
                ?.let { blinky!!.connectDeviceService(it) }
        }
    }

    override fun onPause() {
        super.onPause()
        unregisterReceiver(gattUpdateReceiver)
    }

    private fun makeGattUpdateIntentFilter(): IntentFilter {
        return IntentFilter().apply {
            addAction(BluetoothLeService.ACTION_GATT_CONNECTED)
            addAction(BluetoothLeService.ACTION_GATT_DISCONNECTED)
        }
    }


}
