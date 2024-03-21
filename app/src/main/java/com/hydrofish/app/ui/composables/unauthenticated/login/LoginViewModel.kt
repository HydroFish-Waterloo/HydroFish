package com.hydrofish.app.ui.composables.unauthenticated.login

import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import com.hydrofish.app.api.ApiService
import com.hydrofish.app.api.AuthSuccess
import com.hydrofish.app.api.LoginRequest
import com.hydrofish.app.ui.common.state.ErrorState
import com.hydrofish.app.ui.composables.unauthenticated.login.state.LoginErrorState
import com.hydrofish.app.ui.composables.unauthenticated.login.state.LoginState
import com.hydrofish.app.ui.composables.unauthenticated.login.state.LoginUiEvent
import com.hydrofish.app.ui.composables.unauthenticated.login.state.UserNameEmptyErrorState
import com.hydrofish.app.ui.composables.unauthenticated.login.state.UserNameWrongErrorState
import com.hydrofish.app.ui.composables.unauthenticated.login.state.passwordEmptyErrorState
import com.hydrofish.app.ui.composables.unauthenticated.login.state.passwordWrongErrorState
import com.hydrofish.app.utils.IUserSessionRepository
import kotlinx.coroutines.flow.update
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

/**
 * ViewModel for Login Screen
 */
class LoginViewModel(
    private val userSessionRepository: IUserSessionRepository,
    private val apiService: ApiService
) : ViewModel() {

    var loginState = mutableStateOf(LoginState())
        private set

    /**
     * Function called on any login event [LoginUiEvent]
     */
    fun onUiEvent(loginUiEvent: LoginUiEvent) {
        when (loginUiEvent) {

            // UserName changed
            is LoginUiEvent.UserNameChanged -> {
                loginState.value = loginState.value.copy(
                    userName = loginUiEvent.inputValue,
                    errorState = loginState.value.errorState.copy(
                        userNameErrorState = if (loginUiEvent.inputValue.trim().isNotEmpty())
                            ErrorState()
                        else
                            UserNameEmptyErrorState
                    )
                )
            }

            // Password changed
            is LoginUiEvent.PasswordChanged -> {
                loginState.value = loginState.value.copy(
                    password = loginUiEvent.inputValue,
                    errorState = loginState.value.errorState.copy(
                        passwordErrorState = if (loginUiEvent.inputValue.trim().isNotEmpty())
                            ErrorState()
                        else
                            passwordEmptyErrorState
                    )
                )
            }

            // Submit Login
            is LoginUiEvent.Submit -> {
                val inputsValidated = validateInputs()
                if (inputsValidated) {
                    // TODO Trigger login in authentication flow

                    val loginRequest = LoginRequest(username = loginState.value.userName, password = loginState.value.password)
                    val call = apiService.loginUser(loginRequest)
                    call.enqueue(object : Callback<AuthSuccess> {
                        override fun onResponse(call: Call<AuthSuccess>, response: Response<AuthSuccess>) {
                            if (response.isSuccessful) {
                                val data = response.body()
                                // Handle the retrieved post data
                                if (data != null) {
                                    userSessionRepository.saveToken(data.token)
                                    userSessionRepository.saveUserName(data.username)
                                    userSessionRepository.syncScore(object : IUserSessionRepository.SyncScoreCallback {
                                        override fun onSuccess(score: Int) {
                                            // Handle success
                                        }

                                        override fun onFailure(errorCode: Int) {
                                            // Handle failure
                                        }
                                    })
                                    loginState.value = loginState.value.copy(isLoginSuccessful = true)
                                } else{

                                }

                            } else {
                                // Handle error
                                loginState.value = loginState.value.copy(
                                    errorState = loginState.value.errorState.copy(
                                        userNameErrorState = UserNameWrongErrorState,
                                        passwordErrorState = passwordWrongErrorState
                                    )
                                )
                            }
                        }

                        override fun onFailure(call: Call<AuthSuccess>, t: Throwable) {
                            // Handle failure
                        }
                    })

                }
            }
        }
    }

    /**
     * Function to validate inputs
     * Ideally it should be on domain layer (usecase)
     * @return true -> inputs are valid
     * @return false -> inputs are invalid
     */
    private fun validateInputs(): Boolean {
        val userNameString = loginState.value.userName.trim()
        val passwordString = loginState.value.password
        return when {

            // UserName empty
            userNameString.isEmpty() -> {
                loginState.value = loginState.value.copy(
                    errorState = LoginErrorState(
                        userNameErrorState = UserNameEmptyErrorState
                    )
                )
                false
            }

            //Password Empty
            passwordString.isEmpty() -> {
                loginState.value = loginState.value.copy(
                    errorState = LoginErrorState(
                        passwordErrorState = passwordEmptyErrorState
                    )
                )
                false
            }

            // No errors
            else -> {
                // Set default error state
                loginState.value = loginState.value.copy(errorState = LoginErrorState())
                true
            }
        }
    }

}