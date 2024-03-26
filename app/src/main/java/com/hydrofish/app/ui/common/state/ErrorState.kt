package com.hydrofish.app.ui.common.state

import androidx.annotation.StringRes
import com.hydrofish.app.R

/**
 * Error state holding values for error ui
 */
data class ErrorState(
    val hasError: Boolean = false,
    val errorMessageString: String? = null,
    @StringRes val errorMessageStringResource: Int = R.string.empty_string
)