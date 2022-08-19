package com.example.myblinky.permissions

import android.Manifest
import android.app.Activity
import android.content.pm.PackageManager
import android.os.Build
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

object PermissionManager {
    // Request Code to be used with permissions request
    val requestCode = 1

    // Tag used in our console logs
    private const val TAG = "PermissionManager"
    // Indicate whether all permissions have been granted
    private var _permissionsGranted = MutableStateFlow<Boolean>(false)
    // Public access to permissions
    var permissionsGranted: StateFlow<Boolean> = _permissionsGranted

    // Permissions required & Permissions we will prompt the user for this lifecycle
    private val permissionsRequired: MutableList<String> = buildPermissionsList()
    private val permissionsToAsk: MutableList<String> = arrayListOf()

    /**
     * Add all permissions your app requires
     * @return MutableList<String> of permissions
     */
    private fun buildPermissionsList(): MutableList<String> {
        val permissionsList = mutableListOf<String>()

        if (Build.VERSION.SDK_INT <= 28) {
            permissionsList.add(Manifest.permission.BLUETOOTH)
            permissionsList.add(Manifest.permission.BLUETOOTH_ADMIN)
            permissionsList.add(Manifest.permission.ACCESS_COARSE_LOCATION)
        } else if (Build.VERSION.SDK_INT <= 30) {
            permissionsList.add(Manifest.permission.BLUETOOTH)
            permissionsList.add(Manifest.permission.BLUETOOTH_ADMIN)
            permissionsList.add(Manifest.permission.ACCESS_FINE_LOCATION)
        } else if (Build.VERSION.SDK_INT >= 31) {
            permissionsList.add(Manifest.permission.BLUETOOTH_SCAN)
            permissionsList.add(Manifest.permission.ACCESS_FINE_LOCATION)
        }

        return permissionsList
    }

    /**
     * Ask user permissions for all required permissions within this object
     * onRequestPermissionResult will be called once all permissions have been granted or dismissed
     * @param activity activity
     */
    fun askPermissions(activity: Activity) {

        // Create permissionsToAsk list based off if permission has been granted already
        permissionsRequired.forEach { permission ->
            if (ContextCompat.checkSelfPermission(
                    activity.applicationContext,
                    permission
                ) != PackageManager.PERMISSION_GRANTED
            ) {
                // Add to permissionsToAsk list
                permissionsToAsk.add(permission)
            }
        }

        // Request each permission
        if (permissionsToAsk.size > 0) {
            ActivityCompat.requestPermissions(
                activity,
                permissionsToAsk.toTypedArray(),
                requestCode
            )
        } else {
            _permissionsGranted.value = true
        }

    }

    /**
     * Receives callback ONCE with array of permissions and results
     * Called from overridden OnRequestPermissionResult from Activity
     */
    fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        // Make sure the request code matches
        if (this.requestCode == requestCode) {

            // Check if any of the permissions were not granted
            _permissionsGranted.value = grantResults.any { it == PackageManager.PERMISSION_DENIED }
        }
    }
}