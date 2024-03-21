package com.hydrofish.app

import com.hydrofish.app.utils.IUserSessionRepository
import io.mockk.mockk
import org.junit.Before
import org.junit.Rule

class HomepageUnitTest {
    private lateinit var viewModel: HydroFishViewModel

    @get:Rule

    private lateinit var mockUserSessionRepository: IUserSessionRepository

    @Before
    fun setUp() {
        mockUserSessionRepository = mockk(relaxed = true)
        viewModel = HydroFishViewModel(mockUserSessionRepository)
    }

//    @Test
//    fun testNegativeWaterConsumed() {
//        Assert.assertThrows(Exception::class.java) {
//            viewModel.increaseWaterLevel(-100);
//        }
//    }


}