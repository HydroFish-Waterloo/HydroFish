import androidx.compose.ui.test.*
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.lifecycle.MutableLiveData
import androidx.navigation.NavHostController
import com.hydrofish.app.ui.composables.tabs.HistoryScreen
import com.hydrofish.app.utils.IUserSessionRepository
import org.junit.Rule
import org.junit.Test
import org.mockito.Mockito.mock
import org.mockito.Mockito.`when`
import org.mockito.MockitoAnnotations

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
}
