package com.hydrofish.app

import android.app.Activity
import android.app.Instrumentation
import android.content.Context
import android.provider.Settings
import androidx.compose.ui.test.assertIsOff
import androidx.compose.ui.test.assertIsOn
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onAllNodesWithText
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.test.espresso.intent.Intents
import androidx.test.espresso.intent.Intents.intending
import androidx.test.espresso.intent.matcher.IntentMatchers.hasAction
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.platform.app.InstrumentationRegistry
import com.hydrofish.app.permission.PermissionChecker
import com.hydrofish.app.permission.PermissionResultHandler
import com.hydrofish.app.ui.composables.tabs.NOTIFICATION_KEY
import com.hydrofish.app.ui.composables.tabs.SettingsScreen
import org.junit.After
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class SettingsScreenInstrumentedTest {

    private fun setSharedPreferencesValue(key: String, value: Boolean) {
        val context = InstrumentationRegistry.getInstrumentation().targetContext
        val sharedPreferences = context.getSharedPreferences("MyPrefs", Context.MODE_PRIVATE)
        with(sharedPreferences.edit()) {
            putBoolean(key, value)
            commit()
        }
    }

    @get:Rule
    val composeTestRule = createComposeRule()

    @Before
    fun setup() {
        Intents.init()
    }

    class MockPermissionChecker : PermissionChecker {
        var canScheduleExactAlarmsResult = true

        override fun canScheduleExactAlarms(context: Context): Boolean {
            return canScheduleExactAlarmsResult
        }
    }

    class MockPermissionResultHandler(private val mockResult: Boolean) : PermissionResultHandler {
        override fun handlePermissionResult(isGranted: Boolean): Boolean {
            return mockResult
        }
    }


    @Test()
    fun testScheduleNotification_BothPermissionsNotGranted_SwitchOff() {
        setSharedPreferencesValue(NOTIFICATION_KEY, false)

        intending(hasAction(Settings.ACTION_REQUEST_SCHEDULE_EXACT_ALARM)).respondWith(Instrumentation.ActivityResult(Activity.RESULT_OK, null))

        composeTestRule.setContent {
            SettingsScreen(MockPermissionChecker().apply { canScheduleExactAlarmsResult = false }, MockPermissionResultHandler(false))
        }

        composeTestRule.onNodeWithText("Notification").assertIsOff()

        composeTestRule.onNodeWithText("Notification").performClick()

        composeTestRule.waitUntil(3000) {
            composeTestRule.onAllNodesWithText("Permission Needed").fetchSemanticsNodes()
                .isNotEmpty()
        }

        composeTestRule
            .onNodeWithText("Permission Needed")
            .assertExists()

        composeTestRule
            .onNodeWithText("This app requires notification permission to function properly.")
            .assertExists()

        composeTestRule
            .onNodeWithText("Open Settings")
            .assertExists()

        composeTestRule
            .onNodeWithText("Dismiss")
            .assertExists()
    }

    @Test()
    fun testScheduleNotification_BothPermissionsNotGranted_SwitchOn() {
        setSharedPreferencesValue(NOTIFICATION_KEY, true)

        intending(hasAction(Settings.ACTION_REQUEST_SCHEDULE_EXACT_ALARM)).respondWith(Instrumentation.ActivityResult(Activity.RESULT_OK, null))

        composeTestRule.setContent {
            SettingsScreen(MockPermissionChecker().apply { canScheduleExactAlarmsResult = false }, MockPermissionResultHandler(false))
        }

        composeTestRule.onNodeWithText("Notification").assertIsOff()

        composeTestRule.onNodeWithText("Notification").performClick()

        composeTestRule.waitUntil(3000) {
            composeTestRule.onAllNodesWithText("Permission Needed").fetchSemanticsNodes()
                .isNotEmpty()
        }

        composeTestRule
            .onNodeWithText("Permission Needed")
            .assertExists()

        composeTestRule
            .onNodeWithText("This app requires notification permission to function properly.")
            .assertExists()

        composeTestRule
            .onNodeWithText("Open Settings")
            .assertExists()

        composeTestRule
            .onNodeWithText("Dismiss")
            .assertExists()
    }

    @Test()
    fun testScheduleNotification_ScheduleAlarmPermissionsNotGranted_SwitchOff() {
        setSharedPreferencesValue(NOTIFICATION_KEY, false)

        intending(hasAction(Settings.ACTION_REQUEST_SCHEDULE_EXACT_ALARM)).respondWith(Instrumentation.ActivityResult(Activity.RESULT_OK, null))

        composeTestRule.setContent {
            SettingsScreen(MockPermissionChecker().apply { canScheduleExactAlarmsResult = false }, MockPermissionResultHandler(true))
        }

        composeTestRule.onNodeWithText("Notification").assertIsOff()

        composeTestRule.onNodeWithText("Notification").performClick()
    }

    @Test()
    fun testScheduleNotification_ScheduleAlarmPermissionsNotGranted_SwitchOn() {
        setSharedPreferencesValue(NOTIFICATION_KEY, true)

        intending(hasAction(Settings.ACTION_REQUEST_SCHEDULE_EXACT_ALARM)).respondWith(Instrumentation.ActivityResult(Activity.RESULT_OK, null))

        composeTestRule.setContent {
            SettingsScreen(MockPermissionChecker().apply { canScheduleExactAlarmsResult = false }, MockPermissionResultHandler(true))
        }

        composeTestRule.onNodeWithText("Notification").assertIsOff()

        composeTestRule.onNodeWithText("Notification").performClick()
    }

    @Test()
    fun testScheduleNotification_PostNotificationPermissionsNotGranted_SwitchOff() {
        setSharedPreferencesValue(NOTIFICATION_KEY, false)

        composeTestRule.setContent {
            SettingsScreen(MockPermissionChecker().apply { canScheduleExactAlarmsResult = true }, MockPermissionResultHandler(false))
        }

        composeTestRule.onNodeWithText("Notification").assertIsOff()

        composeTestRule.onNodeWithText("Notification").performClick()

        composeTestRule.waitUntil(3000) {
            composeTestRule.onAllNodesWithText("Permission Needed").fetchSemanticsNodes()
                .isNotEmpty()
        }

        composeTestRule
            .onNodeWithText("Permission Needed")
            .assertExists()

        composeTestRule
            .onNodeWithText("This app requires notification permission to function properly.")
            .assertExists()

        composeTestRule
            .onNodeWithText("Open Settings")
            .assertExists()

        composeTestRule
            .onNodeWithText("Dismiss")
            .assertExists()
    }

    @Test()
    fun testScheduleNotification_PostNotificationPermissionsNotGranted_SwitchOn() {
        setSharedPreferencesValue(NOTIFICATION_KEY, true)

        composeTestRule.setContent {
            SettingsScreen(MockPermissionChecker().apply { canScheduleExactAlarmsResult = true }, MockPermissionResultHandler(false))
        }

        composeTestRule.onNodeWithText("Notification").assertIsOff()

        composeTestRule.onNodeWithText("Notification").performClick()

        composeTestRule.waitUntil(3000) {
            composeTestRule.onAllNodesWithText("Permission Needed").fetchSemanticsNodes()
                .isNotEmpty()
        }

        composeTestRule
            .onNodeWithText("Permission Needed")
            .assertExists()

        composeTestRule
            .onNodeWithText("This app requires notification permission to function properly.")
            .assertExists()

        composeTestRule
            .onNodeWithText("Open Settings")
            .assertExists()

        composeTestRule
            .onNodeWithText("Dismiss")
            .assertExists()
    }

    @Test()
    fun testScheduleNotification_BothPermissionsGranted_SwitchOff() {
        setSharedPreferencesValue(NOTIFICATION_KEY, false)

        composeTestRule.setContent {
            SettingsScreen(MockPermissionChecker().apply { canScheduleExactAlarmsResult = true }, MockPermissionResultHandler(true))
        }

        composeTestRule.onNodeWithText("Notification").assertIsOff()

        composeTestRule.onNodeWithText("Notification").performClick()

        composeTestRule.onNodeWithText("Notification").assertIsOn()
    }

    @Test()
    fun testScheduleNotification_BothPermissionsGranted_SwitchOn() {
        setSharedPreferencesValue(NOTIFICATION_KEY, true)

        composeTestRule.setContent {
            SettingsScreen(MockPermissionChecker().apply { canScheduleExactAlarmsResult = true }, MockPermissionResultHandler(true))
        }

        composeTestRule.onNodeWithText("Notification").assertIsOn()

        composeTestRule.onNodeWithText("Notification").performClick()

        composeTestRule.onNodeWithText("Notification").assertIsOff()
    }

    @After
    fun tearDown() {
        val context = InstrumentationRegistry.getInstrumentation().targetContext
        val sharedPreferences = context.getSharedPreferences("MyPrefs", Context.MODE_PRIVATE)
        sharedPreferences.edit().clear().commit()
        Intents.release()
    }
}
