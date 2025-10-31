package com.vaxcare.unifiedhub.core.data.device

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.os.BatteryManager.EXTRA_LEVEL
import android.os.BatteryManager.EXTRA_PLUGGED
import android.os.BatteryManager.EXTRA_SCALE
import android.os.PowerManager
import androidx.activity.ComponentActivity
import androidx.lifecycle.DefaultLifecycleObserver
import androidx.lifecycle.LifecycleOwner
import com.vaxcare.unifiedhub.core.data.datasource.BatteryStatusProvider
import com.vaxcare.unifiedhub.core.model.BatteryStatus
import javax.inject.Inject

class BatteryMonitor @Inject constructor(
    private val batteryStatusProvider: BatteryStatusProvider,
) : DefaultLifecycleObserver {
    companion object {
        private const val UNKNOWN = -1
    }

    private lateinit var context: Context

    private lateinit var batteryReceiver: BroadcastReceiver
    private var powerManager: PowerManager? = null

    fun initialize(activity: ComponentActivity) {
        batteryReceiver = object : BroadcastReceiver() {
            override fun onReceive(context: Context?, intent: Intent?) {
                if (powerManager == null) {
                    powerManager = context?.getSystemService(Context.POWER_SERVICE) as? PowerManager
                }
                intent?.handleBatteryIntent()
            }
        }
        activity.lifecycle.addObserver(this)
        context = activity
    }

    override fun onResume(owner: LifecycleOwner) {
        super.onResume(owner)
        val filter = IntentFilter(Intent.ACTION_BATTERY_CHANGED)
        context.registerReceiver(batteryReceiver, filter)
    }

    override fun onPause(owner: LifecycleOwner) {
        super.onPause(owner)
        context.unregisterReceiver(batteryReceiver)
    }

    private fun Intent.handleBatteryIntent() {
        val plugged = getIntExtra(EXTRA_PLUGGED, UNKNOWN)
        val level = getIntExtra(EXTRA_LEVEL, UNKNOWN)
        val scale = getIntExtra(EXTRA_SCALE, UNKNOWN)
        val isPowerSavingMode = powerManager?.isPowerSaveMode ?: false

        batteryStatusProvider.update(
            BatteryStatus(
                percent = (level * 100 / scale.toFloat()).toInt(),
                isCharging = plugged != 0,
                isPowerSaveModeEnabled = isPowerSavingMode
            )
        )
    }
}
