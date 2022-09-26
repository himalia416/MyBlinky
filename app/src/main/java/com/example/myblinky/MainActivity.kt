package com.example.myblinky

import android.os.Bundle
import androidx.activity.compose.setContent
import androidx.compose.material3.Surface
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.flowWithLifecycle
import com.example.myblinky.service.rememberBoundLocalService
import com.example.myblinky.screen.ControlDeviceScreen
import com.example.myblinky.screen.ScanningScreen
import dagger.hilt.android.AndroidEntryPoint
import no.nordicsemi.android.common.navigation.ComposeDestination
import no.nordicsemi.android.common.navigation.ComposeDestinations
import no.nordicsemi.android.common.navigation.DestinationId
import no.nordicsemi.android.common.navigation.NavigationView
import no.nordicsemi.android.common.theme.NordicActivity
import no.nordicsemi.android.common.theme.NordicTheme


@AndroidEntryPoint
class MainActivity : NordicActivity() {

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
                ScanningScreen(navigationManager)
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

                        ControlDeviceScreen(
                            navigationManager = navigationManager,
                            device = device,
                            onLedChange = { blinky.turnLed(it) },
                            buttonState = buttonState,
                        )
                    }
                }
            }
        ))

    companion object {
        val Main = DestinationId(NavigationConst.HOME)
        val ConnectView = DestinationId(NavigationConst.CONTROL_BLINKY)
    }
}
