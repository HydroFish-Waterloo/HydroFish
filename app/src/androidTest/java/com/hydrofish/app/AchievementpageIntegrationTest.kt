import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.test.*
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.lifecycle.MutableLiveData
import androidx.navigation.NavHostController
import com.hydrofish.app.ui.composables.tabs.AchievementsScreen
import com.hydrofish.app.ui.composables.tabs.AchievementsViewModel
import com.hydrofish.app.ui.composables.tabs.HistoryScreen
import com.hydrofish.app.utils.IUserSessionRepository
import org.junit.Rule
import org.junit.Test
import org.mockito.Mockito.mock
import org.mockito.Mockito.`when`
import org.mockito.MockitoAnnotations

class AchievementpageIntegrationTest {

    @get:Rule
    val composeTestRule = createComposeRule()
    //val score by achievementsViewModel.scoreLiveData.observeAsState(1)
    @Test
    fun testAchievementScreen_WhenUserLoggedIn() {
        MockitoAnnotations.initMocks(this)

        val mockUserSessionRepository = mock(IUserSessionRepository::class.java)
        val navController = mock(NavHostController::class.java)

        `when`(mockUserSessionRepository.isLoggedIn).thenReturn(MutableLiveData(true))

        composeTestRule.setContent {
            AchievementsScreen(userSessionRepository = mockUserSessionRepository, navController = navController)
        }
        composeTestRule.onNodeWithText("Achievements").assertIsDisplayed()
    }

    @Test
    fun testAchievementScreen_WhenUserNotLoggedIn() {
        MockitoAnnotations.initMocks(this)

        val mockUserSessionRepository = mock(IUserSessionRepository::class.java)
        val navController = mock(NavHostController::class.java)

        `when`(mockUserSessionRepository.isLoggedIn).thenReturn(MutableLiveData(false))

        composeTestRule.setContent {
            AchievementsScreen(userSessionRepository = mockUserSessionRepository, navController = navController)
        }

        composeTestRule.onNodeWithText("Please log in to access this page").assertIsDisplayed()
    }
}
