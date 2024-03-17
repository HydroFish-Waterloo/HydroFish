package com.hydrofish.app

import com.hydrofish.app.api.ApiService
import com.hydrofish.app.api.AuthSuccess
import com.hydrofish.app.ui.composables.unauthenticated.login.LoginViewModel
import com.hydrofish.app.ui.composables.unauthenticated.login.state.LoginUiEvent
import com.hydrofish.app.utils.IUserSessionRepository
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.mockk
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.TestDispatcher
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.ResponseBody.Companion.toResponseBody
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.rules.TestWatcher
import org.junit.runner.Description
import retrofit2.Response


/**
 * Example local unit test, which will execute on the development machine (host).
 *
 * See [testing documentation](http://d.android.com/tools/testing).
 */

@ExperimentalCoroutinesApi
class MainCoroutineRule(val dispatcher: TestDispatcher = StandardTestDispatcher()) : TestWatcher() {

    override fun starting(description: Description?) {
        super.starting(description)
        Dispatchers.setMain(dispatcher)
    }

    override fun finished(description: Description?) {
        super.finished(description)
        Dispatchers.resetMain()
    }
}

@ExperimentalCoroutinesApi
class LoginViewModelUnitTest {

    @get:Rule
    var mainCoroutineRule = MainCoroutineRule()

    private lateinit var viewModel: LoginViewModel
    private lateinit var mockApiService: ApiService
    private lateinit var mockUserSessionRepository: IUserSessionRepository

    @Before
    fun setup() {
        mockApiService = mockk()
        mockUserSessionRepository = mockk(relaxed = true)
        viewModel = LoginViewModel(mockApiService, mockUserSessionRepository)
    }

    @Test
    fun `login success updates loginState`() = runTest {
        val loginResponse = Response.success(AuthSuccess("fake_username", "fake_token"))
        coEvery { mockApiService.loginUser(any()) } returns loginResponse

        viewModel.onUiEvent(LoginUiEvent.UserNameChanged("testUser"))
        viewModel.onUiEvent(LoginUiEvent.PasswordChanged("password123"))
        viewModel.onUiEvent(LoginUiEvent.Submit)


        advanceUntilIdle()

        assertTrue(viewModel.loginState.value.isLoginSuccessful)
        coVerify { mockUserSessionRepository.saveToken("fake_token") }
        coVerify { mockUserSessionRepository.saveUserName("fake_username") }
    }

    @Test
    fun `login failure updates loginState`() = runTest {
        val loginResponse = Response.error<AuthSuccess>(400, "".toResponseBody("text/plain".toMediaTypeOrNull()))
        coEvery { mockApiService.loginUser(any()) } returns loginResponse

        viewModel.onUiEvent(LoginUiEvent.UserNameChanged("testUser"))
        viewModel.onUiEvent(LoginUiEvent.PasswordChanged("password123"))
        viewModel.onUiEvent(LoginUiEvent.Submit)

        advanceUntilIdle()

        assertFalse(viewModel.loginState.value.isLoginSuccessful)
        assertTrue(viewModel.loginState.value.errorState.userNameErrorState.hasError || viewModel.loginState.value.errorState.passwordErrorState.hasError)
    }
}