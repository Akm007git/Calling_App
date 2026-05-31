package com.example.callapp

import android.telecom.Call
import android.telecom.InCallService

object CallStateRepository {
    var currentCall: Call? = null
    var service: InCallService? = null
}
