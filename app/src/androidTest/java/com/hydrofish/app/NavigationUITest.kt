package com.hydrofish.app

import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onAllNodesWithText
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.lifecycle.MutableLiveData
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.hydrofish.app.nav.NavItem
import com.hydrofish.app.ui.composables.tabs.AchievementsScreen
import com.hydrofish.app.ui.composables.tabs.HistoryScreen
import com.hydrofish.app.ui.composables.tabs.SettingsScreen
import com.hydrofish.app.ui.composables.unauthenticatedGraph
import com.hydrofish.app.utils.IUserSessionRepository
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.Mockito

@RunWith(AndroidJUnit4::class)
class NavigationUITest {

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
    fun settingsScreenNavigatesToLogin() {
        composeTestRule.setContent {
            val navController = rememberNavController()

            NavHost(navController = navController, startDestination = NavItem.Settings.path) {
                composable(NavItem.Settings.path) {
                    SettingsScreen(
                        permissionChecker = SettingsScreenInstrumentedTest.MockPermissionChecker()
                            .apply { canScheduleExactAlarmsResult = false },
                        permissionResultHandler = SettingsScreenInstrumentedTest.MockPermissionResultHandler(false),
                        navController,
                        mockUserSessionRepository
                    )
                }
                unauthenticatedGraph(navController, mockUserSessionRepository)
            }
        }

        composeTestRule.onNodeWithText("Login").performClick()

        composeTestRule.waitForIdle()

        composeTestRule.waitUntil(1000) {
            composeTestRule.onAllNodesWithText("Hydro Fish").fetchSemanticsNodes()
                .isNotEmpty()
        }

        composeTestRule.onNodeWithText("Register").performClick()

        composeTestRule.waitForIdle()

        composeTestRule.waitUntil(1000) {
            composeTestRule.onAllNodesWithText("Registration").fetchSemanticsNodes()
                .isNotEmpty()
        }

        composeTestRule.onNodeWithText("Back to Login").performClick()

        composeTestRule.waitForIdle()

        composeTestRule.waitUntil(1000) {
            composeTestRule.onAllNodesWithText("Hydro Fish").fetchSemanticsNodes()
                .isNotEmpty()
        }

        composeTestRule.onNodeWithText("Back").performClick()

        composeTestRule.waitForIdle()

        composeTestRule.waitUntil(1000) {
            composeTestRule.onAllNodesWithText("Settings").fetchSemanticsNodes()
                .isNotEmpty()
        }

    }

    @Test
    fun historyScreenNavigatesToLogin() {
        composeTestRule.setContent {
            val navController = rememberNavController()

            NavHost(navController = navController, startDestination = NavItem.Settings.path) {
                composable(NavItem.Settings.path) {
                    HistoryScreen(
                        mockUserSessionRepository,
                        navController,
                    )
                }
                unauthenticatedGraph(navController, mockUserSessionRepository)
            }
        }

        composeTestRule.onNodeWithText("Go to login page").performClick()

        composeTestRule.waitForIdle()

        composeTestRule.waitUntil(1000) {
            composeTestRule.onAllNodesWithText("Hydro Fish").fetchSemanticsNodes()
                .isNotEmpty()
        }

        composeTestRule.onNodeWithText("Register").performClick()

        composeTestRule.waitForIdle()

        composeTestRule.waitUntil(1000) {
            composeTestRule.onAllNodesWithText("Registration").fetchSemanticsNodes()
                .isNotEmpty()
        }

        composeTestRule.onNodeWithText("Back to Login").performClick()

        composeTestRule.waitForIdle()

        composeTestRule.waitUntil(1000) {
            composeTestRule.onAllNodesWithText("Hydro Fish").fetchSemanticsNodes()
                .isNotEmpty()
        }

        composeTestRule.onNodeWithText("Back").performClick()

        composeTestRule.waitForIdle()

        composeTestRule.waitUntil(1000) {
            composeTestRule.onAllNodesWithText("Please log in to access this page").fetchSemanticsNodes()
                .isNotEmpty()
        }

    }

    @Test
    fun achievementScreenNavigatesToLogin() {
        composeTestRule.setContent {
            val navController = rememberNavController()

            NavHost(navController = navController, startDestination = NavItem.Settings.path) {
                composable(NavItem.Settings.path) {
                    AchievementsScreen(
                        mockUserSessionRepository,
                        navController,
                    )
                }
                unauthenticatedGraph(navController, mockUserSessionRepository)
            }
        }

        composeTestRule.onNodeWithText("Go to login page").performClick()

        composeTestRule.waitForIdle()

        composeTestRule.waitUntil(1000) {
            composeTestRule.onAllNodesWithText("Hydro Fish").fetchSemanticsNodes()
                .isNotEmpty()
        }

        composeTestRule.onNodeWithText("Register").performClick()

        composeTestRule.waitForIdle()

        composeTestRule.waitUntil(1000) {
            composeTestRule.onAllNodesWithText("Registration").fetchSemanticsNodes()
                .isNotEmpty()
        }

        composeTestRule.onNodeWithText("Back to Login").performClick()

        composeTestRule.waitForIdle()

        composeTestRule.waitUntil(1000) {
            composeTestRule.onAllNodesWithText("Hydro Fish").fetchSemanticsNodes()
                .isNotEmpty()
        }

        composeTestRule.onNodeWithText("Back").performClick()

        composeTestRule.waitForIdle()

        composeTestRule.waitUntil(1000) {
            composeTestRule.onAllNodesWithText("Please log in to access this page").fetchSemanticsNodes()
                .isNotEmpty()
        }

    }
}

