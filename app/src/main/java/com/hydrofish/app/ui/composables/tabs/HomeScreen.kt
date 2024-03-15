package com.hydrofish.app.ui.composables.tabs

import android.util.Log
import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.AnimationVector1D
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.snap
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.geometry.center
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.RadialGradientShader
import androidx.compose.ui.graphics.Shader
import androidx.compose.ui.graphics.ShaderBrush
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.hydrofish.app.HydroFishViewModel
import com.hydrofish.app.R
import com.hydrofish.app.animations.AnimatableType
import com.hydrofish.app.animations.FishInfo
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch
import kotlin.math.cos
import kotlin.math.sin

/**
 * Composable function that represents the home screen of the application.
 */

// https://medium.com/androiddevelopers/making-jellyfish-move-in-compose-animating-imagevectors-and-applying-agsl-rendereffects-3666596a8888
// // create a custom gradient background that has a radius that is the size of the biggest dimension of the drawing area, this creates a better looking radial gradient in this case.
val largeRadialGradient = object : ShaderBrush() {
    override fun createShader(size: Size): Shader {
        val biggerDimension = maxOf(size.height, size.width)
        return RadialGradientShader(
            colors = listOf(Color(0xFF2be4dc), Color(0xFF243484)),
            center = size.center,
            radius = biggerDimension / 2f,
            colorStops = listOf(0f, 0.95f)
        )
    }
}

@Composable
fun HomeScreen(modifier: Modifier = Modifier, hydroFishViewModel: HydroFishViewModel = viewModel()) {
    //This approach ensures that whenever there is a change in the uiState value,
    //recomposition occurs for the composables using the hydroFishUiState value.
    val hydroFishUIState by hydroFishViewModel.uiState.collectAsState()
    val waterPercent = (hydroFishUIState.dailyWaterConsumedML * 1f) / (hydroFishUIState.curDailyMaxWaterConsumedML * 1f)

    Box(modifier = modifier.background(largeRadialGradient),
        contentAlignment = Alignment.Center,
        ) {
        AddFishAnimation(hydroFishViewModel)
        Log.d("HydroFishViewModel", "HomeScreen fish score: ${hydroFishUIState.fishScore}")
        Log.d("HydroFishViewModel", "HomeScreen presentedFish size: ${hydroFishUIState.presentedFish.size}")

        AddProgessBar(waterPercent, modifier.align(Alignment.CenterEnd))

        AddButtons(Modifier.align(Alignment.BottomStart))
    }
}

@Composable
fun AddFishAnimation(hydroFishViewModel: HydroFishViewModel = viewModel()) {
    // stores the animatables in a map
    val animatableMap = mutableMapOf<AnimatableType, Animatable<Float, AnimationVector1D>>()
    AnimatableType.values().forEach { animatableVal ->
        animatableMap[animatableVal] = remember { Animatable(0f) }
    }

    // should contain respective animations for each animatable
    suspend fun animate() {
        coroutineScope {
            launch {
                while (true) {
                    // Move to the right (400f) and then flip
                    animatableMap[AnimatableType.X]?.animateTo(
                        targetValue = 400f,
                        animationSpec = tween(
                            durationMillis = 3000, // Duration of moving to one side
                            easing = LinearEasing
                        )
                    )
                    animatableMap[AnimatableType.FLIP]?.animateTo(
                        targetValue = 180f, // Flip after reaching the target
                        animationSpec = tween(durationMillis = 200) // Quick flip
                    )

                    // Move back to the left (-400f) and then flip back
                    animatableMap[AnimatableType.X]?.animateTo(
                        targetValue = -400f,
                        animationSpec = tween(
                            durationMillis = 3000, // Duration of moving back
                            easing = LinearEasing
                        )
                    )
                    animatableMap[AnimatableType.FLIP]?.animateTo(
                        targetValue = 0f, // Flip back after reaching the start
                        animationSpec = tween(durationMillis = 200) // Quick flip back
                    )
                }
            }

            launch {
                var targetY = 400f
                while (true) {
                    animatableMap[AnimatableType.Y]?.animateTo(
                        targetValue = targetY,
                        animationSpec = tween(durationMillis = 3000, easing = LinearEasing)
                    )
                    targetY *= -1 // Switch direction
                }
            }

            launch {
                var cumulativeRotation = 0f
                while (true) {
                    cumulativeRotation += 360f // Add a full rotation
                    animatableMap[AnimatableType.ROTATE]?.animateTo(
                        targetValue = cumulativeRotation,
                        animationSpec = tween(durationMillis = 3000, easing = LinearEasing)
                    )
                }
            }

            // update angles
            launch {
                animatableMap[AnimatableType.CYCLE]?.animateTo(
                    targetValue = 360f,
                    animationSpec = infiniteRepeatable(
                        animation = tween(durationMillis = 3000, easing = LinearEasing),
                        repeatMode = RepeatMode.Restart
                    )
                )
            }

            // update CYCLEX and CYCLEY
            launch {
                val cycleAnim = animatableMap[AnimatableType.CYCLE]
                while (isActive) {
                    cycleAnim?.value?.let { cycleValue ->
                        val radians = Math.toRadians(cycleValue.toDouble())
                        animatableMap[AnimatableType.CYCLEX]?.animateTo(
                            targetValue = cos(radians).toFloat() * 200f,
                            animationSpec = snap() // Use snap for immediate update
                        )
                    }

                }
            }

            launch {
                val cycleAnim = animatableMap[AnimatableType.CYCLE]
                while (isActive) {
                    cycleAnim?.value?.let { cycleValue ->
                        val radians = Math.toRadians(cycleValue.toDouble())
                        animatableMap[AnimatableType.CYCLEY]?.animateTo(
                            targetValue = sin(radians).toFloat() * 200f,
                            animationSpec = snap()
                        )
                    }

                }
            }


        }
    }

    LaunchedEffect(key1 = Unit) {
        while (true) {
            animate()
        }
    }

    val uiState by hydroFishViewModel.uiState.collectAsState()
    for (animationGroup in uiState.presentedFish) {
        val fishAnimAndList = animationGroup.getFishListWithAnim()
        for (fishInfo in fishAnimAndList.fishes) {
            AddFish(animatableMap, fishAnimAndList.animatableTypes, fishInfo)
        }
    }
}

