package com.hydrofish.app

import androidx.compose.foundation.layout.Row
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import com.hydrofish.app.ui.composables.tabs.AddButtons
import com.hydrofish.app.ui.composables.tabs.ReusableDrinkButton
import com.hydrofish.app.utils.IUserSessionRepository
import org.junit.Assert.assertThrows
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.mockito.Mockito

class HomepageIntegrationTest {
    private lateinit var viewModel: HydroFishViewModel

    // used for loading compose components
    @get:Rule
    val rule = createComposeRule()

    @Before
    fun initialization() {
        val mockUserSessionRepository = Mockito.mock(IUserSessionRepository::class.java)
        viewModel = HydroFishViewModel(mockUserSessionRepository)
    }
    @Test
    fun reusableDrinkButtonValueTest() {
        rule.setContent {
            Row() {
                ReusableDrinkButton(0, viewModel);
                ReusableDrinkButton(1000, viewModel);
                ReusableDrinkButton(1300, viewModel);
            }
        }

        assert(viewModel.uiState.value.dailyWaterConsumedML == 0);
        rule.onNodeWithText("0ml").assertIsDisplayed().performClick();
        assert(viewModel.uiState.value.dailyWaterConsumedML == 0);
        rule.onNodeWithText("1.0L").assertIsDisplayed().performClick();
        assert(viewModel.uiState.value.dailyWaterConsumedML == 1000);
        rule.onNodeWithText("1.3L").assertIsDisplayed().performClick();
        assert(viewModel.uiState.value.dailyWaterConsumedML == (1000 + 1300));
    }

    @Test
    fun negativeWaterButtonTest() {
        assertThrows(Exception::class.java) {
            rule.setContent {
                ReusableDrinkButton(-10, viewModel);
            }
        }
    }

    @Test
    fun overTwiceDailyLimitButtonTest() {
        val exceedLimitAmount = viewModel.uiState.value.curDailyMaxWaterConsumedML * 2 + 10;
        assertThrows(Exception::class.java) {
            rule.setContent {
                ReusableDrinkButton(exceedLimitAmount, viewModel);
            }
        }
    }

    @Test
    fun refreshFishTest() {
        rule.setContent {
            ReusableDrinkButton(330, viewModel);
        }

        val initialFishCount = viewModel.uiState.value.animationGroupPositionHandler.getAnimationGroups();
        while (viewModel.uiState.value.dailyWaterConsumedML < viewModel.uiState.value.curDailyMaxWaterConsumedML) {
            rule.onNodeWithText("330ml").performClick();
        }
        // The fishScore is initiated as 1, so the number of fish will remain the same
        val newFishCount = viewModel.uiState.value.animationGroupPositionHandler.getAllFish(1);
        assert(newFishCount == initialFishCount);

        // add water after we exceed the daily limit: should do nothing
        rule.onNodeWithText("330ml").performClick()
        assert(newFishCount == viewModel.uiState.value.animationGroupPositionHandler.getAnimationGroups());
    }

    @Test
    fun currentSetWaterLevelIncreaseTest() {
        rule.setContent {
            AddButtons(hydroFishViewModel = viewModel);
        }

        assert(viewModel.uiState.value.dailyWaterConsumedML == 0);
        rule.onNodeWithText("150ml").assertIsDisplayed().performClick();
        assert(viewModel.uiState.value.dailyWaterConsumedML == 150);
        rule.onNodeWithText("250ml").assertIsDisplayed().performClick();
        assert(viewModel.uiState.value.dailyWaterConsumedML == 150 + 250);
        rule.onNodeWithText("330ml").assertIsDisplayed().performClick();
        assert(viewModel.uiState.value.dailyWaterConsumedML == 150 + 250 + 330);
    }
}