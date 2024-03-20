package com.hydrofish.app.ui.composables.tabs

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import com.hydrofish.app.utils.UserSessionRepository

class AchievementsViewModel(private val userSessionRepository: UserSessionRepository) : ViewModel() {
    val scoreLiveData: LiveData<Int> = userSessionRepository.scoreLiveData
    val isLoggedIn: LiveData<Boolean> = userSessionRepository.isLoggedIn
}
