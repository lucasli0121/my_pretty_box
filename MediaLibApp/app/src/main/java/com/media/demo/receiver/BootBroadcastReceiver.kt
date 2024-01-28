package com.media.demo.receiver

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent

class BootBroadcastReceiver: BroadcastReceiver() {
    private val action: String = "android.intent.action.BOOT_COMPLETED"
    override fun onReceive(context: Context?, intent: Intent?) {
        if (intent != null && intent.action.equals(action, ignoreCase = true)) {
            var intentNew = context?.packageManager?.getLaunchIntentForPackage(context.packageName)
            intentNew?.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            context?.startActivity(intentNew)
        }
    }
}