// will be repeatedly recomposed based on the animatables, due to the use of .value
@Composable
fun AddFish(
    animatableStorageMap: MutableMap<AnimatableType, Animatable<Float, AnimationVector1D>>,
    animatableActivatedSet: HashSet<AnimatableType>,
    fishInfo: FishInfo
) {
    fun getAnimatableValIfExists(animatable: AnimatableType): Float {
        return if (animatableActivatedSet.contains(animatable))
                (animatableStorageMap[animatable]?.value as Float) else 0f
    }

    Image(
        painter = painterResource(id = fishInfo.fishId),
        contentDescription = "fish",
        modifier = Modifier
            .size(100.dp)
            .graphicsLayer {
                translationX = getAnimatableValIfExists(AnimatableType.X) + getAnimatableValIfExists(AnimatableType.CYCLEX)+ fishInfo.coordinates.x
                translationY = getAnimatableValIfExists(AnimatableType.Y) + getAnimatableValIfExists(AnimatableType.CYCLEY)+ fishInfo.coordinates.y
                rotationZ = getAnimatableValIfExists(AnimatableType.ROTATE)
                rotationY = getAnimatableValIfExists(AnimatableType.FLIP)
            }
    )
}

@Composable
fun AddProgessBar(waterConsumed: Float, modifier: Modifier = Modifier) {
    Box(
        modifier = modifier
            .width(20.dp)
            .fillMaxHeight()
            .background(Color.White)
    ) {
        Box(
            modifier = Modifier
                .fillMaxHeight(waterConsumed)
                .fillMaxWidth()
                .background(MaterialTheme.colorScheme.primary)
                .align(Alignment.BottomStart)
        )
    }
}

@Composable
@Preview
fun AddButtons(modifier: Modifier = Modifier, hydroFishViewModel: HydroFishViewModel = viewModel()) {
    Column (
        modifier = modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.Bottom,
        horizontalAlignment = Alignment.CenterHorizontally){
        Row(horizontalArrangement = Arrangement.Center) {
            ReusableDrinkButton(
                waterAmt=150,
                hydroFishViewModel
            )
            Spacer(modifier = Modifier.size(10.dp))
            ReusableDrinkButton(
                waterAmt=250,
                hydroFishViewModel
            )
            Spacer(modifier = Modifier.size(10.dp))
            ReusableDrinkButton(
                waterAmt=330,
                hydroFishViewModel
            )
        }
        Spacer(modifier = Modifier.size(10.dp))
    }

}

@Composable
fun ReusableDrinkButton(waterAmt: Int, hydroFishViewModel: HydroFishViewModel = viewModel()) {
    val hydroFishUIState by hydroFishViewModel.uiState.collectAsState();

    // upper limit
    val buttonMaxLimit = hydroFishUIState.curDailyMaxWaterConsumedML * 2;
    if (waterAmt > buttonMaxLimit) {
        throw Exception("Button Water Amount Exceeds Max Limit");
    }

    // lower limit
    if (waterAmt < 0) {
        throw Exception ("Button Water Amount Cannot Be Negative");
    }

    // ml to L conversion
    var buttonText = waterAmt.toString() + "ml";
    if (waterAmt >= 1000) {
        buttonText = (waterAmt / (1000 * 1.0)).toString() + "L";
    }



    Button(onClick = {
        hydroFishViewModel.increaseWaterLevel(waterAmt);
    }) {
        Column (
            horizontalAlignment = Alignment.CenterHorizontally,){
            Image(
                painterResource(id = R.drawable.ic_launcher_foreground),
                contentDescription ="add drink button",
                modifier = Modifier.size(40.dp))

            Text(text = buttonText,Modifier.padding(start = 10.dp))
        }
    }
}



