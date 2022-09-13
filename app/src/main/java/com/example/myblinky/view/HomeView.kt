package com.example.myblinky.view

import android.bluetooth.le.ScanResult
import android.os.Build
import android.util.Log
import androidx.annotation.RequiresApi
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.example.myblinky.NavigationConst
import com.example.myblinky.R
import com.example.myblinky.permissions.RequireScanPermission
import com.example.myblinky.viewmodel.HomeViewModel

@RequiresApi(Build.VERSION_CODES.M)
@Composable
fun HomeView(navController: NavController, isBluetoothEnabled: MutableState<Boolean>) {
    Column {
        TopAppBar(
            title = { Text(stringResource(id = R.string.app_name)) }
        )
        Column(modifier = Modifier.padding(1.dp)) {
            Surface(color = MaterialTheme.colors.background) {
                if (isBluetoothEnabled.value) {
                    Log.i("Bluetooth state is: ", "$isBluetoothEnabled")
                    Scanning(navController)
                } else {
                    Log.i("Bluetooth state is: ", "$isBluetoothEnabled")
                    ConnectBluetoothView()
                }
            }
        }
    }
}

@RequiresApi(Build.VERSION_CODES.M)
@Composable
fun Scanning(navController: NavController) {
    val viewModel = hiltViewModel<HomeViewModel>()

    Surface(
        color = Color.White,
        modifier = Modifier.padding( horizontal = 4.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(top = 16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            RequireScanPermission {
                ScannedDevices(navController)
                LaunchedEffect(navController) {
                    viewModel.startScanning()
                }
            }
        }
    }

}


@Composable
fun ScannedDevices(navController: NavController) {
    val viewModel = hiltViewModel<HomeViewModel>()
    val devices: State<List<ScanResult>> = viewModel.mLeDevices.collectAsState()

    LazyColumn(verticalArrangement = Arrangement.spacedBy(8.dp)) {
        items(items = devices.value) { devices ->
            ShowScannedDevices(navController, devices = devices)
        }
    }
}

@Composable
fun ShowScannedDevices(
    navController: NavController,
    devices: ScanResult,
) {
    val viewModel = hiltViewModel<HomeViewModel>()

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
                    .clickable {
                        viewModel.stopBleScan()
                        navController.navigate(
                            "${NavigationConst.CONNECT_DEVICE}/${devices.device?.address}"
                        )
                    }
            ) {
                Column {
                    Text(
                        text = devices.device?.name.toString(),
                        modifier = Modifier
                            .padding(vertical = 16.dp, horizontal = 8.dp),
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        text = devices.device?.address.toString(),
                        modifier = Modifier
                            .padding(vertical = 16.dp, horizontal = 8.dp)
                    )
                    Divider()
                }
            }
        }
    }
}
