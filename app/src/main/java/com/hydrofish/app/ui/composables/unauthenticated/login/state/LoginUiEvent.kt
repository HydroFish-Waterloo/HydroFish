package com.hydrofish.app.ui.composables.unauthenticated.login.state

/**
 * Login Screen Events
 */
sealed class LoginUiEvent {
    data class UserNameChanged(val inputValue: String) : LoginUiEvent()
    data class PasswordChanged(val inputValue: String) : LoginUiEvent()
    object Submit : LoginUiEvent()
}