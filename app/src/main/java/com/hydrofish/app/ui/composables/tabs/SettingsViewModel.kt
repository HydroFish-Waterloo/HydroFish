package com.hydrofish.app.ui.composables.tabs

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import com.hydrofish.app.utils.IUserSessionRepository

class SettingsViewModel(private val userSessionRepository: IUserSessionRepository) : ViewModel() {

    val isLoggedIn: LiveData<Boolean> = userSessionRepository.isLoggedIn
    fun logout() {
        userSessionRepository.clearData()
    }
}