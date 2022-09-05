package com.example.myblinky.callback

import android.bluetooth.BluetoothDevice
import android.bluetooth.BluetoothGatt
import android.bluetooth.BluetoothGattCharacteristic.FORMAT_UINT8
import android.provider.ContactsContract
import com.example.myblinky.adapter.BlinkyAPI

abstract class LedDataCallback: BlinkyAPI {
    private val STATE_OFF = 0x00
    private val STATE_ON = 0x01


}