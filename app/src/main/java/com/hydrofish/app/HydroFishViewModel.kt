package com.hydrofish.app

import androidx.lifecycle.ViewModel
import com.hydrofish.app.ui.HydroFishUIState
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update

class HydroFishViewModel: ViewModel() {
    // backing property to avoid updates from other classes
    private val _uiState = MutableStateFlow(HydroFishUIState())
    val uiState: StateFlow<HydroFishUIState> = _uiState.asStateFlow()

    fun increaseWaterLevel(amt: Float) {
        _uiState.update {
            currentState -> currentState.copy(
                dailyWaterConsumedML = amt + currentState.dailyWaterConsumedML
            )
        }
    }
    
}

