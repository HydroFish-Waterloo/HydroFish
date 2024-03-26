package com.hydrofish.app.ui.composables.tabs

import android.graphics.PorterDuff
import android.graphics.PorterDuffColorFilter
import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.AnimationVector1D
import androidx.compose.animation.core.EaseInOut
import androidx.compose.animation.core.EaseInOutElastic
import androidx.compose.animation.core.LinearOutSlowInEasing
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
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.geometry.center
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.graphics.RadialGradientShader
import androidx.compose.ui.graphics.Shader
import androidx.compose.ui.graphics.ShaderBrush
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.core.content.ContextCompat
import androidx.core.graphics.drawable.DrawableCompat
import androidx.lifecycle.viewmodel.compose.viewModel
import com.hydrofish.app.HydroFishViewModel
import com.hydrofish.app.R
import com.hydrofish.app.animations.AnimatableType
import com.hydrofish.app.animations.FishInfo
import com.hydrofish.app.utils.IUserSessionRepository
import com.hydrofish.app.viewmodelfactories.HydroFishViewModelFactory
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch


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

@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun HomeScreen(modifier: Modifier = Modifier, userSessionRepository: IUserSessionRepository) {
    //This approach ensures that whenever there is a change in the uiState value,
    //recomposition occurs for the composables using the hydroFishUiState value.
    val hydroFishViewModel: HydroFishViewModel = viewModel(factory = HydroFishViewModelFactory(userSessionRepository))
    val hydroFishUIState by hydroFishViewModel.uiState.collectAsState()
    val waterPercent = (hydroFishUIState.dailyWaterConsumedML * 1f) / (hydroFishUIState.curDailyMaxWaterConsumedML * 1f)
    val score by hydroFishViewModel.scoreLiveData.observeAsState(1)

    LaunchedEffect(score) {
        hydroFishViewModel.updateFishModels()
    }
    LaunchedEffect(Unit) {
        hydroFishViewModel.checkResetWaterIntake()
    }


    Box(
        modifier = modifier.background(largeRadialGradient),
        contentAlignment = Alignment.Center,
    ) {

        AddFishAnimation(hydroFishViewModel)

        AddProgessBar(waterPercent, modifier.align(Alignment.CenterEnd))

        AddButtons(Modifier.align(Alignment.BottomStart))
    }
}

