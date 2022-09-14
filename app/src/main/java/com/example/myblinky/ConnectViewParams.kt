package com.example.myblinky

import no.nordicsemi.android.common.navigation.DestinationId
import no.nordicsemi.android.common.navigation.NavigationArgument

val connectDevice = DestinationId(NavigationConst.CONNECT_DEVICE)
data class ConnectViewParams(val device: String? ): NavigationArgument{
    override val destinationId: DestinationId = connectDevice
}
