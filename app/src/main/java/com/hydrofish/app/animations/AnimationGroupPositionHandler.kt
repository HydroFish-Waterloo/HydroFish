package com.hydrofish.app.animations

import java.util.Collections
import kotlin.random.Random

/**
 * creates groups of fish with their respective list of animations
 */
class AnimationGroupPositionHandler(animationsPerGroup: List<HashSet<AnimatableType>>) {
    private var populated = false
    private var animationGroups: List<AnimationGroup>
    init {
        val animationGroupMutable = mutableListOf<AnimationGroup>()

        for (param in animationsPerGroup) {
            animationGroupMutable.add(AnimationGroup(animatableTypes = param))
        }

        animationGroups = Collections.unmodifiableList(animationGroupMutable)
    }

    fun getAllFish(score: Int): List<AnimationGroup> {
        if (!populated) {
            val fishImageList = ImageListFromScore.getFishList(score);
            for (fishId in fishImageList) {
                val animationIdx = Random.nextInt(animationGroups.size)
                animationGroups[animationIdx].storeFish(fishId)
            }
            populated = true
        }

        return animationGroups
    }
}

/**
 * singular animation group - contains the animations that the fish perform and
 * the positions to place fish in the group
 */
class AnimationGroup(
    private val startingPosition: Coordinates = Coordinates(0f, 0f),
    private val animatableTypes: HashSet<AnimatableType>
) {
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

    fun getFishListWithAnim(): FishAnimAndList {
        return FishAnimAndList(animatableTypes, Collections.unmodifiableList(fishList))
    }
}