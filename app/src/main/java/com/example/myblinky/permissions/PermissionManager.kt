package com.example.myblinky.permissions

import android.Manifest
import android.os.Build
import android.widget.Toast
import androidx.compose.foundation.layout.*
import androidx.compose.material3.Button
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.example.myblinky.R
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.rememberMultiplePermissionsState

@OptIn(ExperimentalPermissionsApi::class)

@Composable
fun RequireScanPermission(
    content: @Composable () -> Unit,
) {
    val context = LocalContext.current
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
        true -> content()
        false -> {
            NoLocationPermission(
                modifier = Modifier.fillMaxSize()
            ) {
                multiplePermissionsState.launchMultiplePermissionRequest()
            }
        }
    }
}

@Composable
private fun NoLocationPermission(
    modifier: Modifier = Modifier,
    onRequestPermission: () -> Unit,
) {
    Surface {
        Column(
            modifier = modifier,
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                stringResource(id = R.string.location_permission_info),
                textAlign = TextAlign.Center
            )
            Button(
                modifier = Modifier.padding(vertical = 24.dp),
                onClick = onRequestPermission
            ) {
                Text("Request permission")
            }
        }
    }
}