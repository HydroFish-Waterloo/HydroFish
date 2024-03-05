package com.hydrofish.app.animations

import android.util.Log
import java.util.Collections
import kotlin.random.Random

class AnimationGroupPositionHandler(private val animationCount: Int) {
    private var populated = false
    // list of animation groups
    private var animationGroupPositions: List<AnimationGroupPosition>
    init {
        val animationGroupPositionsMutable = mutableListOf<AnimationGroupPosition>()

        for (i in 0 until animationCount) {
            animationGroupPositionsMutable.add(AnimationGroupPosition())
        }

        animationGroupPositions = Collections.unmodifiableList(animationGroupPositionsMutable)
    }

    fun getPositions(score: Int): List<AnimationGroupPosition> {
        if (!populated) {
            val fishImageList = ImageListFromScore.getFishList(score);
            Log.d("test", score.toString())
            for (fishId in fishImageList) {
                Log.d("test", fishId.toString())
                val animationIdx = Random.nextInt(animationGroupPositions.size)
                animationGroupPositions[animationIdx].storeFish(fishId)
            }
            populated = true
        }
        return animationGroupPositions
    }
}

class AnimationGroupPosition(private val startingPosition: Coordinates = Coordinates(0f, 0f)) {
    // save a list of fish in the animation group
    private val fishList = mutableListOf<FishInfo>()

    public fun storeFish(fishId: Int) {
        if (fishList.size == 0) {
            fishList.add(FishInfo(startingPosition, fishId))
            return
        }

        // save random pair
        val randomElement = fishList.asSequence().shuffled().find { true }
        val newCoordinates = randomElement?.coordinates?.getRandOffsetCoordinates(200f) as Coordinates
        fishList.add(FishInfo(newCoordinates, fishId))
    }

    fun getFishList(): List<FishInfo> {
        return Collections.unmodifiableList(fishList)
    }
}