package com.hydrofish.app

import androidx.lifecycle.ViewModel
import com.hydrofish.app.animations.AnimationGroupPosition
import com.hydrofish.app.animations.Coordinates
import com.hydrofish.app.animations.ImageListFromScore
import com.hydrofish.app.ui.HydroFishUIState
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import java.util.Collections
import kotlin.random.Random

class HydroFishViewModel: ViewModel() {
    // backing property to avoid updates from other classes
    private val _uiState = MutableStateFlow(HydroFishUIState())
    val uiState: StateFlow<HydroFishUIState> = _uiState.asStateFlow()
    private fun generateSequentialFloatList(size: Int, range: ClosedFloatingPointRange<Float>): List<Float> {
        val step = (range.endInclusive - range.start) / (size - 1)
        return List(size) { index -> range.start + step * index }
    }

    fun increaseWaterLevel(amt: Int) {
        if (amt < 0) throw Exception("Cannot Increase Water By Negative Value");
        _uiState.update { currentState ->
            currentState.copy(
                dailyWaterConsumedML = amt + currentState.dailyWaterConsumedML,
            )
        }
    }
}

