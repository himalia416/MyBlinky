package com.example.myblinky.scanner.view

import android.annotation.SuppressLint
import android.app.Activity
import android.bluetooth.BluetoothAdapter
import android.content.Intent
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp


@SuppressLint("MissingPermission")
@Composable
fun ConnectBluetoothView() {
    val context = LocalContext.current
    val REQUEST_ENABLE_BT = 3
    Surface {
        Column(
            modifier = Modifier.fillMaxSize(),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text("Please Enable Bluetooth!")
            Button(
                modifier = Modifier.padding(vertical = 24.dp),
                onClick = {
                    val enableBtIntent = Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE)
                    (context as Activity).startActivityForResult(
                        enableBtIntent,
                        REQUEST_ENABLE_BT
                    )
                }
            ) {
                Text("Continue")
            }
        }
    }
}