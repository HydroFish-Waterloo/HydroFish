package com.hydrofish.app

import com.hydrofish.app.animations.ImageListFromScore
import org.junit.Assert.assertEquals
import org.junit.Test

class ImageListFromScoreTest {

    @Test
    fun testGetFishList () {
        // test cases
        val testCases = mapOf(
            -1 to emptyList(),
            0 to emptyList(),
            1 to listOf(R.drawable.fish1),
            2 to listOf(R.drawable.fish2),
            3 to listOf(R.drawable.fish1, R.drawable.fish2),
            4 to listOf(R.drawable.fish3),
            5 to listOf(R.drawable.fish1, R.drawable.fish3),
            6 to listOf(R.drawable.fish2, R.drawable.fish3),
            7 to listOf(R.drawable.fish1, R.drawable.fish2, R.drawable.fish3),
            8 to listOf(R.drawable.fish4),
            9 to listOf(R.drawable.fish1, R.drawable.fish4),
            10 to listOf(R.drawable.fish2, R.drawable.fish4),
            11 to listOf(R.drawable.fish1, R.drawable.fish2, R.drawable.fish4),
            12 to listOf(R.drawable.fish3, R.drawable.fish4),
            13 to listOf(R.drawable.fish1, R.drawable.fish3, R.drawable.fish4),
            14 to listOf(R.drawable.fish2, R.drawable.fish3, R.drawable.fish4),
            15 to listOf(R.drawable.fish1, R.drawable.fish2, R.drawable.fish3, R.drawable.fish4),
            16 to emptyList()
        )

        // test
        testCases.forEach { (input, expected) ->
            val result = ImageListFromScore.getFishList(input)
            assertEquals(expected, result)
        }
    }
}
