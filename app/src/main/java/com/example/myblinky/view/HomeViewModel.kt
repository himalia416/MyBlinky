package com.example.myblinky.view

import android.os.Build
import androidx.lifecycle.ViewModel
import com.example.myblinky.model.BLEManager
import com.example.myblinky.permissions.PermissionManager
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class HomeViewModel @Inject constructor(
    private val permissionManager: PermissionManager,
    private val bleManager: BLEManager
) : ViewModel() {

    val permissionGranted = permissionManager.permissionsGranted

    fun startScanning() = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
        bleManager.startScanning()
    } else {
        TODO("VERSION.SDK_INT < M")
    }
}