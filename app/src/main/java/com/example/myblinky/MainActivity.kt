package com.example.myblinky

import android.bluetooth.BluetoothDevice
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.content.ServiceConnection
import android.os.Bundle
import android.os.IBinder
import androidx.activity.compose.setContent
import androidx.compose.material3.Surface
import androidx.compose.runtime.*
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.flowWithLifecycle
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
                    val blinky = rememberBoundLocalService(device)
                    blinky?.let {
                        val flow = remember(blinky) {
                            blinky.getButtonState().flowWithLifecycle(lifecycle, Lifecycle.State.STARTED)
                        }
                        val buttonState by flow.collectAsState(false)

                        ConnectDeviceView(
                            navigationManager = navigationManager,
                            device = device,
                            onLedChange = { blinky.turnLed(it) },
                            buttonState = buttonState,
                        )
                    }
                }
            }
        ))

    @Composable
    fun rememberBoundLocalService(device: BluetoothDevice): BlinkyAPI? {
        val context: Context = LocalContext.current
        var boundService: BlinkyAPI? by remember(context) { mutableStateOf(null) }
        val serviceConnection: ServiceConnection = remember(context) {
            object : ServiceConnection {
                override fun onServiceConnected(className: ComponentName, service: IBinder) {
                    boundService = service as BlinkyAPI
                }

                override fun onServiceDisconnected(arg0: ComponentName) {
                    boundService = null
                }
            }
        }
        DisposableEffect(context, serviceConnection) {
            val intent = Intent(context, BluetoothLeService::class.java)
            intent.putExtra("ADDRESS", device.address)
            context.startService(intent)
            context.bindService(intent, serviceConnection, Context.BIND_AUTO_CREATE)
            onDispose {
                context.unbindService(serviceConnection)
                boundService = null
            }
        }
        return boundService
    }

    companion object {
        val Main = DestinationId(NavigationConst.HOME)
        val ConnectView = DestinationId(NavigationConst.CONNECT_DEVICE)
    }


}
