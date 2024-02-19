package com.hydrofish.app

import android.app.Activity
import android.app.Instrumentation
import android.content.Context
import android.provider.Settings
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.assertIsSelected
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onAllNodesWithText
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.test.espresso.intent.Intents
import androidx.test.espresso.intent.matcher.IntentMatchers
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.platform.app.InstrumentationRegistry
import com.hydrofish.app.permission.PermissionChecker
import com.hydrofish.app.permission.PermissionResultHandler
import com.hydrofish.app.ui.composables.tabs.NOTIFICATION_KEY
import com.hydrofish.app.ui.composables.tabs.ReminderScreen
import com.hydrofish.app.ui.composables.tabs.S_TIME_KEY
import com.hydrofish.app.ui.composables.tabs.W_TIME_KEY
import org.junit.After
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith


@RunWith(AndroidJUnit4::class)
class ReminderScreenInstrumentedTest {

    @get:Rule
    val composeTestRule = createComposeRule()

    private fun setSharedPreferencesBoolValue(key: String, value: Boolean) {
        val context = InstrumentationRegistry.getInstrumentation().targetContext
        val sharedPreferences = context.getSharedPreferences("MyPrefs", Context.MODE_PRIVATE)
        with(sharedPreferences.edit()) {
            putBoolean(key, value)
            commit()
        }
    }

    private fun setSharedPreferencesStringValue(key: String, value: String) {
        val context = InstrumentationRegistry.getInstrumentation().targetContext
        val sharedPreferences = context.getSharedPreferences("MyPrefs", Context.MODE_PRIVATE)
        with(sharedPreferences.edit()) {
            putString(key, value)
            commit()
        }
    }

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

    @Test
    fun testScheduleNotification_PermissionNotGranted() {
        Intents.intending(IntentMatchers.hasAction(Settings.ACTION_REQUEST_SCHEDULE_EXACT_ALARM))
            .respondWith(Instrumentation.ActivityResult(Activity.RESULT_OK, null))

        composeTestRule.setContent {
            ReminderScreen(
                SettingsScreenInstrumentedTest.MockPermissionChecker()
                    .apply { canScheduleExactAlarmsResult = false })
        }

        composeTestRule
            .onNodeWithText("Schedule Notification")
            .performClick()

    }

    @Test
    fun testScheduleNotification_FunctionalityOff() {
        setSharedPreferencesBoolValue(NOTIFICATION_KEY, false)
        setSharedPreferencesStringValue(W_TIME_KEY, "7:0")
        setSharedPreferencesStringValue(S_TIME_KEY, "8:0")

        composeTestRule.setContent {
            ReminderScreen(
                SettingsScreenInstrumentedTest.MockPermissionChecker()
                    .apply { canScheduleExactAlarmsResult = true })
        }

        composeTestRule.waitUntil(timeoutMillis = 1000) {
            composeTestRule.onAllNodesWithText("01:00").fetchSemanticsNodes()
                .isNotEmpty()
        }

        composeTestRule
            .onNodeWithTag("01:00")
            .assertIsSelected()

        composeTestRule
            .onNodeWithTag("00:15")
            .performClick()

        composeTestRule
            .onNodeWithTag("00:15")
            .assertIsSelected()

        composeTestRule
            .onNodeWithText("Schedule Notification")
            .performClick()

        composeTestRule.waitUntil(timeoutMillis = 3000) {
            composeTestRule.onAllNodesWithText("Please Enable Notification Settings").fetchSemanticsNodes()
                .isNotEmpty()
        }

        composeTestRule
            .onNodeWithText("Please Enable Notification Settings")
            .assertIsDisplayed()
    }

    @Test
    fun testScheduleNotification_FunctionalityOn_LargerInterval() {
        setSharedPreferencesBoolValue(NOTIFICATION_KEY, true)
        setSharedPreferencesStringValue(W_TIME_KEY, "7:0")
        setSharedPreferencesStringValue(S_TIME_KEY, "8:0")

        composeTestRule.setContent {
            ReminderScreen(
                SettingsScreenInstrumentedTest.MockPermissionChecker()
                    .apply { canScheduleExactAlarmsResult = true })
        }

        composeTestRule.waitUntil(timeoutMillis = 1000) {
            composeTestRule.onAllNodesWithText("01:00").fetchSemanticsNodes()
                .isNotEmpty()
        }

        composeTestRule
            .onNodeWithTag("01:00")
            .assertIsSelected()

        composeTestRule
            .onNodeWithTag("02:00")
            .performClick()

        composeTestRule
            .onNodeWithTag("02:00")
            .assertIsSelected()

        composeTestRule
            .onNodeWithText("Schedule Notification")
            .performClick()

        composeTestRule.waitUntil(timeoutMillis = 3000) {
            composeTestRule.onAllNodesWithText("Interval exceeds the time difference").fetchSemanticsNodes()
                .isNotEmpty()
        }

        composeTestRule
            .onNodeWithText("Interval exceeds the time difference")
            .assertIsDisplayed()
    }

    @After
    fun tearDown() {
        val context = InstrumentationRegistry.getInstrumentation().targetContext
        val sharedPreferences = context.getSharedPreferences("MyPrefs", Context.MODE_PRIVATE)
        sharedPreferences.edit().clear().commit()
        Intents.release()
    }
}
