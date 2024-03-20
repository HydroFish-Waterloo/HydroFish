package com.hydrofish.app

import com.hydrofish.app.api.ApiService
import com.hydrofish.app.api.AuthSuccess
import com.hydrofish.app.ui.composables.unauthenticated.login.LoginViewModel
import com.hydrofish.app.ui.composables.unauthenticated.login.state.LoginUiEvent
import com.hydrofish.app.utils.IUserSessionRepository
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.ResponseBody.Companion.toResponseBody
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class LoginViewModelUnitTest {

    private lateinit var viewModel: LoginViewModel
    private lateinit var mockApiService: ApiService
    private lateinit var mockUserSessionRepository: IUserSessionRepository
    private lateinit var mockAuthSuccessCall: Call<AuthSuccess>

    @Before
    fun setup() {
        mockApiService = mockk()
        mockUserSessionRepository = mockk(relaxed = true)
        mockAuthSuccessCall = mockk(relaxed = true)

        viewModel = LoginViewModel(mockUserSessionRepository, mockApiService)
    }

    @Test
    fun loginSuccess() {
        val authSuccess = AuthSuccess("fake_username", "fake_token")
        val response = Response.success(authSuccess)

        every { mockApiService.loginUser(any()) } returns mockAuthSuccessCall
        every { mockAuthSuccessCall.enqueue(any()) } answers {
            firstArg<Callback<AuthSuccess>>().onResponse(mockAuthSuccessCall, response)
        }

        viewModel.onUiEvent(LoginUiEvent.UserNameChanged("testUser"))
        viewModel.onUiEvent(LoginUiEvent.PasswordChanged("password123"))
        viewModel.onUiEvent(LoginUiEvent.Submit)

        assertTrue(viewModel.loginState.value.isLoginSuccessful)
        verify { mockUserSessionRepository.saveToken("fake_token") }
        verify { mockUserSessionRepository.saveUserName("fake_username") }
    }

    @Test
    fun loginFail() {
        val responseError = Response.error<AuthSuccess>(400, "".toResponseBody("text/plain".toMediaTypeOrNull()))

        every { mockApiService.loginUser(any()) } returns mockAuthSuccessCall
        every { mockAuthSuccessCall.enqueue(any()) } answers {
            firstArg<Callback<AuthSuccess>>().onResponse(mockAuthSuccessCall, responseError)
        }

        viewModel.onUiEvent(LoginUiEvent.UserNameChanged("testUser"))
        viewModel.onUiEvent(LoginUiEvent.PasswordChanged("password123"))
        viewModel.onUiEvent(LoginUiEvent.Submit)

        assertFalse(viewModel.loginState.value.isLoginSuccessful)
        assertTrue(viewModel.loginState.value.errorState.userNameErrorState.hasError && viewModel.loginState.value.errorState.passwordErrorState.hasError)
    }
}
