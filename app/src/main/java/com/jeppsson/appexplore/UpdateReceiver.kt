package com.jeppsson.appexplore

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent

class UpdateReceiver : BroadcastReceiver() {

    override fun onReceive(context: Context, intent: Intent) {
        val action = intent.action

        if (Intent.ACTION_PACKAGE_FULLY_REMOVED == action
                || Intent.ACTION_PACKAGE_ADDED == action) {
            PackageScannerService.enqueueWork(context, intent)
        }
    }
}
