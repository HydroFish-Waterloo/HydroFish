package com.hydrofish.app.ui.composables.tabs

import android.util.Log
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
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.hydrofish.app.HydroFishViewModel
import com.hydrofish.app.R
import com.hydrofish.app.ui.composables.FishAnimation
import com.hydrofish.app.ui.composables.FishType
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

    Box(modifier = modifier
        .background(largeRadialGradient),
        contentAlignment = Alignment.Center,

        ) {
        DisplayFish(modifier = Modifier, fishes = hydroFishUIState.fishTypeList, distances = hydroFishUIState.fishDistances)

        AddProgessBar(waterPercent, modifier.align(Alignment.CenterEnd))

        AddButtons(Modifier.align(Alignment.BottomStart))
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
fun DisplayFish(modifier: Modifier, fishes: List<FishType>, distances: List<Float>) {
    if (fishes.size == distances.size) {
        fishes.forEachIndexed { index, fishType ->
            val direction: Boolean = Random.nextBoolean()
            FishAnimation(
                modifier = Modifier,
                fishType = fishType,
                verticalDistance = distances[index],
                directionInit = direction
            )
        }
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
    Button(onClick = {
        hydroFishViewModel.increaseWaterLevel(waterAmt);
    }) {
        Column (
            horizontalAlignment = Alignment.CenterHorizontally,){
            Image(
                painterResource(id = R.drawable.ic_launcher_foreground),
                contentDescription ="add drink button",
                modifier = Modifier.size(40.dp))

            var buttonText = waterAmt.toString() + "ml";
            if (waterAmt >= 1000) {
                buttonText = (waterAmt / (1000 * 1.0)).toString() + "L";
            }

            if (waterAmt < 0) {
                buttonText = "0ml";
            }
            Text(text = buttonText,Modifier.padding(start = 10.dp))
        }
    }
}



