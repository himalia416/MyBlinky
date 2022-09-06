package com.example.myblinky.model

import android.annotation.SuppressLint
import android.bluetooth.BluetoothAdapter
import android.bluetooth.le.BluetoothLeScanner
import android.bluetooth.le.ScanCallback
import android.bluetooth.le.ScanFilter
import android.bluetooth.le.ScanSettings
import android.os.Build
import android.os.ParcelUuid
import androidx.annotation.RequiresApi
import com.example.myblinky.adapter.UUID_SERVICE_DEVICE
import javax.inject.Inject


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
//            .setMatchMode(ScanSettings.MATCH_MODE_AGGRESSIVE)
//            .setNumOfMatches(ScanSettings.MATCH_NUM_MAX_ADVERTISEMENT)
            .setPhy(ScanSettings.PHY_LE_ALL_SUPPORTED)
            .setReportDelay(0)
            .setScanMode(ScanSettings.SCAN_MODE_LOW_LATENCY)
            .build()
    }

    private fun scanFilters(): MutableList<ScanFilter> {
        val list: MutableList<ScanFilter> = ArrayList()
        val scanFilterName =
            ScanFilter.Builder().setServiceUuid(ParcelUuid(UUID_SERVICE_DEVICE)).build()
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




