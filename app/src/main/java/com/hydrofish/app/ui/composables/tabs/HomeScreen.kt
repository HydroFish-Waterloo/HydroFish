package com.hydrofish.app.ui.composables.tabs

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
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.geometry.center
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.RadialGradientShader
import androidx.compose.ui.graphics.Shader
import androidx.compose.ui.graphics.ShaderBrush
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.hydrofish.app.HydroFishViewModel
import com.hydrofish.app.R
import com.hydrofish.app.ui.composables.FishAnimation
import com.hydrofish.app.ui.composables.FishType

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

// initialize fishList with two fish and distance list
val fishTypeList = mutableListOf(FishType.FISH_V1, FishType.FISH_V1)
var fishDistances = listOf(300f, 500f)
var barFull = true


@Composable
fun HomeScreen(modifier: Modifier, hydroFishViewModel: HydroFishViewModel = viewModel()) {
    //This approach ensures that whenever there is a change in the uiState value,
    //recomposition occurs for the composables using the hydroFishUiState value.
    val hydroFishUIState by hydroFishViewModel.uiState.collectAsState()
    val waterPercent = hydroFishUIState.dailyWaterConsumedML / hydroFishUIState.curDailyMaxWaterConsumedML

    Box(modifier = modifier
        .background(largeRadialGradient),
        contentAlignment = Alignment.Center,

        ) {
        AddFish(modifier = Modifier, barFull)
        DisplayFish(modifier = Modifier, fishes = fishTypeList, distances = fishDistances)

        AddProgessBar(modifier.align(Alignment.CenterEnd), waterPercent)

        AddButtons(Modifier.align(Alignment.BottomStart))
    }
}

@Composable
fun AddProgessBar(modifier: Modifier, waterConsumed: Float) {
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

fun generateSequentialFloatList(size: Int, range: ClosedFloatingPointRange<Float>): List<Float> {
    val step = (range.endInclusive - range.start) / (size - 1)
    return List(size) { index -> range.start + step * index }
}

fun AddFish(modifier: Modifier, barFull: Boolean){
    if (barFull) {
        fishTypeList.add(FishType.FISH_V1)
        fishDistances = generateSequentialFloatList(fishTypeList.size, 100f..200f)
    }
}



@Composable
fun DisplayFish(modifier: Modifier, fishes: List<FishType>, distances: List<Float>) {
    if (fishes.size == distances.size) {
        fishes.forEachIndexed { index, fishType ->
            FishAnimation(
                modifier = Modifier,
                fishType = fishType,
                verticalDistance = distances[index]
            )
        }
    }
}

@Composable
fun AddButtons(modifier: Modifier) {
    Column (
        modifier = modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.Bottom,
        horizontalAlignment = Alignment.CenterHorizontally){
        Row(horizontalArrangement = Arrangement.Center) {
            ReusableDrinkButton(
                waterAmt=150f
            )
            Spacer(modifier = Modifier.size(10.dp))
            ReusableDrinkButton(
                waterAmt=250f
            )
            Spacer(modifier = Modifier.size(10.dp))
            ReusableDrinkButton(
                waterAmt=330f
            )
        }
        Spacer(modifier = Modifier.size(10.dp))
    }

}

@Composable
fun ReusableDrinkButton(waterAmt: Float, hydroFishViewModel: HydroFishViewModel = viewModel()) {
    Button(onClick = {
        hydroFishViewModel.increaseWaterLevel(waterAmt)
    }) {
        Column (
            horizontalAlignment = Alignment.CenterHorizontally,){
            Image(
                painterResource(id = R.drawable.ic_launcher_foreground),
                contentDescription ="add drink button",
                modifier = Modifier.size(40.dp))

            Text(text = waterAmt.toString() + "ml",Modifier.padding(start = 10.dp))
        }
    }
}



