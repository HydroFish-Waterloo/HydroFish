package com.hydrofish.app.ui.composables.unauthenticated.login.state

import com.hydrofish.app.R
import com.hydrofish.app.ui.common.state.ErrorState

val UserNameEmptyErrorState = ErrorState(
    hasError = true,
    errorMessageStringResource = R.string.login_error_msg_empty_user_name
)

val passwordEmptyErrorState = ErrorState(
    hasError = true,
    errorMessageStringResource = R.string.login_error_msg_empty_password
)

val UserNameWrongErrorState = ErrorState(
    hasError = true,
    errorMessageStringResource = R.string.login_error_msg_wrong_user_name
)

val passwordWrongErrorState = ErrorState(
    hasError = true,
    errorMessageStringResource = R.string.login_error_msg_wrong_password
)