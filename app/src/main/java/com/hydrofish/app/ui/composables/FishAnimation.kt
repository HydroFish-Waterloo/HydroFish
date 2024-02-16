package com.hydrofish.app.ui.composables

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.offset
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.graphics.vector.Group
import androidx.compose.ui.graphics.vector.Path
import androidx.compose.ui.graphics.vector.rememberVectorPainter
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.dp


@Composable
fun Fish(x: Float, isRight: Boolean, fishType: FishType, verticalDistance: Float) {
    val fishPainter = rememberVectorPainter(
        defaultWidth = 200.46f.dp,
        defaultHeight = 563.1f.dp,
        viewportHeight = 563.1f,
        viewportWidth = 200.46f,
        autoMirror = true,
    ) { viewPortWidth, viewPortHeight ->
        when (fishType) {
            FishType.FISH_V1 -> {
                Group(
                    name = "fish",//
                    scaleX = 0.3f, // Scale down the fish horizontally
                    scaleY = 0.3f,
                ) {
                    Group("fish1") {
                        Path(
                            pathData = FishPaths.fish_v1,
                            fill = SolidColor(Color.White),
                            fillAlpha = 1f
                        )
                        Path(
                            pathData = FishPaths.fish_v2,
                            fill = SolidColor(Color.White),
                            fillAlpha = 1f
                        )

                    }

                }
            }
            FishType.FISH_V2 -> {
                Group(name = "shark", scaleX = 0.6f, scaleY = 0.6f) {
                    Path(
                        pathData = SharkPaths.shark_v1,
                        fill = SolidColor(Color.White),
                        fillAlpha = 0.4f
                    )
                    Path(
                        pathData = SharkPaths.shark_v2,
                        fill = SolidColor(Color.White),
                        fillAlpha = 0.4f
                    )
                    Path(
                        pathData = SharkPaths.shark_v3,
                        fill = SolidColor(Color.White),
                        fillAlpha = 1f
                    )
                }
            }
        }
    }

    if (isRight) {
        Image(
            fishPainter,
            contentDescription = "Fish_right",
            modifier = Modifier
                .fillMaxSize()
                .offset(x.dp, y = verticalDistance.dp)
                .graphicsLayer {
                    // Apply a horizontal flip
                    scaleX = -1f
                }
        )
    } else {
        Image(
            fishPainter,
            contentDescription = "Fish_left",
            modifier = Modifier
                .fillMaxSize()
                .offset(x.dp, y = verticalDistance.dp)
        )
    }
}

@Composable
fun FishAnimation(modifier: Modifier, fishType: FishType, verticalDistance: Float, directionInit: Boolean) {

    val screenWidthPx = with(LocalDensity.current) { LocalConfiguration.current.screenWidthDp.dp.toPx() }
    val fishWidthPx = with(LocalDensity.current) { 200.46f.dp.toPx() }

    var isRightFishActive by remember { mutableStateOf(directionInit) }
    var prepareForNextAnimation by remember { mutableStateOf(false) }
    val animatableX = remember { Animatable(-fishWidthPx) }
    val durationMillis = 10000

    LaunchedEffect(key1 = prepareForNextAnimation) {
        if (prepareForNextAnimation) {
            // Change direction
            isRightFishActive = !isRightFishActive
            // Prepare the position for the next animation
            val startPos = if (isRightFishActive) -fishWidthPx*5/3 else screenWidthPx
            animatableX.snapTo(startPos)
            prepareForNextAnimation = false
        }
    }

    LaunchedEffect(key1 = isRightFishActive) {
        val targetValue = if (isRightFishActive) screenWidthPx else -fishWidthPx
        animatableX.animateTo(
            targetValue = targetValue,
            animationSpec = tween(durationMillis = durationMillis, easing = LinearEasing)
        )
        // Signal ready to prepare for the next animation
        prepareForNextAnimation = true
    }

    // Assuming Fish is a previously defined composable that takes these parameters
    Fish(
        x = animatableX.value / LocalDensity.current.density, // Convert back to dp for positioning
        isRight = isRightFishActive,
        fishType = fishType,
        verticalDistance = verticalDistance
    )

}
