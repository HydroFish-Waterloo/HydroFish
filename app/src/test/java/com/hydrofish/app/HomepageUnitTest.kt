package com.hydrofish.app

import android.util.Log
import org.junit.Before
import org.junit.Test

class HomepageUnitTest {
    private lateinit var viewModel: HydroFishViewModel

    @Before
    fun setUp() {
        viewModel = HydroFishViewModel()
    }

    @Test
    fun testNegativeWaterConsumed() {
        viewModel.increaseWaterLevel(-100);

        // should not accept negative values
        assert(viewModel.uiState.value.dailyWaterConsumedML == 0);
    }

    @Test
    fun testFishAddition() {
        // one ml away
        viewModel.increaseWaterLevel(viewModel.uiState.value.curDailyMaxWaterConsumedML - 1);
        val initialFishCount = viewModel.uiState.value.fishTypeList.count();
        viewModel.increaseWaterLevel(1);
        assert(viewModel.uiState.value.fishTypeList.count() == initialFishCount + 1);
    }
}