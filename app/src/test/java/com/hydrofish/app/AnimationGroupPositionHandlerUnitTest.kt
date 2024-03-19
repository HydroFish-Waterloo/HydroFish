package com.hydrofish.app

import com.hydrofish.app.animations.AnimatableType
import com.hydrofish.app.animations.AnimationGroup
import com.hydrofish.app.animations.AnimationGroupPositionHandler
import com.hydrofish.app.animations.Coordinates
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test

class AnimationGroupTest {

    private lateinit var animationGroup: AnimationGroup

    @Before
    fun setUp() {
        val startingPosition = Coordinates(0f, 0f)
        val animatableTypes = hashSetOf(AnimatableType.X, AnimatableType.Y)
        animationGroup = AnimationGroup(startingPosition, animatableTypes)
    }

    @Test
    fun testStoreFish() {
        val initialSize = animationGroup.getFishListWithAnim().fishes.size
        animationGroup.storeFish(1)
        val fishList = animationGroup.getFishListWithAnim().fishes
        assertTrue("Fish with ID 1 should be stored in the group", fishList.any { it.fishId == 1 })
        assertEquals(initialSize+1,fishList.size)

    }

    @Test
    fun testGetFishListWithAnim() {
        // Store some fish first
        animationGroup.storeFish(1)
        animationGroup.storeFish(2)

        val fishAnimAndList = animationGroup.getFishListWithAnim()

        assertEquals("The fish list should contain 2 fishes", 2, fishAnimAndList.fishes.size)
        assertTrue("The animation types should include X and Y", fishAnimAndList.animatableTypes.containsAll(listOf(AnimatableType.X, AnimatableType.Y)))
    }

    @Test
    fun testClearFishList() {
        // Store some fish first
        animationGroup.storeFish(1)
        animationGroup.storeFish(2)

        animationGroup.clearFishList()

        val fishAnimAndList = animationGroup.getFishListWithAnim()

        assertEquals("The fish list should contain 0 fish", 0, fishAnimAndList.fishes.size)

    }
}

class AnimationGroupPositionHandlerTest {

    private lateinit var handler: AnimationGroupPositionHandler

    @Before
    fun setUp() {
        // Initialize handler
        val animationsPerGroup = listOf(
            hashSetOf(AnimatableType.X, AnimatableType.Y),
            hashSetOf(AnimatableType.ROTATE)
        )
        handler = AnimationGroupPositionHandler(animationsPerGroup)
    }

    @Test
    fun testGetAllFish() {
        val score = 15
        val mockFishIds = listOf(1, 2, 3, 4)
        val animationGroups = handler.getAllFish(score)

        // Calculate the sum of sizes of the fishList in each AnimationGroup instance
        val totalFishInGroups = animationGroups.sumOf { it.getFishListWithAnim().fishes.size }

        // Assert that this sum equals the number of fish IDs returned by ImageListFromScore.getFishList(score)
        assertEquals("The total number of fish should match across all AnimationGroups", mockFishIds.size, totalFishInGroups)
    }
}