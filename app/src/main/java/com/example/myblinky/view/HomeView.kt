package com.example.myblinky.view

import android.annotation.SuppressLint
import android.util.Log
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.myblinky.R

@SuppressLint("MissingPermission", "UnrememberedMutableState")
@Composable
fun HomeView(navController: NavController, isBluetoothEnabled: MutableState<Boolean>) {

    Column {
        TopAppBar(

            title = { Text(stringResource(id = R.string.app_name)) }

        )

        Column {
            Surface(color = MaterialTheme.colors.background) {
                if (isBluetoothEnabled.value) {
                    Log.i("Bluetooth state is ON", "$isBluetoothEnabled")
                    ScannedDevices()
                } else {


                    Log.i("Bluetooth state is OFF", "$isBluetoothEnabled")
                    ConnectBluetoothView()
                }
            }
        }
    }
}


@Composable
fun ScannedDevices(names: List<String> = List(10) { "Device $it" }) {
    LazyColumn(modifier = Modifier.padding(vertical = 4.dp)) {
        items(items = names) { name ->
            Scanning(name = name)
        }
    }
}


@Composable
fun Scanning(name: String) {
    val expanded = remember { mutableStateOf(false) }
    val extraPadding = if (expanded.value) 48.dp else 0.dp

    Surface(
        color = Color.Gray,
        modifier = Modifier.padding(vertical = 4.dp, horizontal = 8.dp)
    ) {
        Row(modifier = Modifier.padding(24.dp)) {
            Column(
                modifier = Modifier
                    .weight(1f)
                    .padding(bottom = extraPadding)
            ) {
                Text(text = "New")
                Text(text = name)

            }
            OutlinedButton(
                onClick = { expanded.value = !expanded.value },
            ) {
                Text(if (expanded.value) "Device Selected" else "Connected device")
            }
        }

    }
}
