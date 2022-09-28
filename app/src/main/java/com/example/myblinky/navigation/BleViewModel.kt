package com.example.myblinky.navigation

import androidx.compose.runtime.*
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.myblinky.ConnectViewParams
import com.example.myblinky.MainActivity
import com.example.myblinky.MainActivity.Companion.ConnectView
import com.example.myblinky.screen.ControlDeviceScreen
import com.example.myblinky.screen.ScanningScreen
import com.example.myblinky.service.rememberBoundLocalService
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.stateIn
import no.nordicsemi.android.common.navigation.ComposeDestination
import no.nordicsemi.android.common.navigation.ComposeDestinations
import javax.inject.Inject

@HiltViewModel
class BleViewModel  @Inject constructor() : ViewModel() {
    private val blinkyControl =   ComposeDestination(ConnectView) { navigationManager ->
        val deviceSelected = remember {
            (navigationManager.getArgument(ConnectView) as? ConnectViewParams)?.device
        }
        deviceSelected?.let { device ->
            val blinky = rememberBoundLocalService(device)
            blinky?.let {
                val flow = remember(blinky) {
                    blinky.getButtonState().stateIn(viewModelScope, SharingStarted.Lazily, false) //flowWithLifecycle(lifecycle, Lifecycle.State.STARTED)
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
    val blinkyControlDestination = ComposeDestinations( blinkyControl )

    private val Scanner =  ComposeDestination(MainActivity.Main) { navigationManager ->
        ScanningScreen(navigationManager)
    }
    val scanningDestinations = ComposeDestinations ( Scanner )
}