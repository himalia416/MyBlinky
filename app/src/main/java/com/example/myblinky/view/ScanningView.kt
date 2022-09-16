package com.example.myblinky.view

import android.bluetooth.le.ScanResult
import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowDropUp
import androidx.compose.material.icons.filled.FilterList
import androidx.compose.material3.Divider
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
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
    var dropDownMenuExpanded by remember {
        mutableStateOf(false)
    }

    Column {
        NordicAppBar(
            text = stringResource(id = R.string.app_name),
            actions = {
                addFilterOption()
            }
        )
        Column(modifier = Modifier.padding(16.dp)) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(top = 16.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                RequireBluetooth {
                    ScannedDevices(navController)
                    LaunchedEffect(navController) {
                        viewModel.startScanning()
                    }
                }
            }
        }
    }
}


@Composable
fun addFilterOption() {
    var expanded by remember { mutableStateOf(false) }
    val suggestions = listOf("Only devices advertising LBS UUID", "All devices")
    val icon = if (expanded)
        Icons.Default.ArrowDropUp
    else
        Icons.Default.FilterList


    Column() {
        Icon(icon, "contentDescription",
            Modifier.clickable { expanded = !expanded })
        DropdownMenu(
            expanded = expanded,
            onDismissRequest = { expanded = false},
        ) {

        }
    }
}

@Composable
fun ScannedDevices(navController: NavigationManager) {
    val viewModel = hiltViewModel<ScanningViewModel>()
    val devices: State<List<ScanResult>> = viewModel.mLeDevices.collectAsState()

    LazyColumn(verticalArrangement = Arrangement.spacedBy(8.dp)) {
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
