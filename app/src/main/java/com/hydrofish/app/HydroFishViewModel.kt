package com.hydrofish.app

import androidx.lifecycle.ViewModel
import com.hydrofish.app.ui.HydroFishUIState
import com.hydrofish.app.ui.composables.FishType
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update

class HydroFishViewModel: ViewModel() {
    // backing property to avoid updates from other classes
    private val _uiState = MutableStateFlow(HydroFishUIState())
    val uiState: StateFlow<HydroFishUIState> = _uiState.asStateFlow()
    private fun generateSequentialFloatList(size: Int, range: ClosedFloatingPointRange<Float>): List<Float> {
        val step = (range.endInclusive - range.start) / (size - 1)
        return List(size) { index -> range.start + step * index }
    }

    fun increaseWaterLevel(amt: Int) {
        if (amt <= 0) throw Exception("Cannot Increase Water By Negative Value");

        val willSurpassLimit = uiState.value.dailyWaterConsumedML + amt >= uiState.value.curDailyMaxWaterConsumedML;
        val hasSurpassedLimit = uiState.value.dailyWaterConsumedML >= uiState.value.curDailyMaxWaterConsumedML;

        if (willSurpassLimit && !hasSurpassedLimit) {
            val newList = uiState.value.fishTypeList
            newList.add(FishType.FISH_V1);
            _uiState.update { currentState ->
                currentState.copy(
                    dailyWaterConsumedML = amt + currentState.dailyWaterConsumedML,
                    fishDistances = generateSequentialFloatList(uiState.value.fishTypeList.size, 100f..600f),
                    fishTypeList = newList
                )
            }
        } else {
            _uiState.update { currentState ->
                currentState.copy(
                    dailyWaterConsumedML = amt + currentState.dailyWaterConsumedML,
                )
            }
        }
    }
}

