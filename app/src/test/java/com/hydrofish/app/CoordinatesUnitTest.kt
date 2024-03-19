package com.hydrofish.app

import com.hydrofish.app.animations.Coordinates
import org.junit.Test

class CoordinatesTest {

    // Not sure if it is the right way to test
    @Test
    fun testDistinctCoordinates() {
        val startCoordinates = Coordinates(0f, 0f)
        val offset = 200f
        val numCoordinates = 4
        val generatedCoordinates = mutableSetOf<Coordinates>()

        repeat(numCoordinates) {
            val newCoordinates = startCoordinates.getRandOffsetCoordinates(offset)
            generatedCoordinates.add(newCoordinates)
        }

        // Ensure that all x values are distinct
        val xValues = generatedCoordinates.map { it.x }
        xValues.distinct().size == numCoordinates

        // Ensure that all y values are distinct
        val yValues = generatedCoordinates.map { it.y }
        yValues.distinct().size == numCoordinates
    }
}