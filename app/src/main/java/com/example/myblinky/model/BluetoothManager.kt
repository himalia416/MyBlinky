package com.example.myblinky.model

import android.annotation.SuppressLint
import android.bluetooth.BluetoothAdapter
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.util.Log
import androidx.compose.runtime.MutableState

@SuppressLint("UnrememberedMutableState")
fun checkBluetoothStatus(bluetoothStatus: MutableState<Boolean>): BroadcastReceiver {

    val mReceiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context?, intent: Intent?) {
            if (intent?.action == BluetoothAdapter.ACTION_STATE_CHANGED) {
                when (intent.getIntExtra(
                    BluetoothAdapter.EXTRA_STATE,
                    BluetoothAdapter.ERROR
                )) {
                    BluetoothAdapter.STATE_OFF -> {
                        bluetoothStatus.value = false
                    }
                    BluetoothAdapter.STATE_ON -> {
                        bluetoothStatus.value = true
                    }
                }

            }
        }
    }
    return mReceiver
}