package com.hydrofish.app

import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithText
import androidx.lifecycle.MutableLiveData
import androidx.navigation.NavHostController
import com.hydrofish.app.ui.composables.tabs.AchievementsScreen
import com.hydrofish.app.utils.IUserSessionRepository
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.mockito.Mockito
import org.mockito.Mockito.`when`

class AchievementScreenIntegrationTest {

    private lateinit var mockUserSessionRepository: IUserSessionRepository

    @get:Rule
    val composeTestRule = createComposeRule()

    @Before
    fun setup() {
        mockUserSessionRepository = Mockito.mock(IUserSessionRepository::class.java)
        Mockito.`when`(mockUserSessionRepository.isLoggedIn).thenReturn(MutableLiveData(false))
        Mockito.`when`(mockUserSessionRepository.scoreLiveData).thenReturn(MutableLiveData(1))
    }
    @Test
    fun testAchievementScreen_WhenUserLoggedIn() {

        val navController = Mockito.mock(NavHostController::class.java)

        `when`(mockUserSessionRepository.isLoggedIn).thenReturn(MutableLiveData(true))

        composeTestRule.setContent {
            AchievementsScreen(userSessionRepository = mockUserSessionRepository, navController = navController)
        }
        composeTestRule.onNodeWithText("Achievements").assertIsDisplayed()
    }

    @Test
    fun testAchievementScreen_WhenUserNotLoggedIn() {

        val navController = Mockito.mock(NavHostController::class.java)

        `when`(mockUserSessionRepository.isLoggedIn).thenReturn(MutableLiveData(false))

        composeTestRule.setContent {
            AchievementsScreen(userSessionRepository = mockUserSessionRepository, navController = navController)
        }

        composeTestRule.onNodeWithText("Please log in to access this page").assertIsDisplayed()
    }
}
