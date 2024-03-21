package com.hydrofish.app

import com.hydrofish.app.api.ApiService
import com.hydrofish.app.api.AuthSuccess
import com.hydrofish.app.ui.composables.unauthenticated.registration.RegistrationViewModel
import com.hydrofish.app.ui.composables.unauthenticated.registration.state.RegistrationUiEvent
import com.hydrofish.app.utils.IUserSessionRepository
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.ResponseBody.Companion.toResponseBody
import org.junit.Assert
import org.junit.Before
import org.junit.Test
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class RegisterViewModelUnitTest {
    private lateinit var viewModel: RegistrationViewModel
    private lateinit var mockApiService: ApiService
    private lateinit var mockUserSessionRepository: IUserSessionRepository
    private lateinit var mockAuthSuccessCall: Call<AuthSuccess>

    @Before
    fun setup() {
        mockApiService = mockk()
        mockUserSessionRepository = mockk(relaxed = true)
        mockAuthSuccessCall = mockk(relaxed = true)

        viewModel = RegistrationViewModel(mockUserSessionRepository, mockApiService)
    }

    @Test
    fun registerSuccess() {
        val authSuccess = AuthSuccess("testUser", "fake_token")
        val response = Response.success(authSuccess)

        every { mockApiService.registerUser(any()) } returns mockAuthSuccessCall
        every { mockAuthSuccessCall.enqueue(any()) } answers {
            firstArg<Callback<AuthSuccess>>().onResponse(mockAuthSuccessCall, response)
        }

        viewModel.onUiEvent(RegistrationUiEvent.UserNameChanged("testUser"))
        viewModel.onUiEvent(RegistrationUiEvent.MobileNumberChanged("1235479633"))
        viewModel.onUiEvent(RegistrationUiEvent.PasswordChanged("password123"))
        viewModel.onUiEvent(RegistrationUiEvent.ConfirmPasswordChanged("password123"))
        viewModel.onUiEvent(RegistrationUiEvent.Submit)

        Assert.assertTrue(viewModel.registrationState.value.isRegistrationSuccessful)
        verify { mockUserSessionRepository.saveToken("fake_token") }
        verify { mockUserSessionRepository.saveUserName("testUser") }
    }

    @Test
    fun registerFailByBackEnd() {
        val errorJson = """
            {
                "username": ["A user with that username already exists."],
                "password1": ["The password is too similar to the username."],
                "password2": ["The password is too similar to the username."]
            }
        """.trimIndent()

        val responseError = Response.error<AuthSuccess>(400, errorJson.toResponseBody("text/plain".toMediaTypeOrNull()))

        every { mockApiService.registerUser(any()) } returns mockAuthSuccessCall
        every { mockAuthSuccessCall.enqueue(any()) } answers {
            firstArg<Callback<AuthSuccess>>().onResponse(mockAuthSuccessCall, responseError)
        }

        viewModel.onUiEvent(RegistrationUiEvent.UserNameChanged("testUser"))
        viewModel.onUiEvent(RegistrationUiEvent.MobileNumberChanged("1235479633"))
        viewModel.onUiEvent(RegistrationUiEvent.PasswordChanged("password123"))
        viewModel.onUiEvent(RegistrationUiEvent.ConfirmPasswordChanged("password123"))
        viewModel.onUiEvent(RegistrationUiEvent.Submit)

        Assert.assertFalse(viewModel.registrationState.value.isRegistrationSuccessful)
        Assert.assertTrue(
            viewModel.registrationState.value.errorState.userNameErrorState.hasError
                    && viewModel.registrationState.value.errorState.passwordErrorState.hasError
                    && viewModel.registrationState.value.errorState.confirmPasswordErrorState.hasError
        )
    }

    @Test
    fun registerFailByEmptyUserName() {

        viewModel.onUiEvent(RegistrationUiEvent.UserNameChanged(""))
        viewModel.onUiEvent(RegistrationUiEvent.MobileNumberChanged("1235479633"))
        viewModel.onUiEvent(RegistrationUiEvent.PasswordChanged("password123"))
        viewModel.onUiEvent(RegistrationUiEvent.ConfirmPasswordChanged("password123"))
        viewModel.onUiEvent(RegistrationUiEvent.Submit)

        Assert.assertFalse(viewModel.registrationState.value.isRegistrationSuccessful)

        val userNameErrorState = viewModel.registrationState.value.errorState.userNameErrorState

        Assert.assertTrue(userNameErrorState.hasError)
        Assert.assertEquals(
            R.string.registration_error_msg_empty_user_name,
            userNameErrorState.errorMessageStringResource
        )
    }

    @Test
    fun registerFailByEmptyMobile() {

        viewModel.onUiEvent(RegistrationUiEvent.UserNameChanged("testUser"))
        viewModel.onUiEvent(RegistrationUiEvent.MobileNumberChanged(""))
        viewModel.onUiEvent(RegistrationUiEvent.PasswordChanged("password123"))
        viewModel.onUiEvent(RegistrationUiEvent.ConfirmPasswordChanged("password123"))
        viewModel.onUiEvent(RegistrationUiEvent.Submit)

        Assert.assertFalse(viewModel.registrationState.value.isRegistrationSuccessful)

        val mobileNumberErrorState = viewModel.registrationState.value.errorState.mobileNumberErrorState

        Assert.assertTrue(mobileNumberErrorState.hasError)
        Assert.assertEquals(
            R.string.registration_error_msg_empty_mobile,
            mobileNumberErrorState.errorMessageStringResource
        )
    }

    @Test
    fun registerFailByEmptyPassword() {

        viewModel.onUiEvent(RegistrationUiEvent.UserNameChanged("testUser"))
        viewModel.onUiEvent(RegistrationUiEvent.MobileNumberChanged("1235479633"))
        viewModel.onUiEvent(RegistrationUiEvent.PasswordChanged(""))
        viewModel.onUiEvent(RegistrationUiEvent.ConfirmPasswordChanged("password123"))
        viewModel.onUiEvent(RegistrationUiEvent.Submit)

        Assert.assertFalse(viewModel.registrationState.value.isRegistrationSuccessful)

        val passwordErrorState = viewModel.registrationState.value.errorState.passwordErrorState

        Assert.assertTrue(passwordErrorState.hasError)
        Assert.assertEquals(
            R.string.registration_error_msg_empty_password,
            passwordErrorState.errorMessageStringResource
        )
    }

    @Test
    fun registerFailByEmptyConfirmPassword() {

        viewModel.onUiEvent(RegistrationUiEvent.UserNameChanged("testUser"))
        viewModel.onUiEvent(RegistrationUiEvent.MobileNumberChanged("1235479633"))
        viewModel.onUiEvent(RegistrationUiEvent.PasswordChanged("password123"))
        viewModel.onUiEvent(RegistrationUiEvent.ConfirmPasswordChanged(""))
        viewModel.onUiEvent(RegistrationUiEvent.Submit)

        Assert.assertFalse(viewModel.registrationState.value.isRegistrationSuccessful)

        val confirmPasswordErrorState = viewModel.registrationState.value.errorState.confirmPasswordErrorState

        Assert.assertTrue(confirmPasswordErrorState.hasError)
        Assert.assertEquals(
            R.string.registration_error_msg_empty_confirm_password,
            confirmPasswordErrorState.errorMessageStringResource
        )
    }

    @Test
    fun registerFailByPasswordMismatch() {

        viewModel.onUiEvent(RegistrationUiEvent.UserNameChanged("testUser"))
        viewModel.onUiEvent(RegistrationUiEvent.MobileNumberChanged("1235479633"))
        viewModel.onUiEvent(RegistrationUiEvent.PasswordChanged("password"))
        viewModel.onUiEvent(RegistrationUiEvent.ConfirmPasswordChanged("password123"))
        viewModel.onUiEvent(RegistrationUiEvent.Submit)

        Assert.assertFalse(viewModel.registrationState.value.isRegistrationSuccessful)

        val confirmPasswordErrorState = viewModel.registrationState.value.errorState.confirmPasswordErrorState

        Assert.assertTrue(confirmPasswordErrorState.hasError)
        Assert.assertEquals(
            R.string.registration_error_msg_password_mismatch,
            confirmPasswordErrorState.errorMessageStringResource
        )
    }
}