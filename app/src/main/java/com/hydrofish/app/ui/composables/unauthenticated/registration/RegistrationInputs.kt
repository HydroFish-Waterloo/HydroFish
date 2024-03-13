package com.hydrofish.app.ui.composables.unauthenticated.registration

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.ImeAction
import com.hydrofish.app.R
import com.hydrofish.app.ui.common.customComposableViews.MobileNumberTextField
import com.hydrofish.app.ui.common.customComposableViews.NormalButton
import com.hydrofish.app.ui.common.customComposableViews.PasswordTextField
import com.hydrofish.app.ui.common.customComposableViews.UserNameTextField
import com.hydrofish.app.ui.composables.unauthenticated.registration.state.RegistrationState
import com.hydrofish.app.ui.theme.AppTheme

@Composable
fun RegistrationInputs(
    registrationState: RegistrationState,
    onUserNameChange: (String) -> Unit,
    onMobileNumberChange: (String) -> Unit,
    onPasswordChange: (String) -> Unit,
    onConfirmPasswordChange: (String) -> Unit,
    onSubmit: () -> Unit,
) {
    // Login Inputs Section
    Column(modifier = Modifier.fillMaxWidth()) {

        // UserName
        UserNameTextField(
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = AppTheme.dimens.paddingLarge),
            value = registrationState.userName,
            onValueChange = onUserNameChange,
            label = stringResource(id = R.string.registration_user_name_label),
            isError = registrationState.errorState.userNameErrorState.hasError,
            errorText = registrationState.errorState.userNameErrorState.errorMessageString
                ?: stringResource(id = registrationState.errorState.userNameErrorState.errorMessageStringResource),
            imeAction = ImeAction.Next
        )

        // Mobile Number
        MobileNumberTextField(
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = AppTheme.dimens.paddingLarge),
            value = registrationState.mobileNumber,
            onValueChange = onMobileNumberChange,
            label = stringResource(id = R.string.registration_mobile_label),
            isError = registrationState.errorState.mobileNumberErrorState.hasError,
            errorText = registrationState.errorState.mobileNumberErrorState.errorMessageString
                ?: stringResource(id = registrationState.errorState.mobileNumberErrorState.errorMessageStringResource),
            imeAction = ImeAction.Next
        )


        // Password
        PasswordTextField(
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = AppTheme.dimens.paddingLarge),
            value = registrationState.password,
            onValueChange = onPasswordChange,
            label = stringResource(id = R.string.registration_password_label),
            isError = registrationState.errorState.passwordErrorState.hasError,
            errorText = registrationState.errorState.passwordErrorState.errorMessageString
                ?: stringResource(id = registrationState.errorState.passwordErrorState.errorMessageStringResource),
            imeAction = ImeAction.Next
        )

        // Confirm Password
        PasswordTextField(
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = AppTheme.dimens.paddingLarge),
            value = registrationState.confirmPassword,
            onValueChange = onConfirmPasswordChange,
            label = stringResource(id = R.string.registration_confirm_password_label),
            isError = registrationState.errorState.confirmPasswordErrorState.hasError,
            errorText = registrationState.errorState.confirmPasswordErrorState.errorMessageString
                ?: stringResource(id = registrationState.errorState.confirmPasswordErrorState.errorMessageStringResource),
            imeAction = ImeAction.Done
        )

        // Registration Submit Button
        NormalButton(
            modifier = Modifier.padding(top = AppTheme.dimens.paddingExtraLarge),
            text = stringResource(id = R.string.registration_button_text),
            onClick = onSubmit
        )


    }
}