package com.hydrofish.app.permission

import android.app.AlarmManager
import android.content.Context
import android.os.Build

interface PermissionChecker {
    fun canScheduleExactAlarms(context: Context): Boolean
}

class RealPermissionChecker : PermissionChecker {
    override fun canScheduleExactAlarms(context: Context): Boolean {
        val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            return alarmManager.canScheduleExactAlarms()
        }
        return true
    }
}
