package com.dawinder.btnjc.ui.composables.tabs

import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.animateIntAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.geometry.center
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.graphics.RadialGradientShader
import androidx.compose.ui.graphics.Shader
import androidx.compose.ui.graphics.ShaderBrush
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.dawinder.btnjc.HydroFishViewModel
import com.dawinder.btnjc.R
import com.dawinder.btnjc.ui.theme.md_theme_light_error
import com.dawinder.btnjc.ui.theme.typography

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
fun HomeScreen(modifier: Modifier, hydroFishViewModel: HydroFishViewModel = viewModel()) {
    //This approach ensures that whenever there is a change in the uiState value,
    //recomposition occurs for the composables using the hydroFishUiState value.
    val hydroFishUIState by hydroFishViewModel.uiState.collectAsState()
    val waterPercent = hydroFishUIState.dailyWaterConsumedML / hydroFishUIState.curDailyMaxWaterConsumedML

    Box(modifier = modifier
        .background(largeRadialGradient),
        contentAlignment = Alignment.Center,

    ) {
        AddProgessBar(modifier.align(Alignment.CenterEnd), waterPercent)

        // create the fish
        AddFish(Modifier)

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
@Composable
fun AddFish(modifier: Modifier) {
    Image(
        painter = painterResource(id = R.drawable.fish),
        contentDescription = "an aquarium fish",
        modifier = modifier
            .width(80.dp)
            .height(80.dp)
    )
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
