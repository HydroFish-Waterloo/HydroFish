package com.hydrofish.app

import android.util.Log
import org.junit.Assert
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
        Assert.assertThrows(Exception::class.java) {
            viewModel.increaseWaterLevel(-100);
        }
    }

    // test no longer works - require a new test
//    @Test
//    fun testFishAddition() {
//        // one ml away
//        viewModel.increaseWaterLevel(viewModel.uiState.value.curDailyMaxWaterConsumedML - 1);
//        val initialFishCount = viewModel.uiState.value.fishTypeList.count();
//        viewModel.increaseWaterLevel(1);
//        val newFishCount = viewModel.uiState.value.fishTypeList.count();
//        assert(newFishCount == initialFishCount + 1);
//
//        // once we exceed the limit, no additional fish should be added
//        viewModel.increaseWaterLevel(1);
//        assert(newFishCount == viewModel.uiState.value.fishTypeList.count());
//    }
}