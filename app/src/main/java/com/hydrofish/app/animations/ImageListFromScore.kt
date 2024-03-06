package com.hydrofish.app.animations

import com.hydrofish.app.R

class ImageListFromScore {
    companion object {
        private val FishList = listOf(
            R.drawable.fish1,
            R.drawable.fish2,
            R.drawable.fish3,
            R.drawable.fish4
        )

        fun getFishList(value: Int): List<Int> {
            val composition = mutableListOf<Int>()
            var remainingValue = value
            var n = 0
            while (remainingValue > 0 && n < FishList.size) {
                if (remainingValue and 1 == 1) {
                    composition.add(FishList[n])
                }
                remainingValue = remainingValue ushr 1 // right shift by 1 (equivalent to division by 2)
                n++
            }
            return composition
        }
    }
}