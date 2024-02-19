package com.hydrofish.app.ui.composables.tabs

import android.app.AlarmManager
import android.app.PendingIntent
import android.app.TimePickerDialog
import android.content.Context
import android.content.Intent
import android.provider.Settings
import android.util.Log
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.RadioButton
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.unit.dp
import com.hydrofish.app.permission.PermissionChecker
import com.hydrofish.app.receiver.AlarmReceiver
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch
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
const val INTERVAL_KEY = "interval"
@Composable
fun ReminderScreen(permissionChecker: PermissionChecker) {
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

    val notificationValue = sharedPreferences.getBoolean(NOTIFICATION_KEY, false)
    val notificationCheck by remember { mutableStateOf(notificationValue) }

    // Retrieve values from SharedPreferences with default values
    val defaultWTimeValue = sharedPreferences.getString(W_TIME_KEY, "7:0") ?: ""
    val wTime = rememberSaveable { mutableStateOf(defaultWTimeValue) }

    val defaultSTimeValue = sharedPreferences.getString(S_TIME_KEY, "22:0") ?: ""
    val sTime = rememberSaveable { mutableStateOf(defaultSTimeValue) }

    val timeIntervalToSeconds = listOf(900, 1800, 3600, 5400, 7200)
    val timeIntervals = listOf("00:15", "00:30", "01:00", "01:30", "02:00")
    // get the time interval from the shared preferences
    val defaultIntervalIndex = sharedPreferences.getInt(INTERVAL_KEY, 2)
    var selectedIntervalIndex by remember { mutableStateOf(defaultIntervalIndex) }
    var selectedIntervalValue by remember {mutableStateOf(timeIntervalToSeconds[selectedIntervalIndex])}    //default 15 seconds

    // variable for Snackbar
    val snackbarHostState = remember { SnackbarHostState() }
    val coroutineScope = rememberCoroutineScope()
    var showSnackbar by remember { mutableStateOf(false) }
    var backgroundColor by remember { mutableStateOf(Color(0xFF323232)) }
    fun triggerSnackbar(scope: CoroutineScope, message: String) {
        scope.launch {
            snackbarHostState.showSnackbar(message)
        }
    }

    // Value for storing time as a string
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

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        Text("Set Your Schedule", style = MaterialTheme.typography.headlineMedium)

        Spacer(modifier = Modifier.height(16.dp))

        Button(
            onClick = {
                if (!permissionChecker.canScheduleExactAlarms(mContext)){
                        // app doesn't have the permission, request for permission.
                        val intent = Intent(Settings.ACTION_REQUEST_SCHEDULE_EXACT_ALARM)
                        mContext.startActivity(intent)
                } else {
                    if (!notificationCheck) {
                        showSnackbar = true
                        if (showSnackbar) {
                            backgroundColor = Color(0xFFFFB74D)
                            triggerSnackbar(coroutineScope, "Please Enable Notification Settings")
                            showSnackbar = false
                        }
                        //Toast.makeText(mContext, "Please Enable Notification Settings", Toast.LENGTH_LONG).show()
                    }
                    else {
//                        val currentWTimeValue = sharedPreferences.getString(W_TIME_KEY, "7:0") ?: ""
//                        val currentSTimeValue = sharedPreferences.getString(S_TIME_KEY, "22:0") ?: ""
//
//                        Log.e("WTime", currentWTimeValue)
//                        Log.e("STime", currentSTimeValue)

                        // app has permission, schedule the alarm.
                        Log.e("WTime", wTime.value)
                        Log.e("STime", sTime.value)
                        val wakeUpSeconds = timeStringToSeconds(wTime.value)
                        val sleepSeconds = timeStringToSeconds(sTime.value)

                        val diffSeconds = if (sleepSeconds >= wakeUpSeconds) {
                            sleepSeconds - wakeUpSeconds
                        } else {
                            24 * 3600 - wakeUpSeconds + sleepSeconds
                        }
                        if (diffSeconds > selectedIntervalValue) {
                            showSnackbar = true
                            if (showSnackbar) {
                                backgroundColor = Color(0xFF81C784)
                                triggerSnackbar(coroutineScope, "Notification Scheduled Successfully")
                                showSnackbar = false
                            }
                            try{
                                scheduleAlarm(mContext,selectedIntervalValue,wakeUpSeconds,diffSeconds)
                            }catch (e: Exception){
                                Log.e("Alarm", "Error: ${e.message}")
                            }
                        } else {
                            showSnackbar = true
                            if (showSnackbar) {
                                backgroundColor = Color(0xFFE57373)
                                triggerSnackbar(coroutineScope, "Interval exceeds the time difference")
                                showSnackbar = false
                            }
                        }
                    }
                }
            },
        ) {
            Text(text = "Schedule Notification", color = Color.White)
        }

        Spacer(modifier = Modifier.height(16.dp))

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

        Column {
            timeIntervals.forEachIndexed { index, interval ->
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    RadioButton(
                        selected = selectedIntervalIndex == index,
                        onClick = {
                            selectedIntervalIndex = index
                            selectedIntervalValue = timeIntervalToSeconds[selectedIntervalIndex]
                            sharedPreferences.edit().putInt(INTERVAL_KEY, selectedIntervalIndex).apply()
                        },
                        modifier = Modifier.testTag(interval)
                    )
                    Text(
                        text = interval,
                        style = MaterialTheme.typography.bodyLarge,
                        modifier = Modifier.padding(start = 16.dp)
                    )
                }
                if (index < timeIntervals.lastIndex) {
                    Spacer(modifier = Modifier.height(4.dp))
                }
            }
        }
        Spacer(modifier = Modifier.height(16.dp))
        SnackbarHost(
            hostState = snackbarHostState,
            modifier = Modifier
                .align(Alignment.CenterHorizontally),
            snackbar = { data ->
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(50))
                        .background(backgroundColor)
                        .padding(horizontal = 16.dp, vertical = 8.dp)
                ) {
                    Text(
                        text = data.visuals.message,
                        color = Color.White,
                        modifier = Modifier.align(Alignment.Center)
                    )
                }
            }
        )
    }
}

