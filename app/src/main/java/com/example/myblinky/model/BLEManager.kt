package com.example.myblinky.model

import android.annotation.SuppressLint
import android.bluetooth.BluetoothAdapter
import android.bluetooth.BluetoothManager
import android.bluetooth.le.*
import android.content.Context
import android.os.Build
import android.os.ParcelUuid
import androidx.annotation.RequiresApi
import androidx.compose.ui.platform.LocalContext
import androidx.core.content.ContextCompat.getSystemService
import dagger.Binds
import dagger.Provides
import javax.inject.Inject
import javax.inject.Singleton


@SuppressLint("NewApi")
@RequiresApi(Build.VERSION_CODES.M)
class BLEManager @Inject constructor(
) {
    private val bluetoothAdapter: BluetoothAdapter = BluetoothAdapter.getDefaultAdapter()
    val bluetoothLeScanner: BluetoothLeScanner by lazy {
        bluetoothAdapter.bluetoothLeScanner
    }
    private val scanSettings: ScanSettings by lazy {
        ScanSettings.Builder()
            .setCallbackType(ScanSettings.CALLBACK_TYPE_ALL_MATCHES)
            .setLegacy(false)
            .setMatchMode(ScanSettings.MATCH_MODE_AGGRESSIVE)
            .setNumOfMatches(ScanSettings.MATCH_NUM_MAX_ADVERTISEMENT)
            .setPhy(ScanSettings.PHY_LE_ALL_SUPPORTED)
            .setReportDelay(0)
            .setScanMode(ScanSettings.SCAN_MODE_LOW_LATENCY)
            .build()
    }

    private fun scanFilters(): MutableList<ScanFilter> {
        val list: MutableList<ScanFilter> = ArrayList()
        val scanFilterName = ScanFilter.Builder().setDeviceName(null).build()
        list.add(scanFilterName)
        return list
    }

    @SuppressLint("MissingPermission")
    fun startScanning(scanCallback: ScanCallback) {
        bluetoothLeScanner
            .startScan(
                scanFilters(),
                scanSettings,
                scanCallback

            )
    }

}




