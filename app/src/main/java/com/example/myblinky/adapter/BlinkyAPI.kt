package com.example.myblinky.adapter

import android.annotation.SuppressLint
import android.bluetooth.BluetoothDevice
import android.bluetooth.BluetoothGatt
import android.bluetooth.BluetoothGattCharacteristic
import android.content.Context
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import kotlinx.coroutines.flow.Flow
import java.util.*

interface BlinkyAPI {

    fun disconnect()

    suspend fun turnLed(device: BluetoothDevice, on: Boolean)
//    fun onButtonStateChanged(device: BluetoothDevice, buttonState:Boolean)

    fun getButtonState(): LiveData<Boolean>
}