package com.example.myblinky.view

import android.bluetooth.BluetoothGattCharacteristic
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController

@Composable
fun ShowServiceCharacteristics(navController: NavController, device: String) {
    Column {
        TopAppBar(
            title = { Text(text = device) },
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
            val charService: List<BluetoothGattCharacteristic> = mutableListOf()
            LazyColumn(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                items(items = charService) { charService ->
                    ShowServiceCharacteristics(characteristic = charService)
                }
            }
        }
    }
}

@Composable
fun ShowServiceCharacteristics(characteristic: BluetoothGattCharacteristic) {
    Surface(
        modifier = Modifier.padding(vertical = 4.dp, horizontal = 8.dp)
    ) {
        Row(
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        )

        {
            Box(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth()

            ) {
                Column() {
                    Text(
                        text = characteristic.uuid.toString(),
                        modifier = Modifier
                            .padding(vertical = 15.dp, horizontal = 10.dp),
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        text = characteristic.permissions.toString(),
                        modifier = Modifier
                            .padding(vertical = 15.dp, horizontal = 10.dp)
                    )
                    Text(
                        text = characteristic.writeType.toString(),
                        modifier = Modifier
                            .padding(vertical = 15.dp, horizontal = 10.dp)
                    )
                    Divider()

                }

            }
        }
    }
}


