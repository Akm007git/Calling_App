package com.example.callapp

import android.telecom.Call
import android.telecom.CallScreeningService

class MinimalCallScreeningService : CallScreeningService() {
    override fun onScreenCall(callDetails: Call.Details) {
        respondToCall(callDetails, CallResponse.Builder().build())
    }
}
