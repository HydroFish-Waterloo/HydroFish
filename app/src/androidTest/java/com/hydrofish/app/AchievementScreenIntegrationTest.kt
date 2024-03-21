
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithText
import androidx.lifecycle.MutableLiveData
import androidx.navigation.NavHostController
import com.hydrofish.app.ui.composables.tabs.AchievementsScreen
import com.hydrofish.app.utils.IUserSessionRepository
import org.junit.Rule
import org.junit.Test
import org.mockito.Mockito
import org.mockito.Mockito.`when`

class AchievementScreenIntegrationTest {

    @get:Rule
    val composeTestRule = createComposeRule()
    //val score by achievementsViewModel.scoreLiveData.observeAsState(1)
    @Test
    fun testAchievementScreen_WhenUserLoggedIn() {

        val mockUserSessionRepository = Mockito.mock(IUserSessionRepository::class.java)
        val navController = Mockito.mock(NavHostController::class.java)

        `when`(mockUserSessionRepository.isLoggedIn).thenReturn(MutableLiveData(true))

        composeTestRule.setContent {
            AchievementsScreen(userSessionRepository = mockUserSessionRepository, navController = navController)
        }
        composeTestRule.onNodeWithText("Achievements").assertIsDisplayed()
    }

    @Test
    fun testAchievementScreen_WhenUserNotLoggedIn() {

        val mockUserSessionRepository = Mockito.mock(IUserSessionRepository::class.java)
        val navController = Mockito.mock(NavHostController::class.java)

        `when`(mockUserSessionRepository.isLoggedIn).thenReturn(MutableLiveData(false))

        composeTestRule.setContent {
            AchievementsScreen(userSessionRepository = mockUserSessionRepository, navController = navController)
        }

        composeTestRule.onNodeWithText("Please log in to access this page").assertIsDisplayed()
    }
}
