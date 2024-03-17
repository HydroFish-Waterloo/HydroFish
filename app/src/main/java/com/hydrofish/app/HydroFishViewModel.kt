package com.hydrofish.app

import androidx.lifecycle.ViewModel
import com.hydrofish.app.animations.AnimationGroup
import com.hydrofish.app.ui.HydroFishUIState
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update

class HydroFishViewModel: ViewModel() {
    // backing property to avoid updates from other classes
    private val _uiState = MutableStateFlow(HydroFishUIState())
    val uiState: StateFlow<HydroFishUIState> = _uiState.asStateFlow()

    fun increaseWaterLevel(amt: Int) {

        if (amt < 0) throw Exception("Cannot Increase Water By Negative Value");
        val willSurpassLimit = uiState.value.dailyWaterConsumedML + amt >= uiState.value.curDailyMaxWaterConsumedML;
        val hasSurpassedLimit = uiState.value.dailyWaterConsumedML >= uiState.value.curDailyMaxWaterConsumedML;

        if (willSurpassLimit && !hasSurpassedLimit) {

            _uiState.update { currentState ->
                currentState.copy(
                    fishScore = currentState.fishScore + 1

                )
            }
            uiState.value.animationGroupPositionHandler.prepForPopulation()


        }
        else {

        }
        _uiState.update { currentState ->
            currentState.copy(
                dailyWaterConsumedML = amt + currentState.dailyWaterConsumedML,
            ) }

    }

    fun getAllFish(): List<AnimationGroup> {
        return uiState.value.animationGroupPositionHandler.getAllFish(uiState.value.fishScore)
    }
}

