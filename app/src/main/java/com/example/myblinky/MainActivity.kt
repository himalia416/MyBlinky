package com.example.myblinky
import android.os.Bundle
import androidx.activity.compose.setContent
import androidx.compose.material3.Surface
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.flowWithLifecycle
import com.example.myblinky.navigation.ScanningDestinations
import com.example.myblinky.service.rememberBoundLocalService
import com.example.myblinky.screen.ControlDeviceScreen
import dagger.hilt.android.AndroidEntryPoint
import no.nordicsemi.android.common.navigation.ComposeDestination
import no.nordicsemi.android.common.navigation.ComposeDestinations
import no.nordicsemi.android.common.navigation.DestinationId
import no.nordicsemi.android.common.navigation.NavigationView
import no.nordicsemi.android.common.permission.RequireBluetooth
import no.nordicsemi.android.common.theme.NordicActivity
import no.nordicsemi.android.common.theme.NordicTheme

@AndroidEntryPoint
class MainActivity : NordicActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContent {
            NordicTheme {
                Surface {
                    RequireBluetooth {
                        NavigationView(destinations = ScanningDestinations + BlinkyControlDestination)
                    }
                }
            }
        }
    }

private val blinkyControl =   ComposeDestination(ConnectView) { navigationManager ->
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

    val BlinkyControlDestination = ComposeDestinations( blinkyControl )

    companion object {
        val Main = DestinationId(NavigationConst.HOME)
        val ConnectView = DestinationId(NavigationConst.CONTROL_BLINKY)
    }
}




