package com.hydrofish.app.animations

import kotlin.math.pow
import kotlin.math.sqrt
import kotlin.random.Random

class Coordinates(val x: Float, val y: Float) {
    private fun randomFloatInRange(start: Float, endInclusive: Float): Float {
        require (start < endInclusive) { "End value must be greater than start value"}
        return Random.nextFloat() * (endInclusive - start) + start
    }

    fun getRandOffsetCoordinates(
        offset: Float
    ): Coordinates {
        val xOff = randomFloatInRange(-offset, offset)
        val posY = sqrt((offset).pow(2f) - xOff.pow(2f))

        // 50/50 chance of y being negative
        val yOff = if (Random.nextBoolean()) -posY else posY

        return Coordinates(x + xOff, y + yOff)
    }
}