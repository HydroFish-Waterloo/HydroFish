package com.hydrofish.app

import com.hydrofish.app.utils.IUserSessionRepository
import io.mockk.every
import io.mockk.just
import io.mockk.mockk
import io.mockk.runs
import io.mockk.verify
import junit.framework.TestCase.assertEquals
import org.junit.Assert
import org.junit.Before
import org.junit.Test
import java.lang.Exception
import java.time.LocalDate


class HomepageUnitTest {
    private lateinit var viewModel: HydroFishViewModel
    private lateinit var mockUserSessionRepository: IUserSessionRepository
    private var mockDailyWaterConsumed: Int = 500
    private var mockFishScore: Int = 1
    private var mockDateString: String = "2023-03-20"
    @Before
    fun setUp() {
        mockUserSessionRepository = mockk(relaxed = true)
        every { mockUserSessionRepository.getWaterIntake() } returns
            Pair(mockDailyWaterConsumed, mockDateString)
        every { mockUserSessionRepository.getScore() } returns mockFishScore
        viewModel = HydroFishViewModel(mockUserSessionRepository)
    }

    @Test
    fun testInitState() {
        // InitWaterBar
        assertEquals(viewModel.uiState.value.dailyWaterConsumedML, mockDailyWaterConsumed)

        // InitScore
        assertEquals(viewModel.uiState.value.fishScore, mockFishScore)
    }

    @Test
    fun testNegativeWaterConsumed() {
        Assert.assertThrows(Exception::class.java) {
            viewModel.increaseWaterLevel(-100);
        }
    }

    @Test
    fun testIncreaseWaterLevel() {
        assertEquals(viewModel.uiState.value.dailyWaterConsumedML, mockDailyWaterConsumed);
        viewModel.increaseWaterLevel(0)
        assertEquals(viewModel.uiState.value.dailyWaterConsumedML, mockDailyWaterConsumed);
        viewModel.increaseWaterLevel(1000)
        assertEquals(viewModel.uiState.value.dailyWaterConsumedML, mockDailyWaterConsumed + 1000);
        viewModel.increaseWaterLevel(1300)
        assertEquals(viewModel.uiState.value.dailyWaterConsumedML, mockDailyWaterConsumed + 1000 + 1300);
    }

    @Test
    fun testUpdateFishModels() {
        viewModel.updateFishModels()
        assertEquals(viewModel.uiState.value.fishScore, mockFishScore)

        every { mockUserSessionRepository.getScore() } returns mockFishScore + 1
        viewModel.updateFishModels()
        assertEquals(viewModel.uiState.value.fishScore, mockFishScore + 1)
    }

    @Test
    fun testLevelUpSuccess() {
        // Mocking behavior for getScore
        every { mockUserSessionRepository.getScore() } returns 5

        // Mocking behavior for updateScore
        every { mockUserSessionRepository.updateScore(any()) } just runs

        // Mocking behavior for syncScore callback
        every { mockUserSessionRepository.syncScore(any()) } answers {
            // Simulate onSuccess callback
            val callback = args[0] as IUserSessionRepository.SyncScoreCallback
            callback.onSuccess(6)
        }


        // Call the function under test
        viewModel.levelUp()

        // Verify interactions
        verify { mockUserSessionRepository.getScore() }
        verify { mockUserSessionRepository.updateScore(6) }
        verify { mockUserSessionRepository.syncScore(any()) }
        assertEquals( viewModel.uiState.value.fishScore,6 )
    }

    @Test
    fun testLevelUpFailure() {
        // Mocking behavior for getScore
        every { mockUserSessionRepository.getScore() } returns 5

        // Mocking behavior for updateScore
        every { mockUserSessionRepository.updateScore(any()) } just runs

        // Mocking behavior for syncScore callback
        every { mockUserSessionRepository.syncScore(any()) } answers {
            // Simulate onFailure callback
            val callback = args[0] as IUserSessionRepository.SyncScoreCallback
            callback.onFailure(-1)
        }

        // Call the function under test
        viewModel.levelUp()

        // Verify interactions
        verify { mockUserSessionRepository.getScore() }
        verify { mockUserSessionRepository.updateScore(6) }
        verify { mockUserSessionRepository.syncScore(any()) }

        // Verify that fishScore is not updated (remains 5) due to failure
        assertEquals(1, viewModel.uiState.value.fishScore)
    }

    @Test
    fun testCheckResetWaterIntake() {
        // check if dates are different
        assertEquals(viewModel.uiState.value.dailyWaterConsumedML, mockDailyWaterConsumed)
        viewModel.checkResetWaterIntake()
        assertEquals(viewModel.uiState.value.dailyWaterConsumedML, 0)

        // check if dates are the same
        viewModel.increaseWaterLevel(mockDailyWaterConsumed)
        assertEquals(viewModel.uiState.value.dailyWaterConsumedML, mockDailyWaterConsumed)
        every { mockUserSessionRepository.getWaterIntake() } returns
                Pair(mockDailyWaterConsumed, LocalDate.now().toString())
        viewModel.checkResetWaterIntake()
        assertEquals(viewModel.uiState.value.dailyWaterConsumedML, mockDailyWaterConsumed)
    }
}