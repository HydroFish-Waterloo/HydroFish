package com.hydrofish.app

import com.hydrofish.app.animations.ImageListFromScore
import junit.framework.TestCase.assertEquals
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.junit.runners.Parameterized


@RunWith(Parameterized::class)
class ImageListFromScoreTest(private val input: Int, private val expectedImages: List<Int>) {
    private lateinit var imgListFromScore: ImageListFromScore

    companion object {
        @JvmStatic
        @Parameterized.Parameters(name = "getFishList({0}) should return images {1}")
        fun data(): Collection<Array<Any>> {
            return listOf(
                arrayOf(0, emptyList<Int>()),
                arrayOf(1, listOf(R.drawable.fish1)),
                arrayOf(2, listOf(R.drawable.fish2)),
                arrayOf(3, listOf(R.drawable.fish1, R.drawable.fish2)),
                arrayOf(4, listOf(R.drawable.fish3)),
                arrayOf(5, listOf(R.drawable.fish1, R.drawable.fish3)),
                arrayOf(6, listOf(R.drawable.fish2, R.drawable.fish3)),
                arrayOf(7, listOf(R.drawable.fish1, R.drawable.fish2, R.drawable.fish3)),
                arrayOf(8, listOf(R.drawable.fish4)),
                arrayOf(9, listOf(R.drawable.fish1, R.drawable.fish4)),
                arrayOf(10, listOf(R.drawable.fish2, R.drawable.fish4)),
                arrayOf(11, listOf(R.drawable.fish1, R.drawable.fish2, R.drawable.fish4)),
                arrayOf(12, listOf(R.drawable.fish3, R.drawable.fish4)),
                arrayOf(13, listOf(R.drawable.fish1, R.drawable.fish3, R.drawable.fish4)),
                arrayOf(14, listOf(R.drawable.fish2, R.drawable.fish3, R.drawable.fish4)),
                arrayOf(15, listOf(R.drawable.fish1, R.drawable.fish2, R.drawable.fish3, R.drawable.fish4))

            )
        }
    }

    @Before
    fun setUp() {
        imgListFromScore = ImageListFromScore()
    }

    @Test
    fun testGetFishList() {
        assertEquals(expectedImages, imgListFromScore.getFishList(input))
    }
}