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
                //*hashSetOf(AnimatableType.CAMERASCALE)*//*
                //*hashSetOf(AnimatableType.SCALE),*//*
                hashSetOf(AnimatableType.Y, AnimatableType.ROTATE),
                hashSetOf(AnimatableType.DIAGONAL_X, AnimatableType.DIAGONAL_Y, AnimatableType.DIAG_FLIP),
                hashSetOf(AnimatableType.DIAGONAL_X_R, AnimatableType.DIAGONAL_Y_R, AnimatableType.DIAG_FLIP_R),
                /*hashSetOf(AnimatableType.X, AnimatableType.ROTATE),*/
                /*hashSetOf(AnimatableType.SCALE_X, AnimatableType.SCALE_FLIP, AnimatableType.SCALE)*/
                /*hashSetOf(AnimatableType.CYCLE,AnimatableType.CYCLEX,AnimatableType.CYCLEY)*/
            ),
        )
)

