package com.example.myblinky.blinky.navigation

import android.bluetooth.BluetoothDevice
import com.example.myblinky.MainActivity.Companion.ConnectView
import no.nordicsemi.android.common.navigation.DestinationId
import no.nordicsemi.android.common.navigation.NavigationArgument

data class BlinkyDestinationParams(val device: BluetoothDevice): NavigationArgument {
    override val destinationId: DestinationId = ConnectView
}
