package com.example.callapp

import android.content.Intent
import android.telecom.Call
import android.telecom.InCallService

class MinimalInCallService : InCallService() {
    override fun onCallAdded(call: Call) {
        super.onCallAdded(call)
        CallStateRepository.currentCall = call
        CallStateRepository.service = this
        startActivity(
            Intent(this, InCallActivity::class.java).apply {
                addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP)
            }
        )
    }

    override fun onCallRemoved(call: Call) {
        if (CallStateRepository.currentCall == call) {
            CallStateRepository.currentCall = null
        }
        super.onCallRemoved(call)
    }

    override fun onDestroy() {
        if (CallStateRepository.service == this) {
            CallStateRepository.service = null
        }
        super.onDestroy()
    }
}
