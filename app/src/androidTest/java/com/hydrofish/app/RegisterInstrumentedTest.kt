package com.hydrofish.app

import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onAllNodesWithText
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.performTextInput
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.hydrofish.app.api.ApiService
import com.hydrofish.app.api.AuthSuccess
import com.hydrofish.app.api.RegisterRequest
import com.hydrofish.app.ui.composables.unauthenticated.registration.RegistrationScreen
import com.hydrofish.app.ui.composables.unauthenticated.registration.RegistrationViewModel
import com.hydrofish.app.utils.IUserSessionRepository
import junit.framework.TestCase.assertTrue
import kotlinx.coroutines.test.runTest
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.ResponseBody.Companion.toResponseBody
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.ArgumentMatchers
import org.mockito.Mockito
import org.mockito.Mockito.mock
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

@RunWith(AndroidJUnit4::class)
class RegisterInstrumentedTest {
    private fun setupMockFailResponse(errorJson: String, registerRequest: RegisterRequest): Pair<IUserSessionRepository, ApiService> {
        val errorResponseBody = errorJson.toResponseBody("application/json".toMediaTypeOrNull())
        val mockErrorResponse = Response.error<AuthSuccess>(400, errorResponseBody)
        val mockUserSessionRepository = Mockito.mock(IUserSessionRepository::class.java)
        val mockApiService = mock(ApiService::class.java)
        val mockCall: Call<AuthSuccess> = mock(Call::class.java) as Call<AuthSuccess>

        Mockito.`when`(mockApiService.registerUser(registerRequest)).thenReturn(mockCall)
        Mockito.doAnswer { invocation ->
            val callback: Callback<AuthSuccess> = invocation.getArgument(0)
            callback.onResponse(mockCall, mockErrorResponse)
            null
        }.`when`(mockCall).enqueue(ArgumentMatchers.any())

        return Pair(mockUserSessionRepository, mockApiService)
    }

    private fun setupUi(userSessionRepository: IUserSessionRepository, apiService: ApiService) {
        val viewModel = RegistrationViewModel(userSessionRepository, apiService)
        composeTestRule.setContent {
            RegistrationScreen(
                onNavigateBack = {},
                onNavigateToAuthenticatedRoute = {},
                userSessionRepository = userSessionRepository,
                registrationViewModel = viewModel
            )
        }
    }
    private fun fillRegistrationForm(username: String, mobile: String, password: String, confirmPassword: String) {
        composeTestRule.onNodeWithTag("username").performTextInput(username)
        composeTestRule.onNodeWithTag("mobile").performTextInput(mobile)
        composeTestRule.onNodeWithTag("password").performTextInput(password)
        composeTestRule.onNodeWithTag("confirm password").performTextInput(confirmPassword)
    }

    private fun performRegistration() {
        composeTestRule.onNodeWithText("Register").performClick()
    }


    @get:Rule
    val composeTestRule = createComposeRule()

    @Test
    fun registerFailByUserName() = runTest {
        val errorJson = """
            {
                "username": ["A user with that username already exists."]
            }
        """.trimIndent()

        val wrongRegisterRequest = RegisterRequest(username = "wrong", password1 = "password", password2 = "password")

        val (userSessionRepository, apiService) = setupMockFailResponse(errorJson, wrongRegisterRequest)
        setupUi(userSessionRepository, apiService)
        fillRegistrationForm("wrong", "1235479633", "password", "password")
        performRegistration()

        composeTestRule.waitUntil(timeoutMillis = 1000) {
            composeTestRule.onAllNodesWithText("A user with that username already exists.").fetchSemanticsNodes()
                .isNotEmpty()
        }
    }

    @Test
    fun registerFailByUserNamePassword1() = runTest {
        val errorJson = """
            {
                "username": ["A user with that username already exists."],
                "password1": ["The password is too similar to the username."]
            }
        """.trimIndent()

        val wrongRegisterRequest = RegisterRequest(username = "wrong", password1 = "password", password2 = "password")

        val (userSessionRepository, apiService) = setupMockFailResponse(errorJson, wrongRegisterRequest)
        setupUi(userSessionRepository, apiService)
        fillRegistrationForm("wrong", "1235479633", "password", "password")
        performRegistration()

        composeTestRule.waitUntil(timeoutMillis = 1000) {
            composeTestRule.onAllNodesWithText("A user with that username already exists.").fetchSemanticsNodes()
                .isNotEmpty()
        }

        composeTestRule.waitUntil(timeoutMillis = 1000) {
            composeTestRule.onAllNodesWithText("The password is too similar to the username.").fetchSemanticsNodes()
                .isNotEmpty()
        }
    }

