package com.example.myblinky

import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.content.ServiceConnection
import android.os.Build
import android.os.Bundle
import android.os.IBinder
import android.util.Log
import androidx.activity.compose.setContent
import androidx.annotation.RequiresApi
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import com.example.myblinky.adapter.BlinkyAPI
import com.example.myblinky.adapter.BluetoothLeService
import com.example.myblinky.view.ConnectDeviceView
import com.example.myblinky.view.HomeView
import dagger.hilt.android.AndroidEntryPoint
import no.nordicsemi.android.common.navigation.*
import no.nordicsemi.android.common.theme.NordicActivity
import no.nordicsemi.android.common.theme.NordicTheme

@AndroidEntryPoint
class MainActivity : NordicActivity() {
    private var blinky: BlinkyAPI? = null
    private val TAG = "BluetoothLeService"

    @RequiresApi(Build.VERSION_CODES.M)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            NordicTheme {
                NavigationView(destinations = destinations)

            }
        }
    }

    private val Main = DestinationId(NavigationConst.HOME)
    private val ConnectView = DestinationId(NavigationConst.CONNECT_DEVICE)

    private val destinations =
        ComposeDestinations(listOf(
            ComposeDestination(Main) { navigationManager ->
                HomeView(navigationManager)
            },
            ComposeDestination(ConnectView) { navigationManager ->
                deviceAddress?.let { deviceAddress ->
                    Log.e("Device address", deviceAddress)
                    LaunchedEffect(deviceAddress) {
                        connect(deviceAddress)
                    }
                    ConnectDeviceView(
                        navigationManager = navigationManager,
                        deviceAddress = deviceAddress,
                        onLedChange = { blinky?.turnLed(it) },
                        buttonsState = blinky?.getButtonState()?.collectAsState(false)?.value
                            ?: false
                    )
                }
            }
        ))


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
                    Log.w(TAG, "Unable to initialize Bluetooth")
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

}
