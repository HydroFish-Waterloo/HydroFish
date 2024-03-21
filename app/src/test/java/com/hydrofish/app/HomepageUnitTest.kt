package com.hydrofish.app

import com.hydrofish.app.api.ApiService
import com.hydrofish.app.api.PostSuccess
import com.hydrofish.app.utils.IUserSessionRepository
import io.mockk.every
import io.mockk.mockk
import org.junit.Assert
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.mockito.Mockito.mock
import org.mockito.Mockito.`when`
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.lang.Exception
import java.time.LocalDate

class HomepageUnitTest {
    private lateinit var viewModel: HydroFishViewModel

    @get:Rule

    private lateinit var mockUserSessionRepository: IUserSessionRepository

    lateinit var apiService: ApiService

    @Before
    fun setUp() {
        mockUserSessionRepository = mockk(relaxed = true)
        every { mockUserSessionRepository.getWaterIntake() } returns Pair(500, "2023-03-20")
        every {mockUserSessionRepository.getScore()} returns 1
        viewModel = HydroFishViewModel(mockUserSessionRepository)
    }
    @Test
    fun testInitState() {
        Assert.assertThrows(Exception::class.java) {
            viewModel.increaseWaterLevel(-100);
        }
    }


    @Test
    fun testNegativeWaterConsumed() {
        Assert.assertThrows(Exception::class.java) {
            viewModel.increaseWaterLevel(-100);
        }
    }


}