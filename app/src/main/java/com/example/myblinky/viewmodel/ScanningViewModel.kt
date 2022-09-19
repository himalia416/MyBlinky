package com.example.myblinky.viewmodel

import android.annotation.SuppressLint
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.myblinky.model.BLEManager
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
 class ScanningViewModel @Inject constructor(
    private val bleManager: BLEManager
) : ViewModel() {
    private val SCAN_PERIOD: Long = 10000
    private var scanning = false
    private var viewModelJob: Job? = null

    val devices = bleManager.devices

    @SuppressLint("StaticFieldLeak")
    fun startScanning(filterByUuid: Boolean) {
        println("filter value in hiltviewmodel: $filterByUuid")
        if (!scanning) {
            // Stops scanning after a pre-defined scan period.
            viewModelScope
                .launch (Dispatchers.Main) {
                    delay(SCAN_PERIOD)
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
        bleManager.stopScan()
    }

}






