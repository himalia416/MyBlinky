package com.example.myblinky.view

import android.annotation.SuppressLint
import android.bluetooth.le.ScanResult
import android.os.Build
import android.util.Log
import androidx.annotation.RequiresApi
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Surface
import androidx.compose.material.Text
import androidx.compose.material.TopAppBar
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.example.myblinky.R

@RequiresApi(Build.VERSION_CODES.M)
@Composable
fun HomeView(navController: NavController, isBluetoothEnabled: MutableState<Boolean>) {

    Column {
        TopAppBar(

            title = { Text(stringResource(id = R.string.app_name)) }

        )
        Column {
            Surface(color = MaterialTheme.colors.background) {
                if (isBluetoothEnabled.value) {
                    Log.i("Bluetooth state is: ", "$isBluetoothEnabled")
                    Scanning()
                } else {
                    Log.i("Bluetooth state is: ", "$isBluetoothEnabled")
                    ConnectBluetoothView()
                }
            }
        }
    }
}

@RequiresApi(Build.VERSION_CODES.M)
@SuppressLint("MissingPermission")
@Composable
fun Scanning() {
    val viewModel = hiltViewModel<HomeViewModel>()
    val permissionGranted = viewModel.permissionGranted.collectAsState().value

    Surface(
        color = Color.Gray,
        modifier = Modifier.padding(vertical = 4.dp, horizontal = 8.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(top = 20.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            if (permissionGranted) {
                ScannedDevices()
                LaunchedEffect(permissionGranted) {
                    if (permissionGranted) {
                        viewModel.startScanning()
                    }
                }
            } else {
                //TODO show message
            }

        }
    }

}


@Composable
fun ScannedDevices() {
    val viewModel = hiltViewModel<HomeViewModel>()
    val devices: State<List<ScanResult>> = viewModel.mLeDevices.collectAsState()
    LazyColumn(modifier = Modifier.padding(vertical = 4.dp)) {
        items(items = devices.value) { devices ->
            ShowScannedDevices(devices = devices)
        }
    }
}

@Composable
fun ShowScannedDevices(devices: ScanResult) {
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
                Text(text = devices.toString())

            }
        }
    }
}
