package com.example.myblinky.view

import android.annotation.SuppressLint
import android.os.Build
import android.util.Log
import androidx.annotation.RequiresApi
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.example.myblinky.R
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers

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
            val context = LocalContext.current
            val scope = CoroutineScope(Dispatchers.IO)
            Button(

                onClick = {
                    if (permissionGranted) {
                        viewModel.startScanning()
                    }
                }
            ) {
                Text(
                    text = "Scan",
                    style = MaterialTheme.typography.subtitle1
                )
            }
        }
    }
}

