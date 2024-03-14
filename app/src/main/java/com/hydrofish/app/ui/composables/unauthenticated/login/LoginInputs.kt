package com.hydrofish.app.ui.composables.unauthenticated.login

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.style.TextAlign
import com.hydrofish.app.R
import com.hydrofish.app.ui.common.customComposableViews.NormalButton
import com.hydrofish.app.ui.common.customComposableViews.PasswordTextField
import com.hydrofish.app.ui.common.customComposableViews.UserNameTextField
import com.hydrofish.app.ui.composables.unauthenticated.login.state.LoginState
import com.hydrofish.app.ui.theme.AppTheme
@Composable
fun LoginInputs(
    loginState: LoginState,
    onUserNameOrMobileChange: (String) -> Unit,
    onPasswordChange: (String) -> Unit,
    onSubmit: () -> Unit,
    onForgotPasswordClick: () -> Unit
) {

    // Login Inputs Section
    Column(modifier = Modifier.fillMaxWidth()) {

        // UserName
        UserNameTextField(
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = AppTheme.dimens.paddingLarge),
            value = loginState.userName,
            onValueChange = onUserNameOrMobileChange,
            label = stringResource(id = R.string.login_user_name_label),
            isError = loginState.errorState.userNameErrorState.hasError,
            errorText = stringResource(id = loginState.errorState.userNameErrorState.errorMessageStringResource)
        )


        // Password
        PasswordTextField(
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = AppTheme.dimens.paddingLarge),
            value = loginState.password,
            onValueChange = onPasswordChange,
            label = stringResource(id = R.string.login_password_label),
            isError = loginState.errorState.passwordErrorState.hasError,
            errorText = stringResource(id = loginState.errorState.passwordErrorState.errorMessageStringResource),
            imeAction = ImeAction.Done
        )

        // Forgot Password
        Text(
            modifier = Modifier
                .padding(top = AppTheme.dimens.paddingSmall)
                .align(alignment = Alignment.End)
                .clickable {
                    onForgotPasswordClick.invoke()
                },
            text = stringResource(id = R.string.forgot_password),
            color = MaterialTheme.colorScheme.secondary,
            textAlign = TextAlign.End,
            style = MaterialTheme.typography.bodyMedium
        )

        // Login Submit Button
        NormalButton(
            modifier = Modifier.padding(top = AppTheme.dimens.paddingLarge),
            text = stringResource(id = R.string.login_button_text),
            onClick = onSubmit
        )

    }
}