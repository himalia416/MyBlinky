package com.example.myblinky.navigation

import com.example.myblinky.MainActivity
import com.example.myblinky.screen.ScanningScreen
import no.nordicsemi.android.common.navigation.ComposeDestination
import no.nordicsemi.android.common.navigation.ComposeDestinations

private val Scanner =  ComposeDestination(MainActivity.Main) { navigationManager ->
    ScanningScreen(navigationManager)
}
val ScanningDestinations = ComposeDestinations ( Scanner )