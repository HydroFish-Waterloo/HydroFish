package com.hydrofish.app

import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onAllNodesWithText
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.performTextInput
import com.hydrofish.app.api.ApiService
import com.hydrofish.app.api.AuthSuccess
import com.hydrofish.app.api.LoginRequest
import com.hydrofish.app.ui.composables.unauthenticated.login.LoginScreen
import com.hydrofish.app.ui.composables.unauthenticated.login.LoginViewModel
import com.hydrofish.app.utils.IUserSessionRepository
import junit.framework.TestCase
import kotlinx.coroutines.test.runTest
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.ResponseBody.Companion.toResponseBody
import org.junit.Rule
import org.junit.Test
import org.mockito.ArgumentMatchers
import org.mockito.Mockito
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class LoginInstrumentedTest {
    private fun setupMockFailResponse(loginRequest: LoginRequest): Pair<IUserSessionRepository, ApiService> {
        val errorJson = """
            {
                "error": "Invalid Credentials"
            }
        """.trimIndent()
        val errorResponseBody = errorJson.toResponseBody("application/json".toMediaTypeOrNull())
        val mockErrorResponse = Response.error<AuthSuccess>(400, errorResponseBody)
        val mockUserSessionRepository = Mockito.mock(IUserSessionRepository::class.java)
        val mockApiService = Mockito.mock(ApiService::class.java)
        val mockCall: Call<AuthSuccess> = Mockito.mock(Call::class.java) as Call<AuthSuccess>

        Mockito.`when`(mockApiService.loginUser(loginRequest)).thenReturn(mockCall)
        Mockito.doAnswer { invocation ->
            val callback: Callback<AuthSuccess> = invocation.getArgument(0)
            callback.onResponse(mockCall, mockErrorResponse)
            null
        }.`when`(mockCall).enqueue(ArgumentMatchers.any())

        return Pair(mockUserSessionRepository, mockApiService)
    }

    private fun setupUi(userSessionRepository: IUserSessionRepository, apiService: ApiService) {
        val viewModel = LoginViewModel(userSessionRepository, apiService)
        composeTestRule.setContent {
            LoginScreen(
                onNavigateToRegistration = {},
                onNavigateToForgotPassword= {},
                onNavigateToAuthenticatedRoute= {},
                onNavigateBack= {},
                userSessionRepository = userSessionRepository,
                loginViewModel = viewModel
            )
        }
    }
    private fun fillLoginForm(username: String, password: String) {
        composeTestRule.onNodeWithTag("username").performTextInput(username)
        composeTestRule.onNodeWithTag("password").performTextInput(password)
    }

    private fun performLogin() {
        composeTestRule.onNodeWithTag("login").performClick()
    }

    @get:Rule
    val composeTestRule = createComposeRule()

    @Test
    fun loginFail() = runTest {

        val wrongLoginRequest = LoginRequest(username = "wrong", password = "password")

        val (userSessionRepository, apiService) = setupMockFailResponse(wrongLoginRequest)
        setupUi(userSessionRepository, apiService)
        fillLoginForm("wrong", "password")
        performLogin()

        composeTestRule.waitUntil(timeoutMillis = 1000) {
            composeTestRule.onAllNodesWithText("User name might be wrong").fetchSemanticsNodes()
                .isNotEmpty()
        }

        composeTestRule.waitUntil(timeoutMillis = 1000) {
            composeTestRule.onAllNodesWithText("Password might be wrong").fetchSemanticsNodes()
                .isNotEmpty()
        }
    }
    @Test
    fun loginSuccess() = runTest {

        var isOnNavigateCalled = false

        val onNavigateToAuthenticatedRoute = {
            isOnNavigateCalled = true
        }

        val mockSuccessResponse = Response.success(
            AuthSuccess(
                token = "6d2eee5b621aa765c1d5e7f201de347026be7309",
                username = "testtttt"
            )
        )

        val correctLoginRequest = LoginRequest(username = "correct", password = "password")

        val mockUserSessionRepository = Mockito.mock(IUserSessionRepository::class.java)
        val mockApiService = Mockito.mock(ApiService::class.java)

        val mockCall: Call<AuthSuccess> = Mockito.mock(Call::class.java) as Call<AuthSuccess>

        Mockito.`when`(mockApiService.loginUser(correctLoginRequest)).thenReturn(mockCall)
        Mockito.doAnswer { invocation ->
            val callback: Callback<AuthSuccess> = invocation.getArgument(0)
            callback.onResponse(mockCall, mockSuccessResponse)
            null
        }.`when`(mockCall).enqueue(ArgumentMatchers.any())


        val viewModel = LoginViewModel(userSessionRepository = mockUserSessionRepository, apiService = mockApiService)

        composeTestRule.setContent {
            LoginScreen(
                onNavigateToRegistration = {},
                onNavigateToForgotPassword= {},
                onNavigateToAuthenticatedRoute= onNavigateToAuthenticatedRoute,
                onNavigateBack= {},
                userSessionRepository = mockUserSessionRepository,
                loginViewModel = viewModel
            )
        }

        composeTestRule.onNodeWithTag("username").performTextInput("correct")
        composeTestRule.onNodeWithTag("password").performTextInput("password")

        composeTestRule.onNodeWithTag("login").performClick()

        composeTestRule.waitForIdle()

        TestCase.assertTrue(isOnNavigateCalled)
    }

    @Test
    fun loginFailByEmptyUserName() = runTest {

        val mockUserSessionRepository = Mockito.mock(IUserSessionRepository::class.java)
        val mockApiService = Mockito.mock(ApiService::class.java)

        val viewModel = LoginViewModel(userSessionRepository = mockUserSessionRepository, apiService = mockApiService)

        composeTestRule.setContent {
            LoginScreen(
                onNavigateToRegistration = {},
                onNavigateToForgotPassword = {},
                onNavigateToAuthenticatedRoute = {},
                onNavigateBack = {},
                userSessionRepository = mockUserSessionRepository,
                loginViewModel = viewModel
            )
        }

        composeTestRule.onNodeWithTag("username").performTextInput("")
        composeTestRule.onNodeWithTag("password").performTextInput("password")

        composeTestRule.onNodeWithTag("login").performClick()

        composeTestRule.waitForIdle()

        composeTestRule.waitUntil(timeoutMillis = 1000) {
            composeTestRule.onAllNodesWithText("Please enter your user name").fetchSemanticsNodes()
                .isNotEmpty()
        }
    }

    @Test
    fun loginFailByEmptyPassword() = runTest {

        val mockUserSessionRepository = Mockito.mock(IUserSessionRepository::class.java)
        val mockApiService = Mockito.mock(ApiService::class.java)

        val viewModel = LoginViewModel(userSessionRepository = mockUserSessionRepository, apiService = mockApiService)

        composeTestRule.setContent {
            LoginScreen(
                onNavigateToRegistration = {},
                onNavigateToForgotPassword = {},
                onNavigateToAuthenticatedRoute = {},
                onNavigateBack = {},
                userSessionRepository = mockUserSessionRepository,
                loginViewModel = viewModel
            )
        }

        composeTestRule.onNodeWithTag("username").performTextInput("correct")
        composeTestRule.onNodeWithTag("password").performTextInput("")

        composeTestRule.onNodeWithTag("login").performClick()

        composeTestRule.waitForIdle()

        composeTestRule.waitUntil(timeoutMillis = 1000) {
            composeTestRule.onAllNodesWithText("Please enter your password").fetchSemanticsNodes()
                .isNotEmpty()
        }
    }
}