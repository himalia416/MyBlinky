package com.example.myblinky

import android.bluetooth.BluetoothDevice
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.content.ServiceConnection
import android.os.Bundle
import android.os.IBinder
import android.util.Log
import androidx.activity.compose.setContent
import androidx.compose.material3.Surface
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.remember
import com.example.myblinky.adapter.BlinkyAPI
import com.example.myblinky.adapter.BluetoothLeService
import com.example.myblinky.view.ConnectDeviceView
import com.example.myblinky.view.ScanningView
import dagger.hilt.android.AndroidEntryPoint
import no.nordicsemi.android.common.navigation.ComposeDestination
import no.nordicsemi.android.common.navigation.ComposeDestinations
import no.nordicsemi.android.common.navigation.DestinationId
import no.nordicsemi.android.common.navigation.NavigationView
import no.nordicsemi.android.common.theme.NordicActivity
import no.nordicsemi.android.common.theme.NordicTheme



@AndroidEntryPoint
class MainActivity : NordicActivity() {
    private var blinky: BlinkyAPI? = null
    private val TAG = "BluetoothLeService"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContent {
            NordicTheme {
                Surface {
                    NavigationView(destinations = destinations)
                }
            }
        }
    }


    private val destinations =
        ComposeDestinations(listOf(
            ComposeDestination(Main) { navigationManager ->
                ScanningView(navigationManager)
            },
            ComposeDestination(ConnectView) { navigationManager ->
                val deviceSelected = remember {
                    (navigationManager.getArgument(ConnectView) as? ConnectViewParams)?.device
                }
                deviceSelected?.let { device ->
                    LaunchedEffect(device) {
                        connect(device)
                    }
                    val buttonsState = blinky?.getButtonState()?.collectAsState(false)?.value ?: false
                    ConnectDeviceView(
                        navigationManager = navigationManager,
                        device = device,
                        onLedChange = { blinky?.turnLed(it) },
                        buttonsState = buttonsState,
                    )
                }
            }
        ))


    private var deviceAddress: String? = null

     private fun connect(device: BluetoothDevice) {
        this.deviceAddress = device.address

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
            Log.w("AAA", "Connected $blinky")
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
    companion object {
        val Main = DestinationId(NavigationConst.HOME)
        val ConnectView = DestinationId(NavigationConst.CONNECT_DEVICE)
    }


}
