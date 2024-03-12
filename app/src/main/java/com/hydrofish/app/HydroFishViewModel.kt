package com.hydrofish.app

import android.util.Log
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
        val willSurpassLimit = uiState.value.dailyWaterConsumedML + amt >= uiState.value.curDailyMaxWaterConsumedML;
        val hasSurpassedLimit = uiState.value.dailyWaterConsumedML >= uiState.value.curDailyMaxWaterConsumedML;

        if (amt < 0) throw Exception("Cannot Increase Water By Negative Value");
        if (willSurpassLimit && !hasSurpassedLimit){
            _uiState.update { currentState ->
                val newFishScore = currentState.fishScore + 1
                val newPresentedFish = currentState.animationGroupPositionHandler.getAllFish(newFishScore)
                //FishScore is updates
                // But
                Log.d("HydroFishViewModel", "New fish score: $newFishScore")
                Log.d("HydroFishViewModel", "New presentedFish size: ${newPresentedFish.size}")
                currentState.copy(
                    fishScore = newFishScore,
                    dailyWaterConsumedML = amt + currentState.dailyWaterConsumedML,
                    presentedFish = newPresentedFish
                )
            }
        }  else{
            _uiState.update { currentState ->
                currentState.copy(
                    dailyWaterConsumedML = amt + currentState.dailyWaterConsumedML,
                )
            }
        }
    }


    fun getAllFish(): List<AnimationGroup> {
        return uiState.value.animationGroupPositionHandler.getAllFish(uiState.value.fishScore)
    }
}

