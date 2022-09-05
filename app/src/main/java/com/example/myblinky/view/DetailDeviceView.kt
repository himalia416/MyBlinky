package com.example.myblinky.view

import androidx.compose.foundation.layout.*
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController


@Composable
fun ConnectDeviceView(
    navController: NavController,
    deviceName: String,
    onLedChange: (Boolean) -> Unit,
    buttonsState: Boolean,
) {
    val buttonName = "BUTTON"
    val ledName = "LED"
    val ledDescription = "Toggle the switch to turn LED on or off"
    val buttonDescription = "Press Button 1 on the dev kit"

    Column {
        TopAppBar(
            title = { Text(text = deviceName) },
            navigationIcon = if (navController.previousBackStackEntry != null) {
                {
                    IconButton(onClick = { navController.navigateUp() }) {
                        Icon(
                            imageVector = Icons.Filled.ArrowBack,
                            contentDescription = "Back"
                        )
                    }
                }
            } else {
                null
            }
        )
        Column(
            modifier = Modifier.padding(2.dp)
        ) {
            LedView(name = ledName, itemDescription = ledDescription, onLedChange)
            ButtonView(name = buttonName, itemDescription = buttonDescription, buttonsState)
        }
    }
}

@Composable
fun LedView(name: String, itemDescription: String, onLedChange: (Boolean) -> Unit) {

    Row(
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier
            .padding(horizontal = 2.dp),
    ) {
        Card(
            modifier = Modifier
                .weight(1f)
                .padding(vertical = 15.dp)

        ) {
            Column(
                modifier = Modifier.padding(10.dp)
            ) {
                Text(
                    text = name,
                    modifier = Modifier.padding(4.dp),
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = itemDescription,
                    modifier = Modifier.padding(4.dp),
                    textAlign = TextAlign.Center
                )
                toggleLedSwitch(name, onLedChange)

            }
        }
    }
}

@Composable
fun ButtonView(name: String, itemDescription: String, buttonsState: Boolean) {

    Row(
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier
            .padding(horizontal = 2.dp),
    ) {
        Card(
            modifier = Modifier
                .weight(1f)
                .padding(vertical = 15.dp)

        ) {
            Column(
                modifier = Modifier.padding(10.dp)
            ) {
                Text(
                    text = name,
                    modifier = Modifier.padding(4.dp),
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = itemDescription,
                    modifier = Modifier.padding(4.dp),
                    textAlign = TextAlign.Center
                )
                onButtonPressed(name, buttonsState)

            }
        }
    }
}

@Composable
fun onButtonPressed(device: String, buttonsState: Boolean) {
    val mCheckedState = remember {
        mutableStateOf(false)
    }
    val buttonPressed = "PRESSED"
    val buttonReleased = "RELEASED"

    Row(
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier
            .padding(2.dp)
            .fillMaxWidth(),
    ) {

        mCheckedState.value = buttonsState
        Text(text = "State")
        Text(
            text = when (mCheckedState.value) {
                true -> buttonPressed
                false -> buttonReleased
            }, modifier = Modifier.padding(10.dp)
        )
    }
}

@Composable
fun toggleLedSwitch(device: String, onLedChange: (Boolean) -> Unit) {
    val mCheckedState = remember {
        mutableStateOf(false)
    }
    val ledStateOn = "ON"
    val ledStateOff = "OFF"

    Row(
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier
            .padding(2.dp)
            .fillMaxWidth(),
    ) {
        Text(
            text = when (mCheckedState.value) {
                true -> ledStateOn
                false -> ledStateOff
            }, modifier = Modifier.padding(10.dp)
        )
        Switch(
            checked = mCheckedState.value,
            onCheckedChange = onLedChange,// { mCheckedState.value = it },
            colors = SwitchDefaults.colors(
                checkedThumbColor = Color.DarkGray,
                uncheckedThumbColor = Color.Gray,
                checkedTrackColor = Color.Blue,
                uncheckedTrackColor = Color.LightGray,
            )
        )

    }
}