package com.example.myblinky.view

import android.bluetooth.BluetoothDevice
import android.util.Log
import androidx.compose.foundation.layout.*
import androidx.compose.material3.OutlinedCard
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.example.myblinky.R
import no.nordicsemi.android.common.navigation.NavigationManager
import no.nordicsemi.android.common.theme.view.NordicAppBar


@Composable
fun ConnectDeviceView(
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
                name = stringResource(id = R.string.led_name),
                itemDescription = stringResource(
                    id = R.string.led_description
                ),
                onLedChange = onLedChange,
                modifier = Modifier.fillMaxWidth()
            )
            Spacer(modifier = Modifier.height(8.dp))
            ButtonView(
                name = stringResource(id = R.string.button_name),
                itemDescription = stringResource(id = R.string.button_description),
                buttonsState = buttonState,
                modifier = Modifier.fillMaxWidth()
            )
        }
    }
}

@Composable
fun LedView(
    name: String,
    itemDescription: String,
    onLedChange: (Boolean) -> Unit,
    modifier: Modifier = Modifier,
) {
    var isLedOn by remember { mutableStateOf(false) }

    OutlinedCard(
        modifier = modifier
    ) {
        Column(
            modifier = Modifier
                .padding(16.dp)
        ) {
            Text(
                text = name,
                fontWeight = FontWeight.Bold
            )
            Text(
                text = itemDescription,
                modifier = Modifier.padding(top = 4.dp),
            )
            ToggleLedSwitch(
                change = isLedOn,
                onLedChange = {
                    onLedChange(it)
                    isLedOn = it
                }
            )
        }
    }
}

@Composable
fun ButtonView(
    name: String,
    itemDescription: String,
    buttonsState: Boolean,
    modifier: Modifier,
) {
    OutlinedCard(
        modifier = modifier
    ) {
        Column(
            modifier = Modifier
                .padding(16.dp)
        ) {
            Text(
                text = name,
                fontWeight = FontWeight.Bold
            )
            Text(
                text = itemDescription,
                modifier = Modifier.padding(top = 4.dp),
                textAlign = TextAlign.Center
            )
            Row(
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier
                    .fillMaxWidth(),
            ) {
                Text(text = "State")
                ButtonState(buttonsState)
            }
        }
    }
}

@Composable
fun ButtonState(
    buttonsState: Boolean,
    modifier: Modifier = Modifier,
) {
    Log.w("button pressed", "button pressed $buttonsState")
    Text(
        text = when (buttonsState) {
            true -> stringResource(id = R.string.button_pressed)
            false -> stringResource(id = R.string.button_released)
        },
        modifier = modifier,
    )
}

@Composable
fun ToggleLedSwitch(
    change: Boolean,
    onLedChange: (Boolean) -> Unit,
    modifier: Modifier = Modifier,
) {
    Row(
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically,
        modifier = modifier,
    ) {
        Text(
            text = when (change) {
                true -> stringResource(id = R.string.led_on)
                false -> stringResource(id = R.string.led_off)
            },
            modifier = Modifier.weight(1f),
        )
        Switch(
            checked = change,
            onCheckedChange = onLedChange,
        )
    }
}