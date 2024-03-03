package com.hydrofish.app.ui

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.AnimationVector1D

data class HydroFishUIState(
    val dailyWaterConsumedML: Int = 0,
    val curDailyMaxWaterConsumedML: Int = 1000,
    val fishTypeList: MutableList<FishType> = mutableListOf(FishType.FISH_V1, FishType.FISH_V1),
    var fishDistances: List<Float> = listOf(300f, 500f),

    var animatableX: Animatable<Float, AnimationVector1D> = Animatable(0f)
)

