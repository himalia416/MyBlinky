package com.example.myblinky.view

import android.bluetooth.le.ScanResult
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
import com.example.myblinky.viewmodel.ScanningViewModel
import no.nordicsemi.android.common.navigation.NavigationManager
import no.nordicsemi.android.common.permission.RequireBluetooth
import no.nordicsemi.android.common.theme.view.NordicAppBar

@RequiresApi(Build.VERSION_CODES.M)
@Composable
fun ScanningView(navController: NavigationManager) {
    val viewModel = hiltViewModel<ScanningViewModel>()
    val filterByUuid = remember {
        mutableStateOf(true)
    }

    Column {
        NordicAppBar(
            text = stringResource(id = R.string.app_name),
            actions = {
                FilterDropDownMenu(
                    filterByUuid = filterByUuid,
                    viewModel = viewModel
                )
            }
        )

        Column {
            RequireBluetooth {
                ScannedDevices(navController)
                LaunchedEffect(navController) {
                    viewModel.startScanning(filterByUuid.value)
                }
            }
        }
    }
}

@Composable
private fun FilterDropDownMenu(
    filterByUuid: MutableState<Boolean>,
    viewModel: ScanningViewModel
) {
    var expanded by remember { mutableStateOf(false) }
    val filterOptions = listOf(
        stringResource(id = R.string.filter_menu_lbs),
        stringResource(id = R.string.filter_menu_all_devices),
    )
    var (selectedOption: String, onOptionSelected: (String) -> Unit) = remember {
        mutableStateOf(
            filterOptions[0]
        )
    }

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
            filterOptions.forEach { label ->
                Row(
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier
                        .fillMaxWidth(),
                ) {
                    Text(text = label)
                    Checkbox(
                        checked = selectedOption == label,
                        onCheckedChange =
                        {
                            selectedOption = label
                            onOptionSelected(label)
                            filterByUuid.value = selectedOption == filterOptions[0]
                            viewModel.startScanning(filterByUuid.value)
                            expanded = false
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
    val devices: State<List<ScanResult>> = viewModel.devices.collectAsState()

    LazyColumn(
        verticalArrangement = Arrangement.spacedBy(8.dp),
        contentPadding = PaddingValues(16.dp)
    ) {
        items(items = devices.value) { result ->
            ShowScannedDevices(navController, result = result)
        }
    }
}


@Composable
fun ShowScannedDevices(
    navigationManager: NavigationManager,
    result: ScanResult,
) {
    val viewModel = hiltViewModel<ScanningViewModel>()
    Column(
        modifier = Modifier
            .clickable {
                viewModel.stopBleScan()
                navigationManager.navigateTo(
                    destination = ConnectView,
                    ConnectViewParams(result.device)
                )
            }
            .padding(8.dp)
    ) {
        Text(
            text = result.device!!.name ?: "No name",
            fontWeight = FontWeight.Bold
        )
        Text(
            text = result.device!!.address,
        )
        Divider()
    }
}
