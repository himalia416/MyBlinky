package com.example.myblinky.navigation

import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.flowWithLifecycle
import com.example.myblinky.ConnectViewParams
import com.example.myblinky.MainActivity
import com.example.myblinky.screen.ControlDeviceScreen
import com.example.myblinky.service.rememberBoundLocalService
import no.nordicsemi.android.common.navigation.ComposeDestination
import no.nordicsemi.android.common.navigation.ComposeDestinations



// val BlinkyDestination = ComposeDestinations(
//    ComposeDestination(MainActivity.ConnectView) { navigationManager ->
//        val deviceSelected = remember {
//            (navigationManager.getArgument(MainActivity.ConnectView) as? ConnectViewParams)?.device
//        }
//        deviceSelected?.let { device ->
//            val blinky = rememberBoundLocalService(device)
//            blinky?.let {
//                val flow = remember(blinky) {
//                    blinky.getButtonState().flowWithLifecycle(lifecycle, Lifecycle.State.STARTED)
//                }
//                val buttonState by flow.collectAsState(false)
//
//                ControlDeviceScreen(
//                    navigationManager = navigationManager,
//                    device = device,
//                    onLedChange = { blinky.turnLed(it) },
//                    buttonState = buttonState,
//                )
//            }
//        }
//    }
//)
