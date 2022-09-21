package com.example.myblinky.viewmodel

import android.annotation.SuppressLint
import android.bluetooth.le.ScanCallback
import android.bluetooth.le.ScanResult
import android.content.Context
import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.myblinky.model.BLEManager
import dagger.Provides
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
 class ScanningViewModel @Inject constructor(
    private val bleManager: BLEManager,
    @ApplicationContext context: Context
) : ViewModel() {
    private val SCAN_PERIOD: Long = 10000
    private var scanning = false
    private var viewModelJob: Job? = null

    val devices = bleManager.devices


    fun startScanning(filterByUuid: Boolean) {
        if (!scanning) {
            // Stops scanning after a pre-defined scan period.
            viewModelScope
                .launch (Dispatchers.Default) {
                    delay(SCAN_PERIOD)
                    println("filter scan stopped 11")
                    stopBleScan()
                }
                .also { viewModelJob = it }
        } else {
            viewModelJob?.cancel()
            stopBleScan()
        }
        scanning = true
        bleManager.startScanning(filterByUuid)
    }

    fun stopBleScan() {
        scanning = false
        println("filter scan stopped 12")
        bleManager.stopScan()
    }

}






