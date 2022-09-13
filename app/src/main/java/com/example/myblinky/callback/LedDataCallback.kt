package com.example.myblinky.callback

import com.example.myblinky.adapter.BlinkyAPI

abstract class LedDataCallback: BlinkyAPI {
    private val STATE_OFF = 0x00
    private val STATE_ON = 0x01


}