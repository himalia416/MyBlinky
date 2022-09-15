package com.example.myblinky.viewmodel

import androidx.lifecycle.ViewModel
import com.example.myblinky.NavigationConst
import dagger.hilt.android.lifecycle.HiltViewModel
import no.nordicsemi.android.common.navigation.DestinationId
import no.nordicsemi.android.common.navigation.NavigationManager
import javax.inject.Inject

@HiltViewModel
class NavigationViewModel @Inject constructor(
    navigationManager: NavigationManager
) : ViewModel() {
    private val ConnectView = DestinationId(NavigationConst.CONNECT_DEVICE)
   val connectDeviceArgs =  navigationManager.getArgumentForId(ConnectView)
}