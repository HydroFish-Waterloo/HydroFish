package com.hydrofish.app.ui.composables.tabs

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.EaseInOut
import androidx.compose.animation.core.EaseInOutElastic
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
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlin.math.pow
import kotlin.math.sqrt
import kotlin.random.Random

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
        val fishImageList = FishImageGenerator.getFishList(15);
        val animationGroupPosition = listOf(
            AnimationGroupPosition(),
            AnimationGroupPosition()
        )
        val fishPositionList = mutableListOf<Coordinates>()
        val animationsChosen = mutableListOf<Int>()
        for (fish in fishImageList) {
            val animationIdx = Random.nextInt(2)
            animationsChosen.add(animationIdx)
            fishPositionList.add(animationGroupPosition[animationIdx].getNewPosition(fish))
        }

        JellyfishAnimation(fishImageList, fishPositionList, animationsChosen)

        AddProgessBar(waterPercent, modifier.align(Alignment.CenterEnd))

        AddButtons(Modifier.align(Alignment.BottomStart))
    }
}

//- animation with svg / grouping images
//- max 4-5 animations (running at once)
//- on adding new fish, we append relevant animation
//- group fish with same animation: regenerate randomly


@Composable
fun JellyfishAnimation(
    fishImageList: List<Int>,
    fishPositionList: MutableList<Coordinates>,
    animationsChosen: MutableList<Int>,
) {
    // animated in coroutine (we do this for everything)
    val animatableX = remember { Animatable(0f) }
    val animatableFlip = remember { Animatable(0f) }
    val animatableY = remember { Animatable(0f) }
    val animatableRotation = remember { Animatable(0f) }

    suspend fun animate() {
        coroutineScope {
            launch {
                animatableX.animateTo(
                    targetValue = 400f,
                    animationSpec = tween(
                        durationMillis = 3000,
                        easing = EaseInOutElastic
                    )
                )
                animatableX.animateTo(
                    targetValue = -400f,
                    animationSpec = tween(
                        durationMillis = 3000,
                        easing = EaseInOutElastic
                    )
                )
            }

            launch {
                delay(2800)
                animatableFlip.animateTo(
                    targetValue = 180f,
                    animationSpec = tween(durationMillis = 200)
                )
                delay(2800)
                animatableFlip.animateTo(
                    targetValue = 0f,
                    animationSpec = tween(durationMillis = 200)
                )
            }

            launch {
                animatableY.animateTo(
                    targetValue = 400f,
                    animationSpec = tween(durationMillis = 3000, easing = EaseInOut)
                )
                animatableY.animateTo(
                    targetValue = -400f,
                    animationSpec = tween(durationMillis = 3000, easing = EaseInOut)
                )
            }

            launch {
                animatableRotation.animateTo(
                    targetValue = 360f,
                    animationSpec = tween(durationMillis = 3000, easing = EaseInOut)
                )
                animatableRotation.animateTo(
                    targetValue = 2000f,
                    animationSpec = tween(durationMillis = 3000, easing = EaseInOut)
                )
            }
        }
    }

    LaunchedEffect(key1 = Unit) {
        while (true) {
            animate()
        }
    }


    // this causes reloading of animations??
    // when we get the animatable value, this entire composable and its children
    // are just constantly reloading, so we can't choose the fish animation index in here
    val animationList = listOf(
        AnimationParams(xVal = animatableX.value, flipVal = animatableFlip.value),
        AnimationParams(yVal = animatableY.value, rotateVal = animatableRotation.value)
    )

    fishImageList.forEachIndexed { index, fish ->
        AddFish(
            fish,
            params = animationList[animationsChosen[index]],
            offset = fishPositionList[index]
        )
    }

}


data class AnimationParams(
    val xVal: Float = 0f,
    val yVal: Float = 0f,
    val rotateVal: Float = 0f,
    val flipVal: Float = 0f
)

@Composable
fun AddFish(imageId: Int, params: AnimationParams, offset: Coordinates) {
    Image(
        painter = painterResource(id = imageId),
        contentDescription = "fish",
        modifier = Modifier
            .size(100.dp)
            .graphicsLayer {
                translationX = params.xVal + offset.x
                translationY = params.yVal + offset.y
                rotationZ = params.rotateVal
                rotationY = params.flipVal
            }
    )
}

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

data class FishInfo(val coordinates: Coordinates, val fishId: Int)
class AnimationGroupPosition(private val startingPosition: Coordinates = Coordinates(0f, 0f)) {
    private val positionList = mutableListOf<FishInfo>()

    public fun getNewPosition(fishId: Int): Coordinates {
        if (positionList.size == 0) {
            positionList.add(FishInfo(startingPosition, fishId))
            return startingPosition
        }

        // obtain random pair
        val randomElement = positionList.asSequence().shuffled().find { true }
        val newCoordinates = randomElement?.coordinates?.getRandOffsetCoordinates(200f) as Coordinates
        positionList.add(FishInfo(newCoordinates, fishId))
        return newCoordinates
    }
}

class FishImageGenerator {
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



