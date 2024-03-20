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

    @get:Rule
    val composeTestRule = createComposeRule()
    @Test
    fun registerFail() = runTest {


//        val mockUserSessionRepository = mockk<IUserSessionRepository>(relaxed = true)
//
//        val mockApiService = mockk<ApiService>()
//
//        coEvery {
//            mockApiService.registerUser(any())
//        } returns Response.error(
//            400,
//            errorJson.toResponseBody("application/json".toMediaType())
//        )

//        val errorResponse: Response<AuthSuccess> = Response.error(400, responseBody)
//
//

        val errorJson = """
            {
                "password2": ["The password is too similar to the username."]
            }
        """.trimIndent()
        val errorResponseBody = errorJson.toResponseBody("application/json".toMediaTypeOrNull())
        val mockErrorResponse = Response.error<AuthSuccess>(400, errorResponseBody)

        val wrongRegisterRequest = RegisterRequest(username = "wrong", password1 = "password", password2 = "password")
        val correctRegisterRequest = RegisterRequest(username = "correct", password1 = "password", password2 = "password")

        val mockUserSessionRepository = Mockito.mock(IUserSessionRepository::class.java)
        val mockApiService = mock(ApiService::class.java)

        val mockCall: Call<AuthSuccess> = mock(Call::class.java) as Call<AuthSuccess>

        Mockito.`when`(mockApiService.registerUser(wrongRegisterRequest)).thenReturn(mockCall)
        Mockito.doAnswer { invocation ->
            val callback: Callback<AuthSuccess> = invocation.getArgument(0)
            callback.onResponse(mockCall, mockErrorResponse)
            null
        }.`when`(mockCall).enqueue(ArgumentMatchers.any())

//        val mockCall = mock(Call::class.java) as Call<AuthSuccess>



//        `when`(mockCall.enqueue(any())).thenAnswer { invocation ->
//            val callback: Callback<AuthSuccess> = invocation.getArgument(0, Callback::class.java) as Callback<AuthSuccess>
//            callback.onResponse(mockCall, mockErrorResponse)
//        }



//        Mockito.doAnswer { invocation ->
//            val callback: Callback<AuthSuccess> = invocation.getArgument(0)
//            val response = Response.success(AuthSuccess("test", "werwfw"))
//            callback.onResponse(mockCall, response)
//            null
//        }.`when`(mockCall).enqueue(any())

//        Mockito.doAnswer { invocation ->
//            val callback: Callback<AuthSuccess> = invocation.getArgument(0)
//            val response = Response.error<AuthSuccess>(400, "错误响应".toResponseBody())
//            callback.onFailure(mockCall, RuntimeException("模拟的异常"))
//            null
//        }.`when`(mockCall).enqueue(any())

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
        composeTestRule.onNodeWithTag("mobile").performTextInput("123")
        composeTestRule.onNodeWithTag("password").performTextInput("password")
        composeTestRule.onNodeWithTag("confirm password").performTextInput("password")

        composeTestRule.onNodeWithText("Register").performClick()

        composeTestRule.waitUntil(timeoutMillis = 10000) {
            composeTestRule.onAllNodesWithText("The password is too similar to the username.").fetchSemanticsNodes()
                .isNotEmpty()
        }

//        composeTestRule.waitUntil(timeoutMillis = 10000) {
//            composeTestRule.onAllNodesWithText("Please enter your password").fetchSemanticsNodes()
//                .isNotEmpty()
//        }
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
        composeTestRule.onNodeWithTag("mobile").performTextInput("123")
        composeTestRule.onNodeWithTag("password").performTextInput("password")
        composeTestRule.onNodeWithTag("confirm password").performTextInput("password")

        composeTestRule.onNodeWithText("Register").performClick()

        composeTestRule.waitForIdle()

        assertTrue(isOnNavigateCalled)
    }
}