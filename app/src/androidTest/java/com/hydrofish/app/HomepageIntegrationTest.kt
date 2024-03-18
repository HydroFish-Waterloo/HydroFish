//package com.hydrofish.app
//
//import android.util.Log
//import androidx.compose.foundation.layout.Row
//import androidx.compose.ui.test.assertIsDisplayed
//import androidx.compose.ui.test.junit4.createComposeRule
//import androidx.compose.ui.test.onNodeWithText
//import androidx.compose.ui.test.performClick
//import androidx.lifecycle.viewmodel.compose.viewModel
//import com.hydrofish.app.ui.composables.tabs.AddButtons
//import com.hydrofish.app.ui.composables.tabs.AddProgessBar
//import com.hydrofish.app.ui.composables.tabs.HomeScreen
//import com.hydrofish.app.ui.composables.tabs.ReusableDrinkButton
//import org.junit.Assert.assertThrows
//import org.junit.Before
//import org.junit.Rule
//import org.junit.Test
//
//class HomepageIntegrationTest {
//    private lateinit var viewModel: HydroFishViewModel
//
//    // used for loading compose components
//    @get:Rule
//    val rule = createComposeRule()
//
//    @Before
//    fun initialization() {
//        viewModel = HydroFishViewModel()
//    }
//    @Test
//    fun reusableDrinkButtonValueTest() {
//        rule.setContent {
//            Row() {
//                ReusableDrinkButton(0, viewModel);
//                ReusableDrinkButton(1000, viewModel);
//                ReusableDrinkButton(1300, viewModel);
//            }
//        }
//
//        assert(viewModel.uiState.value.dailyWaterConsumedML == 0);
//        rule.onNodeWithText("0ml").assertIsDisplayed().performClick();
//        assert(viewModel.uiState.value.dailyWaterConsumedML == 0);
//        rule.onNodeWithText("1.0L").assertIsDisplayed().performClick();
//        assert(viewModel.uiState.value.dailyWaterConsumedML == 1000);
//        rule.onNodeWithText("1.3L").assertIsDisplayed().performClick();
//        assert(viewModel.uiState.value.dailyWaterConsumedML == (1000 + 1300));
//    }
//
//    @Test
//    fun negativeWaterButtonTest() {
//        assertThrows(Exception::class.java) {
//            rule.setContent {
//                ReusableDrinkButton(-10, viewModel);
//            }
//        }
//    }
//
//    @Test
//    fun overTwiceDailyLimitButtonTest() {
//        val exceedLimitAmount = viewModel.uiState.value.curDailyMaxWaterConsumedML * 2 + 10;
//        assertThrows(Exception::class.java) {
//            rule.setContent {
//                ReusableDrinkButton(exceedLimitAmount, viewModel);
//            }
//        }
//    }
//
//    @Test
//    fun addFishTest() {
//        rule.setContent {
//            ReusableDrinkButton(330, viewModel);
//        }
//
//        val initialFishCount = viewModel.uiState.value.fishTypeList.count();
//        while (viewModel.uiState.value.dailyWaterConsumedML < viewModel.uiState.value.curDailyMaxWaterConsumedML) {
//            rule.onNodeWithText("330ml").performClick();
//        }
//        val newFishCount = viewModel.uiState.value.fishTypeList.count();
//        assert(newFishCount == initialFishCount + 1);
//
//        // add water after we exceed the daily limit: should do nothing
//        rule.onNodeWithText("330ml").performClick()
//        assert(newFishCount == viewModel.uiState.value.fishTypeList.count());
//    }
//
//    @Test
//    fun currentSetWaterLevelIncreaseTest() {
//        rule.setContent {
//            AddButtons(hydroFishViewModel = viewModel);
//        }
//
//        assert(viewModel.uiState.value.dailyWaterConsumedML == 0);
//        rule.onNodeWithText("150ml").assertIsDisplayed().performClick();
//        assert(viewModel.uiState.value.dailyWaterConsumedML == 150);
//        rule.onNodeWithText("250ml").assertIsDisplayed().performClick();
//        assert(viewModel.uiState.value.dailyWaterConsumedML == 150 + 250);
//        rule.onNodeWithText("330ml").assertIsDisplayed().performClick();
//        assert(viewModel.uiState.value.dailyWaterConsumedML == 150 + 250 + 330);
//    }
//}