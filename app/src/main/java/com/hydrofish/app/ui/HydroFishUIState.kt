package com.hydrofish.app.ui

import com.hydrofish.app.animations.AnimatableType
import com.hydrofish.app.animations.AnimationGroup
import com.hydrofish.app.animations.AnimationGroupPositionHandler

data class HydroFishUIState(
    val dailyWaterConsumedML: Int = 0,
    val curDailyMaxWaterConsumedML: Int = 1000,

    var fishScore: Int = 1,
    val animationGroupPositionHandler: AnimationGroupPositionHandler
        = AnimationGroupPositionHandler(
            listOf(
                /*hashSetOf(AnimatableType.X, AnimatableType.FLIP),
                hashSetOf(AnimatableType.Y, AnimatableType.ROTATE),
                hashSetOf(AnimatableType.CYCLE, AnimatableType.CYCLEX, AnimatableType.CYCLEY),*/
                hashSetOf(AnimatableType.CYCLE, AnimatableType.CYCLEX, AnimatableType.CYCLEY)
            ),
        ),
    var presentedFish: List<AnimationGroup> = animationGroupPositionHandler.getAllFish(fishScore)
)

