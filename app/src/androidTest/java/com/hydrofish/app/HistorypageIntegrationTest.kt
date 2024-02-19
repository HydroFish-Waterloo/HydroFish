package com.hydrofish.app

import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.Text
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.compose.ui.unit.dp
import com.hydrofish.app.ui.composables.tabs.HistoryScreen
import com.hydrofish.app.ui.composables.tabs.HydrationEntry
import com.hydrofish.app.ui.theme.HydroFishTheme
import org.junit.Rule
import org.junit.Test
import java.text.SimpleDateFormat
import java.util.*
import androidx.compose.foundation.layout.padding
import androidx.compose.ui.Modifier
import androidx.compose.foundation.lazy.items

class HistoryScreenIntegrationTest {

    @get:Rule
    val composeTestRule = createComposeRule()

    @Test
    fun historyScreenDisplaysCorrectData() {
        // Set up test data
        val dateFormat = SimpleDateFormat("yyyy-MM-dd", Locale.ENGLISH)
        val hydrationData = listOf(
            HydrationEntry("2022-01-01", 200),
            HydrationEntry("2022-01-02", 150),
            HydrationEntry("2022-01-03", 300),
            )

        // Set content of the HistoryScreen with the test data
        composeTestRule.setContent {
            HydroFishTheme {
                HistoryScreen()
            }
        }

        // Verify that the "Last 3 Days" button is displayed
        composeTestRule.onNodeWithText("Last 3 Days").assertIsDisplayed()

        // Verify that the "Last 7 Days" button is displayed
        composeTestRule.onNodeWithText("Last 7 Days").assertIsDisplayed()

        // Verify that the "Last Month" button is displayed
        composeTestRule.onNodeWithText("Last Month").assertIsDisplayed()

        // Click on the "Last 3 Days" button
        composeTestRule.onNodeWithText("Last 3 Days").performClick()

        // Click on the "Last 7 Days" button
        composeTestRule.onNodeWithText("Last 7 Days").performClick()

        // Click on the "Last Month" button
        composeTestRule.onNodeWithText("Last Month").performClick()
    }
    class LazyColumnTest {

        @get:Rule
        val composeTestRule = createComposeRule()

        @Test
        fun testLazyColumn() {
            val data = listOf("Item 1", "Item 2", "Item 3")
            composeTestRule.setContent {
                LazyColumn {
                    items(data) { item ->
                        Text(item, Modifier.padding(16.dp))
                    }
                }
            }

            composeTestRule.onNodeWithText("Item 1").assertIsDisplayed()
            composeTestRule.onNodeWithText("Item 2").assertIsDisplayed()
            composeTestRule.onNodeWithText("Item 3").assertIsDisplayed()
        }
    }
}