    @Test
    fun registerFailByUserNamePassword2() = runTest {
        val errorJson = """
            {
                "username": ["A user with that username already exists."],
                "password2": ["The password is too similar to the username."]
            }
        """.trimIndent()

        val wrongRegisterRequest = RegisterRequest(username = "wrong", password1 = "password", password2 = "password")

        val (userSessionRepository, apiService) = setupMockFailResponse(errorJson, wrongRegisterRequest)
        setupUi(userSessionRepository, apiService)
        fillRegistrationForm("wrong", "1235479633", "password", "password")
        performRegistration()

        composeTestRule.waitUntil(timeoutMillis = 1000) {
            composeTestRule.onAllNodesWithText("A user with that username already exists.").fetchSemanticsNodes()
                .isNotEmpty()
        }

        composeTestRule.waitUntil(timeoutMillis = 1000) {
            composeTestRule.onAllNodesWithText("The password is too similar to the username.").fetchSemanticsNodes()
                .isNotEmpty()
        }
    }

    @Test
    fun registerFailByUserNamePassword12() = runTest {
        val errorJson = """
            {
                "username": ["A user with that username already exists."],
                "password1": ["This password is too common."],
                "password2": ["The password is too similar to the username."]
            }
        """.trimIndent()

        val wrongRegisterRequest = RegisterRequest(username = "wrong", password1 = "password", password2 = "password")

        val (userSessionRepository, apiService) = setupMockFailResponse(errorJson, wrongRegisterRequest)
        setupUi(userSessionRepository, apiService)
        fillRegistrationForm("wrong", "1235479633", "password", "password")
        performRegistration()

        composeTestRule.waitUntil(timeoutMillis = 1000) {
            composeTestRule.onAllNodesWithText("A user with that username already exists.").fetchSemanticsNodes()
                .isNotEmpty()
        }

        composeTestRule.waitUntil(timeoutMillis = 1000) {
            composeTestRule.onAllNodesWithText("This password is too common.").fetchSemanticsNodes()
                .isNotEmpty()
        }

        composeTestRule.waitUntil(timeoutMillis = 1000) {
            composeTestRule.onAllNodesWithText("The password is too similar to the username.").fetchSemanticsNodes()
                .isNotEmpty()
        }
    }

    @Test
    fun registerFailByPassword1() = runTest {
        val errorJson = """
            {
                "password1": ["This password is too common."]
            }
        """.trimIndent()

        val wrongRegisterRequest = RegisterRequest(username = "wrong", password1 = "password", password2 = "password")

        val (userSessionRepository, apiService) = setupMockFailResponse(errorJson, wrongRegisterRequest)
        setupUi(userSessionRepository, apiService)
        fillRegistrationForm("wrong", "1235479633", "password", "password")
        performRegistration()

        composeTestRule.waitUntil(timeoutMillis = 1000) {
            composeTestRule.onAllNodesWithText("This password is too common.").fetchSemanticsNodes()
                .isNotEmpty()
        }
    }

    @Test
    fun registerFailByPassword2() = runTest {
        val errorJson = """
            {
                "password2": ["The password is too similar to the username."]
            }
        """.trimIndent()

        val wrongRegisterRequest = RegisterRequest(username = "wrong", password1 = "password", password2 = "password")

        val (userSessionRepository, apiService) = setupMockFailResponse(errorJson, wrongRegisterRequest)
        setupUi(userSessionRepository, apiService)
        fillRegistrationForm("wrong", "1235479633", "password", "password")
        performRegistration()

        composeTestRule.waitUntil(timeoutMillis = 1000) {
            composeTestRule.onAllNodesWithText("The password is too similar to the username.").fetchSemanticsNodes()
                .isNotEmpty()
        }
    }

