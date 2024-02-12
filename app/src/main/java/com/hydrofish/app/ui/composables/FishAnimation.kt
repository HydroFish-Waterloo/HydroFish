package com.hydrofish.app.ui.composables

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.offset
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.graphics.vector.Group
import androidx.compose.ui.graphics.vector.Path
import androidx.compose.ui.graphics.vector.rememberVectorPainter
import androidx.compose.ui.layout.Layout
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.dp
import com.hydrofish.app.ui.composables.tabs.largeRadialGradient
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch


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
                            fillAlpha = 0.4f
                        )
                        Path(
                            pathData = FishPaths.fish_v2,
                            fill = SolidColor(Color.White),
                            fillAlpha = 0.4f
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

        /*Group("shark") {
            Path(
                pathData = shark_v1,
                fill = SolidColor(Color.White),
                fillAlpha = 0.4f
            )
            Path(
                pathData = shark_v2,
                fill = SolidColor(Color.White),
                fillAlpha = 0.4f
            )
            Path(
                pathData = shark_v3,
                fill = SolidColor(Color.White),
                fillAlpha = 1f
            )

        }*/

    }

    Image(
        fishPainter,
        contentDescription = "Fish_right",
        modifier = Modifier
            .fillMaxSize()
            .offset(x.dp,y = verticalDistance.dp)
            .graphicsLayer {
                // Apply a horizontal flip
                scaleX = -1f
            }
    )


}

@Composable
fun FishAnimation(modifier: Modifier, fishType: FishType, verticalDistance: Float) {
    // isRightFishActive is used to switch between fishes facing left and fishes facing right
    var isRightFishActive by remember { mutableStateOf(true) }
    val duration = 10000
    val screenWidthDp = LocalConfiguration.current.screenWidthDp.dp // Convert screenWidth to dp
    val screenWidthPx = with(LocalDensity.current) { screenWidthDp.toPx() } // Convert dp to pixels for animation
    val fishWidthPx = with(LocalDensity.current){ 200.46f.dp.toPx()}
    //Log.d("YourTag", "screenWidthPx value: $screenWidthPx")
    //Log.d("YourTag", "fishWidthPx value: $fishWidthPx")

    val transitionR = rememberInfiniteTransition()
    val transitionL = rememberInfiniteTransition()

    val translationR by transitionR.animateFloat(
        initialValue = -fishWidthPx/2,
        targetValue = screenWidthPx-fishWidthPx*5/3,
        animationSpec = infiniteRepeatable(
            tween(duration, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        )
    )
    val translationL by transitionL.animateFloat(
        initialValue = screenWidthPx-fishWidthPx*5/3,
        targetValue = -fishWidthPx/2,
        animationSpec = infiniteRepeatable(
            tween(duration, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        )
    )
    val blinkAlphaAnimation = remember {
        Animatable(1f)
    }
    val blinkScaleAnimation = remember {
        Animatable(1f)
    }

    suspend fun instantBlinkAnimation() {
        val tweenSpec = tween<Float>(150, easing = LinearEasing)
        coroutineScope {
            launch {
                blinkAlphaAnimation.animateTo(0f, animationSpec = tweenSpec)
                blinkAlphaAnimation.animateTo(1f, animationSpec = tweenSpec)
            }
            launch {
                blinkScaleAnimation.animateTo(0.3f, animationSpec = tweenSpec)
                blinkScaleAnimation.animateTo(1f, animationSpec = tweenSpec)
            }
        }
    }


    LaunchedEffect(isRightFishActive) {
        if (isRightFishActive) {
            delay(10000) // Wait for the right-facing fish animation to complete
            isRightFishActive = false
        } else {
            delay(10000) // Wait for the left-facing fish animation to complete
            isRightFishActive = true
        }
    }

    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(largeRadialGradient)
        )
        // Use Layout to position and translate the fish animations
        Layout(
            modifier = Modifier.fillMaxSize(),
            content = {
                Fish(translationR, isRightFishActive, fishType, verticalDistance)
            }
        ) { measurables, constraints ->
            layout(constraints.maxWidth, constraints.maxHeight) {
                val placeable = measurables[0].measure(constraints)
                val x = translationR
                placeable.place(x.toInt(), 0)
            }
        }
    }

}

