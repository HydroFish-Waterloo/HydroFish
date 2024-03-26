package com.hydrofish.app.viewmodelfactories

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.hydrofish.app.HydroFishViewModel
import com.hydrofish.app.api.ApiService
import com.hydrofish.app.ui.composables.tabs.AchievementsViewModel
import com.hydrofish.app.ui.composables.tabs.HistoryViewModel
import com.hydrofish.app.ui.composables.tabs.SettingsViewModel
import com.hydrofish.app.ui.composables.unauthenticated.login.LoginViewModel
import com.hydrofish.app.ui.composables.unauthenticated.registration.RegistrationViewModel
import com.hydrofish.app.utils.IUserSessionRepository

class LoginViewModelFactory(private val userSessionRepository: IUserSessionRepository, private val apiService: ApiService) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(LoginViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return LoginViewModel(userSessionRepository, apiService) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}

class RegisterViewModelFactory(private val userSessionRepository: IUserSessionRepository, private val apiService: ApiService) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(RegistrationViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return RegistrationViewModel(userSessionRepository, apiService) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}

class SettingsViewModelFactory(private val userSessionRepository: IUserSessionRepository) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(SettingsViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return SettingsViewModel(userSessionRepository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}

class AchievementsViewModelFactory(private val userSessionRepository: IUserSessionRepository) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(AchievementsViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return AchievementsViewModel(userSessionRepository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}

class HistoryViewModelFactory(private val userSessionRepository: IUserSessionRepository) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(HistoryViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return HistoryViewModel(userSessionRepository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}

class HydroFishViewModelFactory(private val userSessionRepository: IUserSessionRepository) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(HydroFishViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return HydroFishViewModel(userSessionRepository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}