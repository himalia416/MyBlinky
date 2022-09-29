package com.example.myblinky.scanner.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.myblinky.scanner.model.ScanningManager
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
 class ScanningViewModel @Inject constructor(
    private val scanningManager: ScanningManager,
) : ViewModel() {
    private val SCAN_PERIOD: Long = 10000
    val scanning =  MutableStateFlow(false)

    //    var scanning = false
    private var viewModelJob: Job? = null

    val devices = scanningManager.devices


    fun startScanning(filterByUuid: Boolean) {
        if (!scanning.value) {
            // Stops scanning after a pre-defined scan period.
            viewModelScope
                .launch (Dispatchers.Default) {
                    delay(SCAN_PERIOD)
                    stopBleScan()
                }
                .also { viewModelJob = it }
        } else {
            viewModelJob?.cancel()
            stopBleScan()
        }
        scanning.value = true
        scanningManager.startScanning(filterByUuid)
    }

    fun stopBleScan() {
        scanning.value = false
        scanningManager.stopScan()
    }

}






