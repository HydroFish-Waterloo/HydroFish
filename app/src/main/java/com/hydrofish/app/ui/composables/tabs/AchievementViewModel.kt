package com.hydrofish.app.ui.composables.tabs

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import com.hydrofish.app.utils.IUserSessionRepository

class AchievementsViewModel(private val userSessionRepository: IUserSessionRepository) : ViewModel() {
    val scoreLiveData: LiveData<Int> = userSessionRepository.scoreLiveData
}
