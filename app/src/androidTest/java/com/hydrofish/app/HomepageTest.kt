package com.hydrofish.app

import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.lifecycle.viewmodel.compose.viewModel
import com.hydrofish.app.ui.composables.tabs.AddProgessBar
import com.hydrofish.app.ui.composables.tabs.HomeScreen
import com.hydrofish.app.ui.composables.tabs.ReusableDrinkButton
import org.junit.Rule
import org.junit.Test

class HomepageTest {

    @get:Rule
    val rule = createComposeRule()

    @Test
    fun test_ReusableDrinkButton() {
        rule.setContent {
            ReusableDrinkButton(330f);
        }

        rule.onNodeWithText("330.0ml").assertIsDisplayed()
    }
    
    @Test
    fun test_AddProgessBar() {
        rule.setContent {
            AddProgessBar(waterConsumed = 330f)
        }

        // not sure what test to add yet
    }

    @Test
    fun test_HomePage() {
        rule.setContent {
            HomeScreen()
        }

        rule.onNodeWithText("150.0ml").assertIsDisplayed()
        rule.onNodeWithText("250.0ml").assertIsDisplayed()
        rule.onNodeWithText("330.0ml").assertIsDisplayed()
    }
}