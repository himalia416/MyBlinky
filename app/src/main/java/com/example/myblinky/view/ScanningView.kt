package com.example.myblinky.view

import android.bluetooth.BluetoothDevice
import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.FilterList
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.myblinky.ConnectViewParams
import com.example.myblinky.MainActivity.Companion.ConnectView
import com.example.myblinky.R
import com.example.myblinky.data.FilterOption
import com.example.myblinky.viewmodel.FilterDropDownViewModel
import com.example.myblinky.viewmodel.ScanningViewModel
import no.nordicsemi.android.common.navigation.NavigationManager
import no.nordicsemi.android.common.permission.RequireBluetooth
import no.nordicsemi.android.common.theme.view.NordicAppBar

@RequiresApi(Build.VERSION_CODES.M)
@Composable
fun ScanningView(navController: NavigationManager) {
    val viewModel = hiltViewModel<ScanningViewModel>()
    val filterViewModel = hiltViewModel<FilterDropDownViewModel>()
    val filter by remember { filterViewModel.filterOptions }

    Column {
        NordicAppBar(
            text = stringResource(id = R.string.app_name),
            actions = {
                FilterDropDownMenu(
                    filterOptions = listOf(filter),
                    onFilterChanged = {  isSelected ->
                        filterViewModel.setFilterSelected(isSelected)
                    }
                )
            }
        )
        Column {
            RequireBluetooth {
                ScannedDevices(navController)
                DisposableEffect(filter) {
                    viewModel.startScanning(filter.isSelected)
                    onDispose { viewModel.stopBleScan() }
                }
            }
        }
    }
}

@Composable
private fun FilterDropDownMenu(
    filterOptions: List<FilterOption>,
    onFilterChanged: ( Boolean) -> Unit,
) {
    var expanded by remember { mutableStateOf(false) }

    Box {
        IconButton(
            onClick = { expanded = !expanded }
        ) {
            Icon(
                imageVector = Icons.Filled.FilterList,
                contentDescription = stringResource(id = R.string.menu_filter)
            )
        }
        DropdownMenu(
            expanded = expanded,
            onDismissRequest = { expanded = false }
        ) {
            filterOptions.forEach { filterOption ->
                Row(
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier
                        .clickable {
                            expanded = false
                            onFilterChanged(!filterOption.isSelected)
                        }
                        .padding(horizontal = 8.dp),
                ) {
                    Text(text = filterOption.filterName)
                    Checkbox(
                        checked = filterOption.isSelected,
                        onCheckedChange =
                        {
                            expanded = false
                            onFilterChanged(it)
                        }
                    )
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
            ConnectViewParams(it)
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
