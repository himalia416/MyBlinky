package com.example.myblinky.navigation

import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import com.example.myblinky.ConnectViewParams
import com.example.myblinky.MainActivity
import com.example.myblinky.screen.ControlDeviceScreen
import com.example.myblinky.service.rememberBoundLocalService
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.stateIn
import no.nordicsemi.android.common.navigation.ComposeDestination
import no.nordicsemi.android.common.navigation.ComposeDestinations

private val scope: CoroutineScope = CoroutineScope( Dispatchers.IO )

private val BlinkyControl =   ComposeDestination(MainActivity.ConnectView) { navigationManager ->
    val deviceSelected = remember {
        (navigationManager.getArgument(MainActivity.ConnectView) as? ConnectViewParams)?.device
    }
    deviceSelected?.let { device ->
        val blinky = rememberBoundLocalService(device)
        blinky?.let {
            val flow = remember(blinky) {
                blinky.getButtonState().stateIn(scope, SharingStarted.Lazily, false)
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
val BlinkyControlDestination = ComposeDestinations( BlinkyControl )

