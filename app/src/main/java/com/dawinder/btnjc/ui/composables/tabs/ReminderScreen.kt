package com.dawinder.btnjc.ui.composables.tabs

import android.app.Activity
import android.app.AlarmManager
import android.app.PendingIntent
import android.app.TimePickerDialog
import android.content.Context
import android.content.Intent
import android.os.Build
import android.provider.Settings
import android.util.Log
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.RadioButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import com.dawinder.btnjc.receiver.AlarmReceiver
import java.util.Calendar
import java.util.Locale

/**
 * Composable function that represents the search screen of the application.
 */

/**
 * Composable function that represents the list screen of the application.
 */

const val W_TIME_KEY = "w_time"
const val S_TIME_KEY = "s_time"
@Composable
fun ReminderScreen() {
// Fetching local context
    val mContext = LocalContext.current

    // Declaring and initializing a calendar
    val mCalendar = Calendar.getInstance()
    val mHour = mCalendar[Calendar.HOUR_OF_DAY]
    val mMinute = mCalendar[Calendar.MINUTE]

    // Initialize SharedPreferences
    val sharedPreferences = remember {
        mContext.getSharedPreferences("MyPrefs", Context.MODE_PRIVATE)
    }

    // Retrieve values from SharedPreferences with default values
    val defaultWTimeValue = sharedPreferences.getString(W_TIME_KEY, "") ?: ""
    val wTime = rememberSaveable { mutableStateOf(defaultWTimeValue) }

    val defaultSTimeValue = sharedPreferences.getString(S_TIME_KEY, "") ?: ""
    val sTime = rememberSaveable { mutableStateOf(defaultSTimeValue) }


    // Value for storing time as a string
//    val wTime = rememberSaveable  { mutableStateOf("") }

    val wTimePickerDialog = TimePickerDialog(
        mContext,
        {_, mHour : Int, mMinute: Int ->
            wTime.value = "$mHour:$mMinute"
            sharedPreferences.edit().putString(W_TIME_KEY, wTime.value).apply()
        }, mHour, mMinute, false
    )
    val wformattedTime = if (wTime.value.isNotBlank()) {
        val components = wTime.value.split(":")
        if (components.size == 2) {
            val hour = components[0].toIntOrNull() ?: 0
            val minute = components[1].toIntOrNull() ?: 0
            String.format(Locale.getDefault(), "%02d:%02d", hour, minute)
        } else {
            ""
        }
    } else {
        ""
    }

//    val sTime = rememberSaveable { mutableStateOf("") }

    val sTimePickerDialog = TimePickerDialog(
        mContext,
        {_, mHour : Int, mMinute: Int ->
            sTime.value = "$mHour:$mMinute"
            sharedPreferences.edit().putString(S_TIME_KEY, sTime.value).apply()
        }, mHour, mMinute, false
    )
    val sformattedTime = if (sTime.value.isNotBlank()) {
        val components = sTime.value.split(":")
        if (components.size == 2) {
            val hour = components[0].toIntOrNull() ?: 0
            val minute = components[1].toIntOrNull() ?: 0
            String.format(Locale.getDefault(), "%02d:%02d", hour, minute)
        } else {
            ""
        }
    } else {
        ""
    }

    val timeIntervals = listOf("00:15", "00:30", "01:00", "01:30", "02:00", "02:30")
    var selectedInterval by remember { mutableStateOf(timeIntervals[3]) } //default 01:30
    var selectedIntervalValue by remember { mutableStateOf(15) }    //default 15 seconds

    val timeIntervalToMinutes = mapOf(
        "00:15" to 2,
        "00:30" to 5,
        "01:00" to 10,
        "01:30" to 15,
        "02:00" to 25,
        "02:30" to 30
    )


    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        Text("Set Your Schedule", style = MaterialTheme.typography.headlineMedium)
        Row(verticalAlignment = Alignment.CenterVertically) {
            OutlinedTextField(
                value = wformattedTime,
                onValueChange = { /* Do nothing since it's read-only */ },
                label = { Text("Wake up time") },
                readOnly = true,
                modifier = Modifier.weight(1f) // Take all available space
            )
            Spacer(modifier = Modifier.width(16.dp))
            Button(
                onClick = { wTimePickerDialog.show() },
                colors = ButtonDefaults.buttonColors(containerColor = Color(0XFF0F9D58))
            ) {
                Text(text = "Choose", color = Color.White)
            }
        }
        Spacer(modifier = Modifier.height(16.dp))
        Row(verticalAlignment = Alignment.CenterVertically) {
            OutlinedTextField(
                value = sformattedTime,
                onValueChange = { /* Do nothing since it's read-only */ },
                label = { Text("Sleeping time") },
                readOnly = true,
                modifier = Modifier.weight(1f) // Take all available space
            )
            Spacer(modifier = Modifier.width(16.dp))
            Button(
                onClick = { sTimePickerDialog.show() },
                colors = ButtonDefaults.buttonColors(containerColor = Color(0XFF0F9D58))
            ) {
                Text(text = "Choose", color = Color.White)
            }
        }
        Spacer(modifier = Modifier.height(32.dp))
        // Time interval section
        Text("Time Interval", style = MaterialTheme.typography.headlineSmall)

        timeIntervals.forEach { interval ->
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.fillMaxWidth().padding(vertical = 8.dp)
            ) {
                RadioButton(
                    selected = selectedInterval == interval,
                    onClick = {
                        selectedInterval = interval
                        selectedIntervalValue = timeIntervalToMinutes[interval] ?: 0
                    }
                )
                Text(
                    text = interval,
                    style = MaterialTheme.typography.bodyLarge,
                    modifier = Modifier.padding(start = 16.dp)
                )
            }
        }
    }

    val context = LocalContext.current
    val activity = context as? Activity

    Button(onClick = {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            if (!(context.getSystemService(Context.ALARM_SERVICE) as AlarmManager).canScheduleExactAlarms()) {
                // app doesn't have the permission, request for permission.
                val intent = Intent(Settings.ACTION_REQUEST_SCHEDULE_EXACT_ALARM)
                context.startActivity(intent)
            } else {
                // app has permission, schedule the alarm.
                scheduleAlarm(context,selectedIntervalValue)
            }
        }
    }) {
        Text("Schedule Notification")
    }
}

fun scheduleAlarm(context: Context, interval: Int) {
    val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager
    val intent = Intent(context, AlarmReceiver::class.java)
    val pendingIntent = PendingIntent.getBroadcast(
        context,
        0,
        intent,
        PendingIntent.FLAG_IMMUTABLE or PendingIntent.FLAG_UPDATE_CURRENT
    )

    val triggerTime = System.currentTimeMillis() + (interval * 1000) //set the time to trigger the alarm
    alarmManager.setExactAndAllowWhileIdle(
        AlarmManager.RTC_WAKEUP,
        triggerTime,
        pendingIntent
    )
}