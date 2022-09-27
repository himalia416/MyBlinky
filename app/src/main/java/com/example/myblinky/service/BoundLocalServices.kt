package com.example.myblinky.service

import android.bluetooth.BluetoothDevice
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.content.ServiceConnection
import android.os.IBinder
import androidx.compose.runtime.*
import androidx.compose.ui.platform.LocalContext

@Composable
fun rememberBoundLocalService(device: BluetoothDevice): BlinkyAPI? {
    val context: Context = LocalContext.current
    var boundService: BlinkyAPI? by remember(context) { mutableStateOf(null) }
    val serviceConnection: ServiceConnection = remember(context) {
        object : ServiceConnection {
            override fun onServiceConnected(className: ComponentName, service: IBinder) {
                boundService = service as BlinkyAPI
            }

            override fun onServiceDisconnected(arg0: ComponentName) {
                boundService = null
            }
        }
    }
    DisposableEffect(context, serviceConnection) {
        val intent = Intent(context, BluetoothLeService::class.java)
        intent.putExtra("ADDRESS", device.address)
        context.startService(intent)
        context.bindService(intent, serviceConnection, Context.BIND_AUTO_CREATE)
        onDispose {
            boundService = null
            context.unbindService(serviceConnection)
        }
    }
    return boundService
}