    @Test
    fun registerFailByPassword12() = runTest {
        val errorJson = """
            {
                "password1": ["This password is too common."],
                "password2": ["The password is too similar to the username."]
            }
        """.trimIndent()

        val wrongRegisterRequest = RegisterRequest(username = "wrong", password1 = "password", password2 = "password")

        val (userSessionRepository, apiService) = setupMockFailResponse(errorJson, wrongRegisterRequest)
        setupUi(userSessionRepository, apiService)
        fillRegistrationForm("wrong", "1235479633", "password", "password")
        performRegistration()

        composeTestRule.waitUntil(timeoutMillis = 1000) {
            composeTestRule.onAllNodesWithText("This password is too common.").fetchSemanticsNodes()
                .isNotEmpty()
        }

        composeTestRule.waitUntil(timeoutMillis = 1000) {
            composeTestRule.onAllNodesWithText("The password is too similar to the username.").fetchSemanticsNodes()
                .isNotEmpty()
        }
    }

    @Test
    fun registerSuccess() = runTest {

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

        val correctRegisterRequest = RegisterRequest(username = "correct", password1 = "password", password2 = "password")

        val mockUserSessionRepository = Mockito.mock(IUserSessionRepository::class.java)
        val mockApiService = mock(ApiService::class.java)

        val mockCall: Call<AuthSuccess> = mock(Call::class.java) as Call<AuthSuccess>

        Mockito.`when`(mockApiService.registerUser(correctRegisterRequest)).thenReturn(mockCall)
        Mockito.doAnswer { invocation ->
            val callback: Callback<AuthSuccess> = invocation.getArgument(0)
            callback.onResponse(mockCall, mockSuccessResponse)
            null
        }.`when`(mockCall).enqueue(ArgumentMatchers.any())


        val viewModel = RegistrationViewModel(userSessionRepository = mockUserSessionRepository, apiService = mockApiService)

        composeTestRule.setContent {
            RegistrationScreen(
                onNavigateBack = {},
                onNavigateToAuthenticatedRoute = onNavigateToAuthenticatedRoute,
                userSessionRepository = mockUserSessionRepository,
                registrationViewModel = viewModel
            )
        }

        composeTestRule.onNodeWithTag("username").performTextInput("correct")
        composeTestRule.onNodeWithTag("mobile").performTextInput("1235479633")
        composeTestRule.onNodeWithTag("password").performTextInput("password")
        composeTestRule.onNodeWithTag("confirm password").performTextInput("password")

        composeTestRule.onNodeWithText("Register").performClick()

        composeTestRule.waitForIdle()

        assertTrue(isOnNavigateCalled)
    }

    @Test
    fun registerFailByEmptyUserName() = runTest {

        val mockUserSessionRepository = Mockito.mock(IUserSessionRepository::class.java)
        val mockApiService = mock(ApiService::class.java)

        val viewModel = RegistrationViewModel(userSessionRepository = mockUserSessionRepository, apiService = mockApiService)

        composeTestRule.setContent {
            RegistrationScreen(
                onNavigateBack = {},
                onNavigateToAuthenticatedRoute = {},
                userSessionRepository = mockUserSessionRepository,
                registrationViewModel = viewModel
            )
        }

        composeTestRule.onNodeWithTag("username").performTextInput("  ")
        composeTestRule.onNodeWithTag("mobile").performTextInput("1235479633")
        composeTestRule.onNodeWithTag("password").performTextInput("password")
        composeTestRule.onNodeWithTag("confirm password").performTextInput("password")

        composeTestRule.onNodeWithText("Register").performClick()

        composeTestRule.waitForIdle()

        composeTestRule.waitUntil(timeoutMillis = 1000) {
            composeTestRule.onAllNodesWithText("Please enter your user name").fetchSemanticsNodes()
                .isNotEmpty()
        }
    }

