package com.hydrofish.app.animations

import com.hydrofish.app.R

class ImageListFromScore {
    companion object {
        private val FishDict = mapOf(
            1 to R.drawable.fish1,
            2 to R.drawable.fish2,
            4 to R.drawable.fish3,
            8 to R.drawable.fish4
        )

        private fun decomposeValue(value: Int): List<Int> {
            val composition = mutableListOf<Int>()
            var remainingValue = value
            var power = 1
            while (remainingValue > 0) {
                if (remainingValue and 1 == 1) {
                    composition.add(power)
                }
                remainingValue = remainingValue ushr 1 // right shift by 1 (equivalent to division by 2)
                power *= 2
            }
            return composition
        }

        fun getFishList(score: Int): List<Int> {
            val decomposedScore = decomposeValue(score)
            return decomposedScore.map { FishDict[it] as Int }
        }
    }
}