fun timeStringToSeconds(time: String): Int {
    if (!time.matches(Regex("\\b([0-9]|1[0-9]|2[0-3]):([0-5]?[0-9])\\b"))) {
        throw IllegalArgumentException("Invalid time format. Please use HH:mm or H:m format.")
    }

    try {
        val (hours, minutes) = time.split(":").map { it.toInt() }
        return hours * 3600 + minutes * 60
    } catch (e: NumberFormatException) {
        throw IllegalArgumentException("Time contains non-numeric characters.")
    }
}

fun scheduleAlarm(context: Context, interval: Int, wakeUpSeconds: Int, diffSeconds: Int) {

    val alarmNum = (diffSeconds / interval)
    for (i in 1..alarmNum){
        Log.i("Alarm", "Alarm $i")
        val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager
        val intent = Intent(context, AlarmReceiver::class.java)
        val pendingIntent = PendingIntent.getBroadcast(
            context,
            i,
            intent,
            PendingIntent.FLAG_IMMUTABLE or PendingIntent.FLAG_UPDATE_CURRENT
        )

        val currentTime = System.currentTimeMillis()
        val todayStartMillis = currentTime - currentTime % (24 * 3600 * 1000)
        val wakeUpTodayMillis = todayStartMillis + wakeUpSeconds * 1000
        val triggerTime = System.currentTimeMillis() + (i * interval * 1000L / 300)

//            val triggerTime = System.currentTimeMillis() + i * interval * 1000L/300
//            val triggerTime = System.currentTimeMillis() + (interval * 1000/300) //set the time to trigger the alarm
        alarmManager.setExactAndAllowWhileIdle(
            AlarmManager.RTC_WAKEUP,
            triggerTime,
            pendingIntent
        )
    }
}