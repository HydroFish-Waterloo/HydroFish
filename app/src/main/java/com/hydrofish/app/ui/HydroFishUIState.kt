package com.hydrofish.app.ui

import com.hydrofish.app.animations.AnimatableType
import com.hydrofish.app.animations.AnimationGroupPositionHandler

data class HydroFishUIState(
    val dailyWaterConsumedML: Int = 0,
    val curDailyMaxWaterConsumedML: Int = 1000,

    var fishScore: Int = 1,
    val animationGroupPositionHandler: AnimationGroupPositionHandler
        = AnimationGroupPositionHandler(
            listOf(
                hashSetOf(AnimatableType.X, AnimatableType.FLIP),
                hashSetOf(AnimatableType.Y, AnimatableType.ROTATE),
                hashSetOf(AnimatableType.DIAGONAL_X, AnimatableType.DIAGONAL_Y, AnimatableType.DIAG_FLIP),
                hashSetOf(AnimatableType.DIAGONAL_X_R, AnimatableType.DIAGONAL_Y_R, AnimatableType.DIAG_FLIP_R),
            ),
        )
)

