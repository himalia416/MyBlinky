package com.example.myblinky.permissions

import android.Manifest
import android.os.Build
import android.widget.Toast
import androidx.compose.foundation.layout.*
import androidx.compose.material.Button
import androidx.compose.material.Surface
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.rememberMultiplePermissionsState

@OptIn(ExperimentalPermissionsApi::class)

@Composable
fun requireScanPermission(): Boolean {
    val context = LocalContext.current
    val isLocationPermissionGranted: Boolean

    val permissionsList = mutableListOf<String>()

    if (Build.VERSION.SDK_INT in 26..30) {
        permissionsList.add(Manifest.permission.ACCESS_FINE_LOCATION)
    } else if (Build.VERSION.SDK_INT >= 31) {
        permissionsList.add(Manifest.permission.BLUETOOTH_SCAN)
        permissionsList.add(Manifest.permission.BLUETOOTH_CONNECT)
    }

    val multiplePermissionsState =
        rememberMultiplePermissionsState(permissions = permissionsList) { permissionStateMap ->
            if (!permissionStateMap.containsValue(false)) {
                Toast.makeText(context, "Permissions Granted", Toast.LENGTH_SHORT).show()
            } else {
                Toast.makeText(context, "Permissions Denied", Toast.LENGTH_SHORT).show()
            }
        }
    when (multiplePermissionsState.allPermissionsGranted) {
        true -> isLocationPermissionGranted = true
        false -> {
            isLocationPermissionGranted = false
            Surface {
                Column(
                    modifier = Modifier.fillMaxSize().fillMaxHeight(),
                    verticalArrangement = Arrangement.Center,
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        "Location permission for this application is important in order to scan for BLE devices. Please grant the permission!",
                        textAlign = TextAlign.Center
                    )
                    Button(
                        modifier = Modifier.padding(vertical = 24.dp),
                        onClick = { multiplePermissionsState.launchMultiplePermissionRequest() }) {
                        Text("Request permission")
                    }
                }
            }
        }
    }
    return isLocationPermissionGranted
}