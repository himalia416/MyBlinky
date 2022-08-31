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
fun ConnectDeviceView(navController: NavController, deviceName: String) {
    val ledStateOn = "ON"
    val ledStateOff = "OFF"
    val buttonName = "BUTTON"
    val ledName = "LED"
    val ledDescription = "Toggle the switch to turn LED on or off"
    val buttonPressed = "PRESSED"
    val buttonReleased = "RELEASED"
    val buttonDescription = "Press Button #xx on the dev kit"
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
            @Composable
            fun checkLEDState(buttonStyle: String, isPressed: Boolean) {
                val stateOn: String
                val stateOff: String
                if (buttonStyle == ledName) {
                    stateOn = ledStateOn
                    stateOff = ledStateOff

                } else {
                    stateOn = buttonPressed
                    stateOff = buttonReleased
                }
                Text(
                    text = when (isPressed) {
                        true -> stateOn
                        false -> stateOff
                    }, modifier = Modifier.padding(10.dp)
                )
            }

            @Composable
            fun toggleDevice(device: String) {
                val mCheckedState = remember {
                    mutableStateOf(false)
                }

                Row(
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier
                        .padding(2.dp)
                        .fillMaxWidth(),
                ) {
                    if (device == ledName) {
                        checkLEDState(device, mCheckedState.value)
                        Switch(
                            checked = mCheckedState.value,
                            onCheckedChange = { mCheckedState.value = it },
                            colors = SwitchDefaults.colors(
                                checkedThumbColor = Color.DarkGray,
                                uncheckedThumbColor = Color.Gray,
                                checkedTrackColor = Color.Blue,
                                uncheckedTrackColor = Color.LightGray,
                            )
                        )

                    } else {
                        Text(text = "State")
                        checkLEDState(device, mCheckedState.value)
                    }
                }
            }

            @Composable
            fun detailDeviceItems(name: String, itemDescription: String) {

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
                            toggleDevice(name)

                        }
                    }
                }
            }
            detailDeviceItems(name = ledName, itemDescription = ledDescription)
            detailDeviceItems(name = buttonName, itemDescription = buttonDescription)
        }
    }
}