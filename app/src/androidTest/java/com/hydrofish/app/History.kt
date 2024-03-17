//import androidx.compose.ui.test.*
//import androidx.compose.foundation.layout.*
//import androidx.compose.ui.Modifier
//import androidx.compose.ui.test.junit4.createComposeRule
//import androidx.lifecycle.LiveData
//import androidx.lifecycle.MutableLiveData
//import androidx.navigation.NavController
//import androidx.navigation.NavHostController
//import com.hydrofish.app.ui.composables.tabs.HistoryScreen
//import com.hydrofish.app.utils.UserSessionRepository
//import kotlinx.coroutines.runBlocking
//import org.junit.Rule
//import org.junit.Test
//import org.mockito.Mockito.mock
//import org.mockito.Mockito.`when`
//
//class HistoryScreenTest {
//
//    @get:Rule
//    val composeTestRule = createComposeRule()
//
//    @Test
//    fun testHistoryScreen_WhenUserLoggedIn() {
//        // Mock dependencies
//        val mockUserSessionRepository = mock(UserSessionRepository::class.java)
//        val navController = mock(NavHostController::class.java)
//
//        // Mock user logged in
//        `when`(mockUserSessionRepository.isLoggedIn).thenReturn(MutableLiveData(true))
//
//        // Run test with logged in user
//        composeTestRule.setContent {
//            HistoryScreen(userSessionRepository = mockUserSessionRepository, navController = navController)
//        }
//
//        // Verify that Greeting "History" is displayed
//        composeTestRule.onNodeWithText("History").assertIsDisplayed()
//
//        // Other verification steps can be added based on UI behavior
//    }
//
//    @Test
//    fun testHistoryScreen_WhenUserNotLoggedIn() {
//        // Mock dependencies
//        val mockUserSessionRepository = mock(UserSessionRepository::class.java)
//        val navController = mock(NavHostController::class.java)
//
//        // Mock user not logged in
//        `when`(mockUserSessionRepository.isLoggedIn).thenReturn(MutableLiveData(false))
//
//        // Run test with user not logged in
//        composeTestRule.setContent {
//            HistoryScreen(userSessionRepository = mockUserSessionRepository, navController = navController)
//        }
//
//        // Verify that CoverScreen is displayed
//        // Add verification steps for CoverScreen
//    }
//
//    // Add more test cases as needed to cover different scenarios
//}
