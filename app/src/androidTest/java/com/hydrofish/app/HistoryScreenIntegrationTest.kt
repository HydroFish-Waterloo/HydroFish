import androidx.compose.ui.test.*
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.lifecycle.MutableLiveData
import androidx.navigation.NavHostController
import com.hydrofish.app.api.DataResponse
import com.hydrofish.app.api.HydrationEntry
import com.hydrofish.app.ui.composables.tabs.HistoryScreen
import com.hydrofish.app.utils.IUserSessionRepository
import org.junit.Rule
import org.junit.Test
import org.mockito.Mockito.mock
import org.mockito.Mockito.`when`
import org.mockito.MockitoAnnotations
import java.util.Date


class HistoryScreenTest {

    @get:Rule
    val composeTestRule = createComposeRule()

    @Test
    fun testHistoryScreen_WhenUserLoggedIn() {
        MockitoAnnotations.initMocks(this)

        val mockUserSessionRepository = mock(IUserSessionRepository::class.java)
        val navController = mock(NavHostController::class.java)

        `when`(mockUserSessionRepository.isLoggedIn).thenReturn(MutableLiveData(true))

        composeTestRule.setContent {
            HistoryScreen(userSessionRepository = mockUserSessionRepository, navController = navController)
        }
        composeTestRule.onNodeWithText("History").assertIsDisplayed()
    }

    @Test
    fun testHistoryScreen_WhenUserNotLoggedIn() {
        MockitoAnnotations.initMocks(this)

        val mockUserSessionRepository = mock(IUserSessionRepository::class.java)
        val navController = mock(NavHostController::class.java)

        `when`(mockUserSessionRepository.isLoggedIn).thenReturn(MutableLiveData(false))

        composeTestRule.setContent {
            HistoryScreen(userSessionRepository = mockUserSessionRepository, navController = navController)
        }

        composeTestRule.onNodeWithText("Please log in to access this page").assertIsDisplayed()
    }

    @Test
    fun testHistoryScreen_UI() {
        MockitoAnnotations.initMocks(this)

        // Mock dependencies
        val mockUserSessionRepository = mock(IUserSessionRepository::class.java)
        val navController = mock(NavHostController::class.java)
        val currentDate = Date()
        val mockHydrationData = listOf(
            HydrationEntry(currentDate, 500),
            HydrationEntry(currentDate, 600),
            HydrationEntry(currentDate, 700)
        )
        val mockResponse = DataResponse(mockHydrationData)

        `when`(mockUserSessionRepository.isLoggedIn).thenReturn(MutableLiveData(true))
        `when`(mockUserSessionRepository.getToken()).thenReturn("mockToken")
        //`when`(historyApiService.getHydrationData(anyMap())).thenReturn(mockResponse)

        composeTestRule.setContent {
            HistoryScreen(userSessionRepository = mockUserSessionRepository, navController = navController)
        }

        composeTestRule.onNodeWithText("History").assertIsDisplayed()
        composeTestRule.onNodeWithText("Last 3 Days").assertIsDisplayed()
        composeTestRule.onNodeWithText("Last 7 Days").assertIsDisplayed()
        composeTestRule.onNodeWithText("Last Month").assertIsDisplayed()
    }
}

