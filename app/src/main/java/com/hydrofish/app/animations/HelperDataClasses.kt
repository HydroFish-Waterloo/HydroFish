package com.hydrofish.app.animations

data class FishInfo(
    val coordinates: Coordinates,
    val fishId: Int
)

data class AnimationParams(
    val xVal: Float = 0f,
    val yVal: Float = 0f,
    val rotateVal: Float = 0f,
    val flipVal: Float = 0f
)