@RequiresApi(Build.VERSION_CODES.O)
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
                animatableMap[AnimatableType.X]?.animateTo(
                    targetValue = 400f,
                    animationSpec = tween(
                        durationMillis = 3000,
                        easing = EaseInOutElastic
                    )
                )
                animatableMap[AnimatableType.X]?.animateTo(
                    targetValue = -400f,
                    animationSpec = tween(
                        durationMillis = 3000,
                        easing = EaseInOutElastic
                    )
                )
            }


            launch {
                delay(2800)
                animatableMap[AnimatableType.FLIP]?.animateTo(
                    targetValue = 180f,
                    animationSpec = tween(durationMillis = 200)
                )
                delay(2800)
                animatableMap[AnimatableType.FLIP]?.animateTo(
                    targetValue = 0f,
                    animationSpec = tween(durationMillis = 200)
                )
            }

            launch {
                delay(2800)
                animatableMap[AnimatableType.DIAG_FLIP]?.animateTo(
                    targetValue = 180f,
                    animationSpec = tween(durationMillis = 200)
                )
                delay(2800)
                animatableMap[AnimatableType.DIAG_FLIP]?.animateTo(
                    targetValue = 0f,
                    animationSpec = tween(durationMillis = 200)
                )
            }

            launch {
                delay(2800)
                animatableMap[AnimatableType.DIAG_FLIP_R]?.animateTo(
                    targetValue = 180f,
                    animationSpec = tween(durationMillis = 200)
                )
                delay(2800)
                animatableMap[AnimatableType.DIAG_FLIP_R]?.animateTo(
                    targetValue = 0f,
                    animationSpec = tween(durationMillis = 200)
                )
            }


            launch {
                animatableMap[AnimatableType.Y]?.animateTo(
                    targetValue = 400f,
                    animationSpec = tween(durationMillis = 3000, easing = EaseInOut)
                )
                animatableMap[AnimatableType.Y]?.animateTo(
                    targetValue = -400f,
                    animationSpec = tween(durationMillis = 3000, easing = EaseInOut)
                )
            }

            launch {
                animatableMap[AnimatableType.ROTATE]?.animateTo(
                    targetValue = 360f,
                    animationSpec = tween(durationMillis = 3000, easing = EaseInOut)
                )
                animatableMap[AnimatableType.ROTATE]?.animateTo(
                    targetValue = 2000f,
                    animationSpec = tween(durationMillis = 3000, easing = EaseInOut)
                )
            }


            // Launch block for diagonal movement using DIAGONAL_X and DIAGONAL_Y
            launch {
                // Diagonal movement towards a target point
                animatableMap[AnimatableType.DIAGONAL_X]?.animateTo(
                    targetValue = 400f, // Target for diagonal X movement
                    animationSpec = tween(durationMillis = 3000, easing = LinearOutSlowInEasing)
                )
                animatableMap[AnimatableType.DIAGONAL_X]?.animateTo(
                    targetValue = -400f, // Reset or move to a new X position
                    animationSpec = tween(durationMillis = 3000, easing = LinearOutSlowInEasing)
                )

            }
            launch {
                animatableMap[AnimatableType.DIAGONAL_Y]?.animateTo(
                targetValue = 400f, // Target for diagonal Y movement
                animationSpec = tween(durationMillis = 3000, easing = LinearOutSlowInEasing)
            )

                // Optionally, animate back to the starting position or another point

                animatableMap[AnimatableType.DIAGONAL_Y]?.animateTo(
                    targetValue = -400f, // Reset or move to a new Y position
                    animationSpec = tween(durationMillis = 3000, easing = LinearOutSlowInEasing)
                )
            }

            launch {
                // Diagonal movement towards a target point
                animatableMap[AnimatableType.DIAGONAL_X_R]?.animateTo(
                    targetValue = 400f, // Target for diagonal X movement
                    animationSpec = tween(durationMillis = 3000, easing = LinearOutSlowInEasing)
                )
                animatableMap[AnimatableType.DIAGONAL_X_R]?.animateTo(
                    targetValue = -400f, // Reset or move to a new X position
                    animationSpec = tween(durationMillis = 3000, easing = LinearOutSlowInEasing)
                )

            }
            launch {
                animatableMap[AnimatableType.DIAGONAL_Y_R]?.animateTo(
                    targetValue = -400f, // Target for diagonal Y movement
                    animationSpec = tween(durationMillis = 3000, easing = LinearOutSlowInEasing)
                )

                // Optionally, animate back to the starting position or another point

                animatableMap[AnimatableType.DIAGONAL_Y_R]?.animateTo(
                    targetValue = 400f, // Reset or move to a new Y position
                    animationSpec = tween(durationMillis = 3000, easing = LinearOutSlowInEasing)
                )
            }




        }
    }

    LaunchedEffect(key1 = Unit) {
        while (true) {
            animate()
        }
    }

    for (animationGroup in hydroFishViewModel.getAllFish()) {
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
                translationX =
                    getAnimatableValIfExists(AnimatableType.X) + getAnimatableValIfExists(
                        AnimatableType.DIAGONAL_X
                    ) + getAnimatableValIfExists(AnimatableType.DIAGONAL_X_R) + fishInfo.coordinates.x
                translationY =
                    getAnimatableValIfExists(AnimatableType.Y) + getAnimatableValIfExists(
                        AnimatableType.DIAGONAL_Y
                    ) + getAnimatableValIfExists(AnimatableType.DIAGONAL_Y_R) + fishInfo.coordinates.y
                rotationZ = getAnimatableValIfExists(AnimatableType.ROTATE)
                rotationY =
                    getAnimatableValIfExists(AnimatableType.FLIP) + getAnimatableValIfExists(
                        AnimatableType.DIAG_FLIP
                    ) + getAnimatableValIfExists(AnimatableType.DIAG_FLIP_R)

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

@RequiresApi(Build.VERSION_CODES.O)
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
                R.drawable.americano,
                hydroFishViewModel
            )
            Spacer(modifier = Modifier.size(10.dp))
            ReusableDrinkButton(
                waterAmt=250,
                R.drawable.soda_can,
                hydroFishViewModel
            )
            Spacer(modifier = Modifier.size(10.dp))
            ReusableDrinkButton(
                waterAmt=330,
                R.drawable.orange_juice,
                hydroFishViewModel
            )
        }
        Spacer(modifier = Modifier.size(10.dp))
    }

}

@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun ReusableDrinkButton(
    waterAmt: Int,
    imageId: Int = R.drawable.ic_launcher_foreground,
    hydroFishViewModel: HydroFishViewModel = viewModel()
) {
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
        hydroFishViewModel.checkResetWaterIntake()
        hydroFishViewModel.increaseWaterLevel(waterAmt)
        val willSurpassLimit = hydroFishUIState.dailyWaterConsumedML + waterAmt >= hydroFishUIState.curDailyMaxWaterConsumedML;
        val hasSurpassedLimit = hydroFishUIState.dailyWaterConsumedML >= hydroFishUIState.curDailyMaxWaterConsumedML;

        if (willSurpassLimit && !hasSurpassedLimit) {
            hydroFishViewModel.levelUp()
        }
    }) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
        ){
            Image(
                painterResource(id = imageId),
                contentDescription ="add drink button",
                modifier = Modifier.size(40.dp),
                colorFilter = ColorFilter.tint(Color.White)
            )

            Text(text = buttonText,Modifier.padding(start = 10.dp))
        }
    }
}



