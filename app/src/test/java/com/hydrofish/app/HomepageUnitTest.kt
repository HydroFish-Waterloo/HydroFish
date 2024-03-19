package com.hydrofish.app

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


}