package com.hydrofish.app.ui.composables.unauthenticated.login.state

import com.hydrofish.app.ui.common.state.ErrorState

/**
 * Login State holding ui input values
 */
data class LoginState(
    val userName: String = "",
    val password: String = "",
    val errorState: LoginErrorState = LoginErrorState(),
    val isLoginSuccessful: Boolean = false
)

/**
 * Error state in login holding respective
 * text field validation errors
 */
data class LoginErrorState(
    val userNameErrorState: ErrorState = ErrorState(),
    val passwordErrorState: ErrorState = ErrorState()
)

