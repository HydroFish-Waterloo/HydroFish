package com.hydrofish.app.ui.composables.unauthenticated.registration

import android.util.Log
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import com.hydrofish.app.api.ApiClient
import com.hydrofish.app.api.AuthSuccess
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
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class RegistrationViewModel(private val onTokenReceived: (String) -> Unit) : ViewModel() {

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
                    val call = ApiClient.apiService.registerUser(registerRequest)
                    call.enqueue(object : Callback<AuthSuccess> {
                        override fun onResponse(call: Call<AuthSuccess>, response: Response<AuthSuccess>) {
                            if (response.isSuccessful) {
                                val token = response.body()
                                // Handle the retrieved post data
                                Log.d("MainActivity", "Register here is token: $token")

                                if (token != null) {
                                    onTokenReceived(token.token)
                                    registrationState.value =
                                        registrationState.value.copy(isRegistrationSuccessful = true)
                                } else{

                                }
                            } else {
                                // Handle error
                                Log.e("MainActivity", "Failed to Register: ${response.code()}")
                            }
                        }

                        override fun onFailure(call: Call<AuthSuccess>, t: Throwable) {
                            // Handle failure
                            Log.e("MainActivity", "Error occurred while Register", t)
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