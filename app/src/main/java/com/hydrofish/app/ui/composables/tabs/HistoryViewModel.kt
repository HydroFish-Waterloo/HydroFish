package com.hydrofish.app.ui.composables.tabs

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import com.hydrofish.app.utils.UserSessionRepository

class HistoryViewModel(private val userSessionRepository: UserSessionRepository): ViewModel() {
    val isLoggedIn: LiveData<Boolean> = userSessionRepository.isLoggedIn
    fun logout() {
        userSessionRepository.clearToken()
    }
}