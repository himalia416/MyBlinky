package com.example.myblinky.view

import androidx.compose.foundation.layout.*
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.myblinky.R


@Composable
fun ConnectDeviceView(
    navController: NavController,
    deviceName: String,
    onLedChange: (Boolean) -> Unit,
    buttonsState: Boolean,
) {

    Column {
        TopAppBar(
            title = { Text(text = deviceName) },
            navigationIcon = if (navController.previousBackStackEntry != null) {
                {
                    IconButton(onClick = { navController.navigateUp() }) {
                        Icon(
                            imageVector = Icons.Filled.ArrowBack,
                            contentDescription = stringResource(id = R.string.back)
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
            LedView(
                name = stringResource(id = R.string.led_name), itemDescription = stringResource(
                    id = R.string.led_description
                ), onLedChange
            )
            ButtonView(
                name = stringResource(id = R.string.button_name),
                itemDescription = stringResource(id = R.string.button_description),
                buttonsState
            )
        }
    }
}

@Composable
fun LedView(name: String, itemDescription: String, onLedChange: (Boolean) -> Unit) {
    var isLedOn by remember { mutableStateOf(false) }

    Row(
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier
            .padding(horizontal = 2.dp),
    ) {
        Card(
            modifier = Modifier
                .weight(1f)
                .padding(vertical = 16.dp)

        ) {
            Column(
                modifier = Modifier.padding(8.dp)
            ) {
                Row(modifier = Modifier.padding(8.dp)) {

                    Text(
                        text = name,
                        modifier = Modifier.padding(4.dp),
                        fontWeight = FontWeight.Bold
                    )
                }

                Text(
                    text = itemDescription,
                    modifier = Modifier.padding(4.dp),
                    textAlign = TextAlign.Center
                )
                toggleLedSwitch(change = isLedOn, onLedChange = {
                    onLedChange(it)
                    isLedOn = it
                })

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
                .padding(vertical = 16.dp)

        ) {
            Column(
                modifier = Modifier.padding(8.dp)
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
                onButtonPressed(buttonsState)
            }
        }
    }
}

@Composable
fun onButtonPressed(buttonsState: Boolean) {
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

        mCheckedState.value = buttonsState
        Text(text = "State")
        Text(
            text = when (mCheckedState.value) {
                true -> stringResource(id = R.string.button_pressed)
                false -> stringResource(id = R.string.button_released)
            }, modifier = Modifier.padding(10.dp)
        )
    }
}

@Composable
fun toggleLedSwitch(change: Boolean, onLedChange: (Boolean) -> Unit) {

    Row(
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier
            .padding(2.dp)
            .fillMaxWidth(),
    ) {
        Text(
            text = when (change) {
                true -> stringResource(id = R.string.led_on)
                false -> stringResource(id = R.string.led_off)
            }, modifier = Modifier.padding(10.dp)
        )
        Switch(
            checked = change,
            onCheckedChange = onLedChange,
            colors = SwitchDefaults.colors(
                checkedThumbColor = Color.DarkGray,
                uncheckedThumbColor = Color.Gray,
                checkedTrackColor = Color.Blue,
                uncheckedTrackColor = Color.LightGray,
            )
        )
    }
}