    @Test
    fun registerFailByEmptyMobile() = runTest {

        val mockUserSessionRepository = Mockito.mock(IUserSessionRepository::class.java)
        val mockApiService = mock(ApiService::class.java)

        val viewModel = RegistrationViewModel(userSessionRepository = mockUserSessionRepository, apiService = mockApiService)

        composeTestRule.setContent {
            RegistrationScreen(
                onNavigateBack = {},
                onNavigateToAuthenticatedRoute = {},
                userSessionRepository = mockUserSessionRepository,
                registrationViewModel = viewModel
            )
        }

        composeTestRule.onNodeWithTag("username").performTextInput("wrong")
        composeTestRule.onNodeWithTag("mobile").performTextInput("")
        composeTestRule.onNodeWithTag("password").performTextInput("password")
        composeTestRule.onNodeWithTag("confirm password").performTextInput("password")

        composeTestRule.onNodeWithText("Register").performClick()

        composeTestRule.waitForIdle()

        composeTestRule.waitUntil(timeoutMillis = 1000) {
            composeTestRule.onAllNodesWithText("Please enter your mobile number").fetchSemanticsNodes()
                .isNotEmpty()
        }
    }

    @Test
    fun registerFailByEmptyPassword1() = runTest {

        val mockUserSessionRepository = Mockito.mock(IUserSessionRepository::class.java)
        val mockApiService = mock(ApiService::class.java)

        val viewModel = RegistrationViewModel(userSessionRepository = mockUserSessionRepository, apiService = mockApiService)

        composeTestRule.setContent {
            RegistrationScreen(
                onNavigateBack = {},
                onNavigateToAuthenticatedRoute = {},
                userSessionRepository = mockUserSessionRepository,
                registrationViewModel = viewModel
            )
        }

        composeTestRule.onNodeWithTag("username").performTextInput("wrong")
        composeTestRule.onNodeWithTag("mobile").performTextInput("1235479633")
        composeTestRule.onNodeWithTag("password").performTextInput("")
        composeTestRule.onNodeWithTag("confirm password").performTextInput("password")

        composeTestRule.onNodeWithText("Register").performClick()

        composeTestRule.waitForIdle()

        composeTestRule.waitUntil(timeoutMillis = 1000) {
            composeTestRule.onAllNodesWithText("Please enter your password").fetchSemanticsNodes()
                .isNotEmpty()
        }
    }

    @Test
    fun registerFailByEmptyPassword2() = runTest {

        val mockUserSessionRepository = Mockito.mock(IUserSessionRepository::class.java)
        val mockApiService = mock(ApiService::class.java)

        val viewModel = RegistrationViewModel(userSessionRepository = mockUserSessionRepository, apiService = mockApiService)

        composeTestRule.setContent {
            RegistrationScreen(
                onNavigateBack = {},
                onNavigateToAuthenticatedRoute = {},
                userSessionRepository = mockUserSessionRepository,
                registrationViewModel = viewModel
            )
        }

        composeTestRule.onNodeWithTag("username").performTextInput("wrong")
        composeTestRule.onNodeWithTag("mobile").performTextInput("1235479633")
        composeTestRule.onNodeWithTag("password").performTextInput("password")
        composeTestRule.onNodeWithTag("confirm password").performTextInput("")

        composeTestRule.onNodeWithText("Register").performClick()

        composeTestRule.waitForIdle()

        composeTestRule.waitUntil(timeoutMillis = 1000) {
            composeTestRule.onAllNodesWithText("Please confirm your password").fetchSemanticsNodes()
                .isNotEmpty()
        }
    }

    @Test
    fun registerFailByMismatch() = runTest {

        val mockUserSessionRepository = Mockito.mock(IUserSessionRepository::class.java)
        val mockApiService = mock(ApiService::class.java)

        val viewModel = RegistrationViewModel(userSessionRepository = mockUserSessionRepository, apiService = mockApiService)

        composeTestRule.setContent {
            RegistrationScreen(
                onNavigateBack = {},
                onNavigateToAuthenticatedRoute = {},
                userSessionRepository = mockUserSessionRepository,
                registrationViewModel = viewModel
            )
        }

        composeTestRule.onNodeWithTag("username").performTextInput("wrong")
        composeTestRule.onNodeWithTag("mobile").performTextInput("1235479633")
        composeTestRule.onNodeWithTag("password").performTextInput("password")
        composeTestRule.onNodeWithTag("confirm password").performTextInput("pass")

        composeTestRule.onNodeWithText("Register").performClick()

        composeTestRule.waitForIdle()

        composeTestRule.waitUntil(timeoutMillis = 1000) {
            composeTestRule.onAllNodesWithText("Please enter the same password here as above").fetchSemanticsNodes()
                .isNotEmpty()
        }
    }
}