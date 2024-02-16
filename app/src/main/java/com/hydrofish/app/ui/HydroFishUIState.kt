package com.hydrofish.app.ui

import com.hydrofish.app.ui.composables.FishType

data class HydroFishUIState(
    val dailyWaterConsumedML: Float = 0f,
    val curDailyMaxWaterConsumedML: Float = 1000f,
    val fishTypeList: MutableList<FishType> = mutableListOf(FishType.FISH_V1, FishType.FISH_V1),
    var fishDistances: List<Float> = listOf(300f, 500f)
)

