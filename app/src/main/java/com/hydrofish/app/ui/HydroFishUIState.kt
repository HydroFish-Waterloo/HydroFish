package com.hydrofish.app.ui

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.AnimationVector1D
import com.hydrofish.app.animations.AnimationGroupPosition
import com.hydrofish.app.animations.AnimationGroupPositionHandler
import com.hydrofish.app.animations.FishInfo

data class HydroFishUIState(
    val dailyWaterConsumedML: Int = 0,
    val curDailyMaxWaterConsumedML: Int = 1000,

    var fishScore: Int = 15,
    val animationGroupPositionHandler: AnimationGroupPositionHandler = AnimationGroupPositionHandler(2)
)

