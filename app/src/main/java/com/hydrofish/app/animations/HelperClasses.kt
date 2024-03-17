package com.hydrofish.app.animations

/**
 * saving fish and their coordinates for each animation group
 */
data class FishInfo (
    val coordinates: Coordinates,
    val fishId: Int
)

/**
 * return type when getting and generating fish
 */
data class FishAnimAndList(
    val animatableTypes: HashSet<AnimatableType>,
    val fishes: List<FishInfo>
)

enum class AnimatableType {
    X,
    Y,
    ROTATE,
    FLIP,
    DIAGONAL_X,
    DIAGONAL_Y,
    DIAG_FLIP,
    DIAG_FLIP_R,
    DIAGONAL_X_R,
    DIAGONAL_Y_R
}