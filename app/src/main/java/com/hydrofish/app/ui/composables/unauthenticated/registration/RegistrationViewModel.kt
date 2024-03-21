package com.hydrofish.app.ui.composables.unauthenticated.registration

import android.util.Log
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import com.google.gson.Gson
import com.hydrofish.app.api.ApiService
import com.hydrofish.app.api.AuthSuccess
import com.hydrofish.app.api.RegisterError
import com.hydrofish.app.api.RegisterRequest
import com.hydrofish.app.ui.common.state.ErrorState
import com.hydrofish.app.ui.composables.unauthenticated.registration.state.RegistrationErrorState
import com.hydrofish.app.ui.composables.unauthenticated.registration.state.RegistrationState
import com.hydrofish.app.ui.composables.unauthenticated.registration.state.RegistrationUiEvent
import com.hydrofish.app.ui.composables.unauthenticated.registration.state.confirmPasswordEmptyErrorState
import com.hydrofish.app.ui.composables.unauthenticated.registration.state.mobileNumberEmptyErrorState
import com.hydrofish.app.ui.composables.unauthenticated.registration.state.passwordEmptyErrorState
import com.hydrofish.app.ui.composables.unauthenticated.registration.state.passwordMismatchErrorState
import com.hydrofish.app.ui.composables.unauthenticated.registration.state.userNameEmptyErrorState
import com.hydrofish.app.utils.IUserSessionRepository
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class RegistrationViewModel(
    private val userSessionRepository: IUserSessionRepository,
    private val apiService: ApiService
) : ViewModel() {

    var registrationState = mutableStateOf(RegistrationState())
        private set

    /**
     * Function called on any login event [RegistrationUiEvent]
     */
    fun onUiEvent(registrationUiEvent: RegistrationUiEvent) {
        when (registrationUiEvent) {

            // UserName changed event
            is RegistrationUiEvent.UserNameChanged -> {
                registrationState.value = registrationState.value.copy(
                    userName = registrationUiEvent.inputValue,
                    errorState = registrationState.value.errorState.copy(
                        userNameErrorState = if (registrationUiEvent.inputValue.trim().isEmpty()) {
                            // UserName empty state
                            userNameEmptyErrorState
                        } else {
                            // Valid state
                            ErrorState()
                        }

                    )
                )
            }

            // Mobile Number changed event
            is RegistrationUiEvent.MobileNumberChanged -> {
                registrationState.value = registrationState.value.copy(
                    mobileNumber = registrationUiEvent.inputValue,
                    errorState = registrationState.value.errorState.copy(
                        mobileNumberErrorState = if (registrationUiEvent.inputValue.trim()
                                .isEmpty()
                        ) {
                            // Mobile Number Empty state
                            mobileNumberEmptyErrorState
                        } else {
                            // Valid state
                            ErrorState()
                        }

                    )
                )
            }

            // Password changed event
            is RegistrationUiEvent.PasswordChanged -> {
                registrationState.value = registrationState.value.copy(
                    password = registrationUiEvent.inputValue,
                    errorState = registrationState.value.errorState.copy(
                        passwordErrorState = if (registrationUiEvent.inputValue.trim().isEmpty()) {
                            // Password Empty state
                            passwordEmptyErrorState
                        } else {
                            // Valid state
                            ErrorState()
                        }

                    )
                )
            }

            // Confirm Password changed event
            is RegistrationUiEvent.ConfirmPasswordChanged -> {
                registrationState.value = registrationState.value.copy(
                    confirmPassword = registrationUiEvent.inputValue,
                    errorState = registrationState.value.errorState.copy(
                        confirmPasswordErrorState = when {

                            // Empty state of confirm password
                            registrationUiEvent.inputValue.trim().isEmpty() -> {
                                confirmPasswordEmptyErrorState
                            }

                            // Password is different than the confirm password
                            registrationState.value.password.trim() != registrationUiEvent.inputValue -> {
                                passwordMismatchErrorState
                            }

                            // Valid state
                            else -> ErrorState()
                        }
                    )
                )
            }


            // Submit Registration event
            is RegistrationUiEvent.Submit -> {
                val inputsValidated = validateInputs()
                if (inputsValidated) {


                    val registerRequest = RegisterRequest(username = registrationState.value.userName, password1 = registrationState.value.password, password2 = registrationState.value.confirmPassword)
                    val call = apiService.registerUser(registerRequest)
                    call.enqueue(object : Callback<AuthSuccess> {
                        override fun onResponse(call: Call<AuthSuccess>, response: Response<AuthSuccess>) {
                            if (response.isSuccessful) {
                                val data = response.body()
                                // Handle the retrieved post data
                                Log.d("Registration", "Register here is token: $data")

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
                                    registrationState.value =
                                        registrationState.value.copy(isRegistrationSuccessful = true)
                                } else{

                                }
                            } else {
                                // Handle error
                                val errorBody = response.errorBody()?.string()
                                val gson = Gson()
                                val registerError = gson.fromJson(errorBody, RegisterError::class.java)
                                if (registerError.username != null) {
                                    // Handle username error
                                    Log.e("Registration", "Username error: ${registerError.username.first()}")
                                    registrationState.value = registrationState.value.copy(
                                        errorState = registrationState.value.errorState.copy(
                                            userNameErrorState = ErrorState(
                                                hasError = true,
                                                errorMessageString = registerError.username.first()
                                            )
                                        )
                                    )
                                } else if (registerError.password1 != null) {
                                    // Handle password2 error
                                    Log.e("Registration", "Password2 error: ${registerError.password1.first()}")
                                    registrationState.value = registrationState.value.copy(
                                        errorState = registrationState.value.errorState.copy(
                                            passwordErrorState = ErrorState(
                                                hasError = true,
                                                errorMessageString = registerError.password1.first()
                                            ),
                                            confirmPasswordErrorState = ErrorState(
                                                    hasError = true,
                                                    errorMessageString = registerError.password1.first()
                                            )
                                        )
                                    )
                                } else if (registerError.password2 != null) {
                                    // Handle password2 error
                                    Log.e("Registration", "Password2 error: ${registerError.password2.first()}")
                                    registrationState.value = registrationState.value.copy(
                                        errorState = registrationState.value.errorState.copy(
                                            confirmPasswordErrorState = ErrorState(
                                                hasError = true,
                                                errorMessageString = registerError.password2.first()
                                            ),
                                            passwordErrorState = ErrorState(
                                                hasError = true,
                                                errorMessageString = registerError.password2.first()
                                            )
                                        )
                                    )
                                }
                                Log.e("Registration", "Failed to Register: ${response.code()}")
                            }
                        }

                        override fun onFailure(call: Call<AuthSuccess>, t: Throwable) {
                            // Handle failure
                            Log.e("Registration", "Error occurred while Register", t)
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
        val userNameString = registrationState.value.userName.trim()
        val mobileNumberString = registrationState.value.mobileNumber.trim()
        val passwordString = registrationState.value.password.trim()
        val confirmPasswordString = registrationState.value.confirmPassword.trim()

        return when {

            // UserName empty
            userNameString.isEmpty() -> {
                registrationState.value = registrationState.value.copy(
                    errorState = RegistrationErrorState(
                        userNameErrorState = userNameEmptyErrorState
                    )
                )
                false
            }

            //Mobile Number Empty
            mobileNumberString.isEmpty() -> {
                registrationState.value = registrationState.value.copy(
                    errorState = RegistrationErrorState(
                        mobileNumberErrorState = mobileNumberEmptyErrorState
                    )
                )
                false
            }

            //Password Empty
            passwordString.isEmpty() -> {
                registrationState.value = registrationState.value.copy(
                    errorState = RegistrationErrorState(
                        passwordErrorState = passwordEmptyErrorState
                    )
                )
                false
            }

            //Confirm Password Empty
            confirmPasswordString.isEmpty() -> {
                registrationState.value = registrationState.value.copy(
                    errorState = RegistrationErrorState(
                        confirmPasswordErrorState = confirmPasswordEmptyErrorState
                    )
                )
                false
            }

            // Password and Confirm Password are different
            passwordString != confirmPasswordString -> {
                registrationState.value = registrationState.value.copy(
                    errorState = RegistrationErrorState(
                        confirmPasswordErrorState = passwordMismatchErrorState
                    )
                )
                false
            }

            // No errors
            else -> {
                // Set default error state
                registrationState.value =
                    registrationState.value.copy(errorState = RegistrationErrorState())
                true
            }
        }
    }
}