package com.hydrofish.app.animations

class AnimationGroupPosition(private val startingPosition: Coordinates = Coordinates(0f, 0f)) {
    private val positionList = mutableListOf<FishInfo>()

    public fun getNewPosition(fishId: Int): Coordinates {
        if (positionList.size == 0) {
            positionList.add(FishInfo(startingPosition, fishId))
            return startingPosition
        }

        // obtain random pair
        val randomElement = positionList.asSequence().shuffled().find { true }
        val newCoordinates = randomElement?.coordinates?.getRandOffsetCoordinates(200f) as Coordinates
        positionList.add(FishInfo(newCoordinates, fishId))
        return newCoordinates
    }
}