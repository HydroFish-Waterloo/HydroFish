package com.hydrofish.app.ui.composables.unauthenticated.registration.state

import com.hydrofish.app.ui.common.state.ErrorState

/**
 * Registration State holding ui input values
 */
data class RegistrationState(
    val userName: String = "",
    val mobileNumber: String = "",
    val password: String = "",
    val confirmPassword: String = "",
    val errorState: RegistrationErrorState = RegistrationErrorState(),
    val isRegistrationSuccessful: Boolean = false
)

/**
 * Error state in registration holding respective
 * text field validation errors
 */
data class RegistrationErrorState(
    val userNameErrorState: ErrorState = ErrorState(),
    val mobileNumberErrorState: ErrorState = ErrorState(),
    val passwordErrorState: ErrorState = ErrorState(),
    val confirmPasswordErrorState: ErrorState = ErrorState()
)