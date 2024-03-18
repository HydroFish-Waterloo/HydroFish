package com.hydrofish.app


import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onAllNodesWithText
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.performTextInput
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewmodel.compose.viewModel
import com.hydrofish.app.ui.composables.unauthenticated.login.LoginScreen
import com.hydrofish.app.ui.composables.unauthenticated.login.LoginViewModel
import com.hydrofish.app.utils.IUserSessionRepository
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.mockito.Mockito


class LoginInstrumentedTest {
    @get:Rule
    val composeTestRule = createComposeRule()

    @Before
    fun setup() {
        val mockLoginViewModel = Mockito.mock(LoginViewModel::class.java)

        composeTestRule.setContent {
            val fakeUserSessionRepository = FakeUserSessionRepository()
            val factory = TestViewModelFactory(mockLoginViewModel)
            val loginViewModel: LoginViewModel = viewModel(factory = factory)

            LoginScreen(
                onNavigateToRegistration = {},
                onNavigateToForgotPassword = {},
                onNavigateToAuthenticatedRoute = {},
                onNavigateBack = {},
                loginViewModel = loginViewModel
            )
        }
    }

    @Test
    fun login_withEmpty_showsErrorMessages() {

        composeTestRule.onNodeWithTag("username").performTextInput("wrong")
        composeTestRule.onNodeWithTag("username").performTextInput("")

        composeTestRule.waitUntil(timeoutMillis = 1000) {
            composeTestRule.onAllNodesWithText("Please enter your user name").fetchSemanticsNodes()
                .isNotEmpty()
        }

        composeTestRule.onNodeWithTag("password").performTextInput("    ")

        composeTestRule.waitUntil(timeoutMillis = 1000) {
            composeTestRule.onAllNodesWithText("Please enter your password").fetchSemanticsNodes()
                .isNotEmpty()
        }
    }

    @Test
    fun login_withInvalidCredentials_showsErrorMessages() {

        composeTestRule.onNodeWithTag("username").performTextInput("wrong")
        composeTestRule.onNodeWithTag("password").performTextInput("wrong")
        composeTestRule.onNodeWithText("Login").performClick()

        composeTestRule.waitUntil(timeoutMillis = 10000) {
            composeTestRule.onAllNodesWithText("User name might be wrong").fetchSemanticsNodes()
                .isNotEmpty()
        }

        composeTestRule.waitUntil(timeoutMillis = 10000) {
            composeTestRule.onAllNodesWithText("Password might be wrong").fetchSemanticsNodes()
                .isNotEmpty()
        }

    }

    class TestViewModelFactory(private val viewModel: ViewModel) : ViewModelProvider.Factory {
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            return viewModel as T
        }
    }

    class FakeUserSessionRepository : IUserSessionRepository {
        override fun saveToken(token: String) {

        }

        override fun saveUserName(token: String) {

        }

        override fun getToken(): String? {
            return null
        }

        override fun getUserName(): String? {
            return null
        }

        override fun clearData() {

        }

        override val isLoggedIn = MutableLiveData<Boolean>(false)

        override val scoreLiveData = MutableLiveData<Int>(0)

        override fun updateScore(newScore: Int) {
            scoreLiveData.value = newScore
        }
    }
}

//class FakeApiService : ApiService {
//    override suspend fun loginUser(loginRequest: LoginRequest): Response<AuthSuccess> {
//        return Response.error(400, "wwww".toResponseBody("text/plain".toMediaTypeOrNull()))
//    }
//
//    override suspend fun registerUser(registerRequest: RegisterRequest): Response<AuthSuccess> {
//        return Response.error(400, "wwww".toResponseBody("text/plain".toMediaTypeOrNull()))
//    }
//}
//
//@HiltAndroidTest
//class LoginScreenTest {
//
//    @get:Rule(order = 0)
//    val hiltRule = HiltAndroidRule(this)
//
//    @get:Rule(order = 1)
//    val composeTestRule = createComposeRule()
//
//    @Before
//    fun setup() {
//        hiltRule.inject()
//    }
//
//    @Test
//    fun loginFailure_displaysErrorMessage() {
//        composeTestRule.setContent {
//            LoginScreen(
//                onNavigateToRegistration = {},
//                onNavigateToForgotPassword = {},
//                onNavigateToAuthenticatedRoute = {},
//                onNavigateBack = {},
//                loginViewModel = hiltViewModel()
//            )
//        }
//
//        composeTestRule.onNodeWithTag("username").performTextInput("wrong")
//        composeTestRule.onNodeWithTag("password").performTextInput("wrong")
//        composeTestRule.onNodeWithText("Login").performClick()
//
//        composeTestRule.waitUntil(timeoutMillis = 10000) {
//            composeTestRule.onAllNodesWithText("User name might be wrong").fetchSemanticsNodes()
//                .isNotEmpty()
//        }
//
//        composeTestRule.waitUntil(timeoutMillis = 10000) {
//            composeTestRule.onAllNodesWithText("Password might be wrong").fetchSemanticsNodes()
//                .isNotEmpty()
//        }
//    }
//}