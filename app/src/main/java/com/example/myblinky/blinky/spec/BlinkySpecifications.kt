package com.example.myblinky.blinky.spec

import java.util.*

class BlinkySpecifications {
    companion object{
        /** Nordic Blinky Service UUID. */
        val UUID_SERVICE_DEVICE: UUID? = UUID.fromString("00001523-1212-efde-1523-785feabcd123")

        /** LED characteristic UUID. */
        val UUID_LED_CHAR: UUID by lazy { UUID.fromString("00001525-1212-efde-1523-785feabcd123") }

        /** BUTTON characteristic UUID. */
        val UUID_BUTTON_CHAR: UUID  by lazy { UUID.fromString("00001524-1212-efde-1523-785feabcd123") }

        /** Update Notification UUID. */
        val UUID_UPDATE_NOTIFICATION_DESCRIPTOR_CHAR: UUID  by lazy { UUID.fromString("00002902-0000-1000-8000-00805f9b34fb") }
    }
}