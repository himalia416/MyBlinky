package com.example.myblinky.screen

import android.bluetooth.BluetoothDevice
import androidx.compose.foundation.layout.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.example.myblinky.R
import com.example.myblinky.view.ButtonView
import com.example.myblinky.view.LedView
import no.nordicsemi.android.common.navigation.NavigationManager
import no.nordicsemi.android.common.theme.view.NordicAppBar


@Composable
fun ControlDeviceScreen(
    navigationManager: NavigationManager,
    device: BluetoothDevice,
    onLedChange: (Boolean) -> Unit,
    buttonState: Boolean,
) {
    Column {
        NordicAppBar(
            text = device.name,
            onNavigationButtonClick = { navigationManager.navigateUp() }
        )
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            LedView(
                ledName = stringResource(id = R.string.led_name),
                ledDescription = stringResource(
                    id = R.string.led_description
                ),
                onLedChange = onLedChange,
                modifier = Modifier.fillMaxWidth()
            )
            Spacer(modifier = Modifier.height(8.dp))
            ButtonView(
                buttonName = stringResource(id = R.string.button_name),
                buttonDescription = stringResource(id = R.string.button_description),
                buttonsState = buttonState,
                modifier = Modifier.fillMaxWidth()
            )
        }
    }
}