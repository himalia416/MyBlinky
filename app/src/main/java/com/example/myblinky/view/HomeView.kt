package com.example.myblinky.view

import android.bluetooth.le.ScanResult
import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.Divider
import androidx.compose.material.Surface
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.State
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.myblinky.ConnectViewParams
import com.example.myblinky.NavigationConst
import com.example.myblinky.R
import com.example.myblinky.viewmodel.HomeViewModel
import no.nordicsemi.android.common.navigation.DestinationId
import no.nordicsemi.android.common.navigation.NavigationManager
import no.nordicsemi.android.common.permission.RequireBluetooth
import no.nordicsemi.android.common.theme.view.NordicAppBar

@RequiresApi(Build.VERSION_CODES.M)
@Composable
fun HomeView(navController: NavigationManager) {
    val viewModel = hiltViewModel<HomeViewModel>()

    Column {
        NordicAppBar(
            text = stringResource(id = R.string.app_name)
        )
        Column(modifier = Modifier.padding(2.dp)) {
            Surface {
                RequireBluetooth {
                    Surface(
                        color = Color.White,
                        modifier = Modifier.padding(horizontal = 4.dp)
                    ) {
                        Column(
                            modifier = Modifier
                                .fillMaxSize()
                                .padding(top = 16.dp),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
//                            RequireScanPermission {
                                ScannedDevices(navController)
                                LaunchedEffect(navController) {
                                    viewModel.startScanning()
                                }
//                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun ScannedDevices(navController: NavigationManager) {
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
    navigationManager: NavigationManager,
    devices: ScanResult,
) {
    val viewModel = hiltViewModel<HomeViewModel>()
    val connectDevice = DestinationId(NavigationConst.CONNECT_DEVICE)

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
                        navigationManager.navigateTo(
                            destination = connectDevice,
                            ConnectViewParams("${devices.device?.address}")
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
