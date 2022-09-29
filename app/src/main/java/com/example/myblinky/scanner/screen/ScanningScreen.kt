package com.example.myblinky.scanner.screen

import android.bluetooth.BluetoothDevice
import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Divider
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.myblinky.blinky.navigation.BlinkyDestinationParams
import com.example.myblinky.MainActivity.Companion.ConnectView
import com.example.myblinky.R
import com.example.myblinky.scanner.view.FilterDropDownView
import com.example.myblinky.scanner.viewmodel.FilterDropDownViewModel
import com.example.myblinky.scanner.viewmodel.ScanningViewModel
import com.google.accompanist.swiperefresh.SwipeRefresh
import com.google.accompanist.swiperefresh.rememberSwipeRefreshState
import no.nordicsemi.android.common.navigation.NavigationManager
import no.nordicsemi.android.common.permission.RequireBluetooth
import no.nordicsemi.android.common.theme.view.NordicAppBar

@RequiresApi(Build.VERSION_CODES.M)
@Composable
fun ScanningScreen(navigationManager: NavigationManager) {
    val viewModel = hiltViewModel<ScanningViewModel>()
    val filterViewModel = hiltViewModel<FilterDropDownViewModel>()
    val filter by remember { filterViewModel.filterOptions }
    val isScanning by viewModel.scanning.collectAsState(false)

    Column {
        NordicAppBar(
            text = stringResource(id = R.string.app_name)
        ) {
            FilterDropDownView(
                filterOptions = listOf(filter),
                onFilterChanged = { isSelected ->
                    filterViewModel.setFilterSelected(isSelected)
                }
            )
        }

        if (isScanning) {
            LinearProgressIndicator(
                modifier = Modifier
                    .fillMaxWidth()
            )
        }

        Column {
            val swipeRefreshState = rememberSwipeRefreshState(isRefreshing = false)
            RequireBluetooth {
                SwipeRefresh(
                    state = swipeRefreshState,
                    onRefresh = {
                        viewModel.clearScanResult()
                        if (!isScanning) {
                            viewModel.startScanning(filter.isSelected)
                        }
                    }
                ) {
                    ScannedDevices(navigationManager)
                    DisposableEffect(filter) {
                        viewModel.startScanning(filter.isSelected)
                        onDispose { viewModel.stopBleScan() }
                    }
                }
            }
        }
    }
}

@Composable
fun ScannedDevices(navController: NavigationManager) {
    val viewModel = hiltViewModel<ScanningViewModel>()
    val devices by viewModel.devices.collectAsState()

    val action: (BluetoothDevice) -> Unit = {
        navController.navigateTo(
            destination = ConnectView,
            BlinkyDestinationParams(it)
        )
    }
    LazyColumn(
        verticalArrangement = Arrangement.spacedBy(8.dp),
        contentPadding = PaddingValues(16.dp)
    ) {
        items(items = devices) { result ->
            ShowScannedDevices(
                name = result.scanRecord?.deviceName,
                address = result.device.address,
                onDeviceSelected = { action(result.device) },
            )
        }
    }
}


@Composable
fun ShowScannedDevices(
    name: String?,
    address: String,
    onDeviceSelected: () -> Unit,
) {
    Column(
        modifier = Modifier
            .padding(8.dp)
            .clickable { onDeviceSelected() }
    ) {
        Text(
            text = name ?: "No name",
            fontWeight = FontWeight.Bold
        )
        Text(
            text = address,
        )
        Divider()
    }
}
