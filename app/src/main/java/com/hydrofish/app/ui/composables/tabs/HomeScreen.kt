package com.hydrofish.app.ui.composables.tabs

import android.util.Log
import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.EaseInBounce
import androidx.compose.animation.core.EaseInCirc
import androidx.compose.animation.core.EaseInElastic
import androidx.compose.animation.core.EaseInOut
import androidx.compose.animation.core.EaseInOutElastic
import androidx.compose.animation.core.EaseInOutSine
import androidx.compose.animation.core.EaseOutBounce
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.offset
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
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.geometry.center
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.PathFillType
import androidx.compose.ui.graphics.RadialGradientShader
import androidx.compose.ui.graphics.Shader
import androidx.compose.ui.graphics.ShaderBrush
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.graphics.vector.Group
import androidx.compose.ui.graphics.vector.Path
import androidx.compose.ui.graphics.vector.PathParser
import androidx.compose.ui.graphics.vector.rememberVectorPainter
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.core.graphics.translationMatrix
import androidx.lifecycle.viewmodel.compose.viewModel
import com.hydrofish.app.HydroFishViewModel
import com.hydrofish.app.R
import com.hydrofish.app.ui.composables.FishAnimation
import com.hydrofish.app.ui.composables.FishType
import com.hydrofish.app.ui.composables.tabs.JellyFishPaths.squarePath
import com.hydrofish.app.ui.composables.tabs.JellyFishPaths.squarePath2
import com.hydrofish.app.ui.composables.tabs.JellyFishPaths.tentaclePath
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
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
        JellyfishAnimation()
//        DisplayFish(modifier = Modifier, fishes = hydroFishUIState.fishTypeList, distances = hydroFishUIState.fishDistances)

        AddProgessBar(waterPercent, modifier.align(Alignment.CenterEnd))

        AddButtons(Modifier.align(Alignment.BottomStart))
    }
}


object JellyFishPaths {
    val tentaclePath = PathParser().parsePathString(
        "M254.44 253.48c-8.78 25.12-15.81 53.28 2.61 77.35 11.06 14.46 42.58 51.91 38.16 69.8-3.91 15.83-20.78 24.73-14.17 28.18 8.22 3.21 22.2-26 18.7-41.25-3.89-16.94-17.68-35.47-34.38-54.78-2.48-2.86-13.15-18.09-14-35.58-1-19.92 12.84-42.61 12.38-42.62-3.13-.02-8.1-4.53-9.3-1.1z"
    ).toNodes()

    val squarePath = PathParser().parsePathString(
        "M327.1 96c-89.97 0-168.54 54.77-212.27 101.63L27.5 131.58c-12.13-9.18-30.24.6-27.14 14.66L24.54 256 .35 365.77c-3.1 14.06 15.01 23.83 27.14 14.66l87.33-66.05C158.55 361.23 237.13 416 327.1 416 464.56 416 576 288 576 256S464.56 96 327.1 96zm87.43 184c-13.25 0-24-10.75-24-24 0-13.26 10.75-24 24-24 13.26 0 24 10.74 24 24 0 13.25-10.75 24-24 24z"

    ).toNodes()

    val squarePath2 = PathParser().parsePathString(
        "M23.4148 4.39996C21.2815 6.9333 18.7481 10.1333 17.6815 11.7333C16.6148 13.2 15.1481 14.1333 14.4815 13.7333C13.8148 13.3333 13.4148 13.4666 13.8148 14.1333C17.6815 22.1333 19.2815 26.9333 19.6815 31.3333C20.2148 36.4 20.0815 36.4 18.4815 30C17.4148 26.2666 15.2815 20.6666 13.6815 17.4666C12.0815 14.4 11.2815 12.1333 12.0815 12.6666C12.8815 13.0666 13.5481 12.9333 13.5481 12.2666C13.5481 10.8 9.54814 7.19996 8.34814 7.46663C7.94814 7.59996 6.74814 7.3333 5.94814 7.06663C5.01481 6.66663 3.14814 6.79996 1.94814 7.3333C-0.185191 8.1333 -0.185191 8.39996 2.61481 10.6666C4.34814 12 5.68148 13.7333 5.68148 14.2666C5.68148 17.2 11.2815 34.8 14.2148 40.6666C15.9481 44.2666 19.8148 54.8 22.8815 64C33.9481 97.0666 49.1481 123.6 67.9481 142.667L78.4815 153.333H73.9481C69.4148 153.333 64.3481 157.2 64.2148 160.933C64.2148 161.733 62.7481 165.333 61.0148 168.8C59.1481 172.4 58.0815 175.733 58.3481 176.267C58.7481 176.8 61.2815 177.2 63.9481 177.2L68.8815 177.067L64.8815 175.867C61.5481 174.933 61.9481 174.8 66.8815 175.333C70.2148 175.6 73.8148 175.333 74.8815 174.667C76.6148 173.467 76.6148 173.333 74.8815 173.333C73.1481 173.333 73.1481 173.2 74.8815 172C76.3481 170.933 76.0815 170.8 73.5481 171.333C71.6815 171.733 68.4815 171.467 66.2148 170.8L62.2148 169.6L66.2148 169.2C68.4815 169.067 71.8148 168.8 73.6815 168.667C75.6815 168.533 78.3481 167.867 79.6815 167.333C81.1481 166.667 83.4148 166.133 84.7481 165.867C86.8815 165.733 86.8815 166 84.0815 168.133C82.3481 169.467 80.2148 170.4 79.4148 170.133C78.6148 169.867 77.9481 170.4 77.9481 171.467C77.9481 174.133 78.3481 173.867 85.6815 168.667L92.0815 164.133L98.2148 170C101.548 173.333 105.148 176 106.215 176C107.148 176 110.748 177.2 114.215 178.667C119.948 181.067 120.481 181.733 119.681 185.067C119.015 188.133 119.148 188.267 120.348 186C121.415 183.867 123.015 183.333 127.148 183.333C131.415 183.333 133.148 182.533 135.681 179.6C137.415 177.467 138.881 174.8 138.881 173.333C138.881 171.467 139.681 171.067 141.948 171.6C144.748 172.133 144.748 172.133 141.681 170.267C138.215 168.267 134.881 159.733 134.881 152.667C134.881 150.133 133.948 148 132.215 146.933C130.748 146.133 128.481 143.067 127.015 140.267L124.481 135.333L127.815 131.333C131.948 126.267 136.615 117.333 135.681 116.267C135.281 115.867 134.881 116.4 134.881 117.333C134.881 118.267 134.615 118.8 134.215 118.4C133.681 118 133.815 116 134.348 113.867C135.148 110.533 135.415 110.4 136.215 112.667C137.015 114.8 137.281 114.133 137.815 110.267C138.748 102.4 137.415 101.733 129.148 105.733C125.148 107.733 121.281 109.333 120.481 109.333C117.281 109.333 113.548 114 113.548 117.867C113.548 121.6 112.881 121.067 101.415 108.8C88.3481 95.0666 73.8148 82.9333 57.5481 72.6666C41.6815 62.6666 33.6815 56 30.0815 49.8666C27.1481 44.8 26.8815 43.6 28.0815 39.0666C29.8148 32.9333 30.0815 -3.62302e-05 28.3481 -3.62302e-05C27.6815 -3.62302e-05 25.4148 1.99996 23.4148 4.39996ZM27.8148 27.6C27.5481 31.8666 27.4148 28.6666 27.4148 20.6666C27.4148 12.5333 27.5481 9.06663 27.8148 12.9333C28.0815 16.8 28.0815 23.3333 27.8148 27.6ZM11.6815 16.8C14.3481 21.3333 15.2815 24.9333 15.5481 31.4666C15.6815 36.1333 15.5481 40 15.4148 40C15.1481 40 13.6815 35.6 12.0815 30.2666C10.4815 25.0666 8.34814 18.9333 7.28148 16.8C6.21481 14.8 5.94814 13.4666 6.74814 13.8666C8.74814 15.2 8.61481 12.9333 6.61481 10.8C5.28148 9.3333 5.28148 9.19996 6.48148 9.86663C7.41481 10.4 9.81481 13.4666 11.6815 16.8ZM25.4148 27.8666C25.5481 43.6 24.7481 40.8 23.9481 23.3333C23.5481 15.2 23.6815 10.4 24.2148 12.6666C24.7481 14.8 25.2815 21.7333 25.4148 27.8666ZM22.6148 28C22.6148 32.5333 22.6148 32.5333 21.4148 26.8C20.8815 23.7333 19.9481 19.7333 19.5481 18.1333C19.2815 16.5333 19.8148 17.0666 20.8815 19.3333C21.9481 21.4666 22.7481 25.4666 22.6148 28ZM38.6148 65.8666C37.6815 66.9333 36.6148 67.4666 36.3481 67.3333C35.6815 66.5333 37.6815 64 39.0148 64C39.6815 64 39.5481 64.8 38.6148 65.8666ZM44.2148 72.1333C44.2148 72.8 43.6815 73.3333 42.8815 73.3333C42.2148 73.3333 41.5481 72.4 41.5481 71.2C41.5481 70.1333 42.2148 69.6 42.8815 70C43.6815 70.4 44.2148 71.3333 44.2148 72.1333ZM47.9481 71.2C47.0148 72.2666 45.9481 72.8 45.6815 72.6666C45.0148 71.8666 47.0148 69.3333 48.3481 69.3333C49.0148 69.3333 48.8815 70.1333 47.9481 71.2ZM50.6148 74.6666C48.7481 77.6 47.4148 77.2 48.6148 74C49.1481 72.9333 50.0815 72 50.8815 72C51.6815 72 51.6815 72.9333 50.6148 74.6666ZM56.6148 74.2666C55.6815 74.5333 54.8815 75.7333 54.8815 76.6666C54.8815 77.6 53.4148 79.3333 51.5481 80.5333C49.6815 81.7333 48.2148 82.4 48.2148 82C48.2148 80.5333 55.5481 73.4666 56.8815 73.4666C57.6815 73.4666 57.4148 73.8666 56.6148 74.2666ZM44.7481 76.6666C44.2148 77.7333 43.6815 78.6666 43.4148 78.6666C43.1481 78.6666 42.8815 77.7333 42.8815 76.6666C42.8815 75.6 43.4148 74.6666 44.2148 74.6666C44.8815 74.6666 45.1481 75.6 44.7481 76.6666ZM36.2148 80.5333C36.2148 80.8 35.0148 81.3333 33.5481 81.7333C32.0815 82.1333 31.1481 81.8666 31.6815 81.2C32.3481 80 36.2148 79.4666 36.2148 80.5333ZM38.0815 84.5333C35.2815 86.5333 34.0815 86.4 35.5481 84.1333C35.9481 83.3333 37.4148 82.6666 38.6148 82.6666C40.3481 82.6666 40.3481 82.9333 38.0815 84.5333ZM71.6815 84.2666C72.2148 85.0666 72.0815 85.3333 71.1481 84.8C70.3481 84.4 69.1481 85.0666 68.3481 86.2666C67.1481 88.4 67.0148 88.4 66.8815 86.4C66.8815 85.2 67.6815 83.8666 68.6148 83.6C69.4148 83.2 70.3481 82.8 70.4815 82.8C70.6148 82.6666 71.1481 83.3333 71.6815 84.2666ZM65.1481 90.5333C67.6815 93.8666 67.1481 94.9333 63.2815 94.4C59.6815 94 55.5481 88.8 57.0148 86.4C57.8148 85.0666 63.0148 87.7333 65.1481 90.5333ZM41.2815 92.5333C40.3481 93.6 39.2815 94.1333 39.0148 94C38.3481 93.2 40.3481 90.6666 41.6815 90.6666C42.3481 90.6666 42.2148 91.4666 41.2815 92.5333ZM73.2815 92.2666C72.0815 93.2 70.8815 94.9333 70.4815 96C69.9481 97.3333 69.6815 97.0666 69.6815 95.2C69.5481 92 70.6148 90.6666 73.4148 90.6666C75.1481 90.6666 75.1481 90.9333 73.2815 92.2666ZM78.6148 95.7333C77.2815 96 76.2148 96.8 76.2148 97.6C76.2148 98.4 75.8148 98.6666 75.4148 98.2666C73.9481 96.9333 77.6815 93.2 79.4148 94.2666C80.3481 94.9333 80.0815 95.4666 78.6148 95.7333ZM45.2815 96.2666C47.4148 98.4 47.2815 100 44.8815 100C43.8148 100 42.8815 99.7333 42.8815 99.4666C42.8815 99.2 42.4815 98 42.0815 96.8C41.1481 94.2666 43.0148 94 45.2815 96.2666ZM91.5481 102.667C92.8815 102.667 93.4148 103.867 93.2815 106C93.0148 108.533 92.4815 109.2 90.8815 108.533C89.8148 108 87.5481 107.733 85.9481 107.867C82.3481 108 78.2148 102.4 79.4148 99.2C80.0815 97.3333 80.7481 97.3333 85.0148 99.8666C87.5481 101.467 90.6148 102.667 91.5481 102.667ZM50.3481 100.667C49.2815 101.733 47.9481 102.667 47.4148 102.667C47.0148 102.667 47.2815 101.733 48.2148 100.667C49.1481 99.6 50.4815 98.6666 51.1481 98.6666C51.8148 98.6666 51.5481 99.6 50.3481 100.667ZM70.8815 100.667C69.9481 101.733 68.6148 102.667 67.9481 102.667C67.1481 102.667 67.2815 101.733 68.2148 100.667C69.1481 99.6 70.4815 98.6666 71.1481 98.6666C71.9481 98.6666 71.8148 99.6 70.8815 100.667ZM56.8815 102.667C57.2815 103.333 57.0148 104 56.3481 104C55.5481 104 54.8815 103.333 54.8815 102.667C54.8815 101.867 55.1481 101.333 55.4148 101.333C55.8148 101.333 56.4815 101.867 56.8815 102.667ZM75.9481 103.2C75.0148 104.267 73.9481 104.8 73.6815 104.667C73.0148 103.867 75.0148 101.333 76.3481 101.333C77.0148 101.333 76.8815 102.133 75.9481 103.2ZM44.2148 104.667C43.2815 105.867 42.2148 106.4 41.9481 106.133C41.0148 105.2 42.8815 102.667 44.4815 102.667C45.2815 102.667 45.2815 103.467 44.2148 104.667ZM50.8815 106.267C49.8148 106.667 48.4815 107.733 47.9481 108.533C47.2815 109.467 46.8815 109.067 46.8815 107.6C46.8815 106.133 47.8148 105.333 49.9481 105.467C51.9481 105.467 52.2148 105.733 50.8815 106.267ZM97.5481 108.667C96.6148 109.867 95.5481 110.4 95.2815 110.133C94.3481 109.2 96.2148 106.667 97.8148 106.667C98.6148 106.667 98.6148 107.467 97.5481 108.667ZM57.0148 113.733C57.1481 118.8 55.8148 120.267 54.3481 116.533C53.9481 115.333 53.5481 112.933 53.5481 111.067C53.5481 105.733 56.8815 108.267 57.0148 113.733ZM132.215 111.333C133.015 109.6 133.148 111.067 133.015 115.467C132.481 122.933 131.415 125.867 129.415 124.667C128.748 124.267 128.615 124.667 128.881 125.733C129.281 126.667 128.215 128.667 126.615 130.133C124.215 132.133 123.948 132.133 125.415 130.267C126.348 129.067 126.881 128 126.348 128C125.815 128 126.481 126.133 127.681 123.867C129.148 120.8 129.415 119.333 128.348 118.267C127.281 117.2 126.881 117.733 126.881 120C126.881 121.733 126.481 122.8 126.081 122.267C125.548 121.867 125.681 119.2 126.215 116.4C127.015 112.133 127.415 111.733 128.215 114C129.148 116.133 129.415 115.867 129.681 112C129.815 108.133 130.081 107.867 130.615 110.667C131.148 113.733 131.281 113.867 132.215 111.333ZM50.8815 111.2C50.8815 111.6 50.3481 112.267 49.5481 112.667C48.8815 113.067 48.2148 112.8 48.2148 112.133C48.2148 111.333 48.8815 110.667 49.5481 110.667C50.3481 110.667 50.8815 110.933 50.8815 111.2ZM86.3481 112.667C85.2815 113.733 83.9481 114.667 83.4148 114.667C83.0148 114.667 83.2815 113.733 84.2148 112.667C85.1481 111.6 86.4815 110.667 87.1481 110.667C87.8148 110.667 87.5481 111.6 86.3481 112.667ZM92.2148 112.667C91.2815 113.867 90.2148 114.4 89.9481 114.133C89.0148 113.2 90.8815 110.667 92.4815 110.667C93.2815 110.667 93.2815 111.467 92.2148 112.667ZM124.215 120.133C124.481 122.667 123.948 126.133 123.281 128C122.081 131.067 121.815 131.067 121.148 128.667C120.081 125.067 118.748 110.933 119.548 112.8C119.948 113.467 120.881 116.8 121.548 120.133C122.748 125.867 122.748 125.867 122.348 118C121.948 112.933 122.215 111.067 122.881 112.8C123.548 114.267 124.081 117.6 124.215 120.133ZM98.7481 112.4C98.4815 112.533 97.1481 113.6 95.8148 114.533C93.8148 116.133 93.5481 116.133 94.3481 114.133C94.7481 112.933 96.0815 112 97.2815 112C98.3481 112 99.0148 112.133 98.7481 112.4ZM102.215 116C100.881 117.467 99.4148 118.267 99.0148 117.867C98.6148 117.467 99.4148 116.267 100.748 115.2C104.481 112.533 105.015 112.933 102.215 116ZM118.081 123.333C118.615 127.867 118.481 129.333 117.548 128C116.348 126.133 115.548 114.8 116.748 116.133C117.015 116.4 117.548 119.6 118.081 123.333ZM63.9481 123.733C64.7481 126.133 61.9481 125.6 61.0148 123.2C60.4815 121.867 60.8815 121.333 61.8148 121.6C62.6148 121.867 63.6815 122.933 63.9481 123.733ZM71.2815 126.4C68.6148 129.2 67.5481 129.067 68.6148 126C69.1481 124.933 70.3481 124 71.5481 124C73.2815 124 73.1481 124.4 71.2815 126.4ZM80.0815 124.4C79.8148 124.533 78.3481 125.6 76.8815 126.667C74.3481 128.533 74.3481 128.533 76.0815 126.267C77.0148 125.067 78.4815 124 79.2815 124C80.0815 124 80.3481 124.133 80.0815 124.4ZM108.881 129.333C109.415 130.133 109.948 133.867 110.215 137.733C110.748 143.6 110.348 145.067 108.081 146.4C106.615 147.333 104.881 147.733 104.215 147.333C101.548 145.6 94.8815 134.267 94.8815 131.2C94.8815 128.267 95.4148 128 101.415 128C105.148 128 108.481 128.667 108.881 129.333ZM65.2815 132.267C66.2148 133.2 66.8815 134.4 66.8815 134.933C66.8815 136.933 64.8815 136 63.1481 133.333C61.4148 130.4 62.7481 129.733 65.2815 132.267ZM80.6148 133.067C79.2815 134.4 77.9481 135.333 77.6815 135.2C76.8815 134.533 80.3481 130.667 81.6815 130.667C82.3481 130.667 81.8148 131.733 80.6148 133.067ZM89.5481 132C86.8815 133.733 85.1481 133.733 86.2148 132C86.6148 131.2 88.0815 130.667 89.2815 130.667C91.2815 130.667 91.2815 130.933 89.5481 132ZM72.7481 133.867C69.9481 135.867 68.7481 135.733 70.2148 133.467C70.6148 132.667 72.0815 132 73.2815 132C75.0148 132 75.0148 132.267 72.7481 133.867ZM80.7481 139.2C77.9481 141.2 76.7481 141.067 78.2148 138.8C78.6148 138 80.0815 137.333 81.2815 137.333C83.0148 137.333 83.0148 137.6 80.7481 139.2ZM86.2148 139.867C85.8148 140.667 84.7481 141.333 83.9481 141.333C83.1481 141.333 83.1481 140.533 84.2148 139.333C86.0815 137.067 87.6815 137.6 86.2148 139.867ZM73.2815 141.333C71.4148 144.267 70.0815 143.867 71.2815 140.667C71.8148 139.6 72.7481 138.667 73.5481 138.667C74.3481 138.667 74.3481 139.6 73.2815 141.333ZM94.6148 144.4C94.8815 145.733 94.2148 146.667 93.0148 146.667C91.6815 146.667 90.8815 145.467 90.8815 143.867C90.8815 140.667 93.9481 141.067 94.6148 144.4ZM79.2815 146.4C77.9481 147.733 76.6148 148.667 76.3481 148.533C75.5481 147.867 79.0148 144 80.3481 144C81.0148 144 80.4815 145.067 79.2815 146.4ZM118.348 152.4C118.481 158.4 117.148 159.6 113.015 156.8C109.148 154.267 107.681 148.4 110.748 147.467C116.215 146 118.081 147.2 118.348 152.4ZM105.148 151.467C105.948 154 103.415 155.733 99.8148 155.067C96.4815 154.4 94.8815 151.067 97.0148 149.067C98.0815 148 104.615 149.867 105.148 151.467ZM82.8815 157.333C84.2148 158.267 83.0148 158.4 78.8815 157.867C71.4148 156.8 70.3481 156 76.3481 156C78.8815 156 81.8148 156.667 82.8815 157.333ZM78.2148 159.867L83.5481 161.067H78.8815C76.3481 161.067 72.7481 160.533 70.8815 160C68.2148 159.2 68.0815 158.933 70.2148 158.933C71.6815 158.8 75.2815 159.333 78.2148 159.867ZM84.0815 163.467C85.1481 164.533 72.8815 163.733 69.5481 162.667C67.4148 161.867 69.4148 161.733 74.8815 162.267C79.6815 162.667 83.8148 163.2 84.0815 163.467ZM75.9481 166.267C73.8148 166.533 70.6148 166.533 68.6148 166.267C66.4815 166 68.2148 165.733 72.2148 165.733C76.2148 165.733 77.9481 166 75.9481 166.267ZM67.2815 172.933C66.3481 173.2 64.7481 173.2 63.9481 172.933C63.0148 172.533 63.6815 172.267 65.5481 172.267C67.4148 172.267 68.0815 172.533 67.2815 172.933ZM73.1481 174.133C72.7481 174.533 71.5481 174.667 70.6148 174.267C69.5481 173.867 69.9481 173.467 71.4148 173.467C72.8815 173.333 73.6815 173.733 73.1481 174.133Z"
    ).toNodes()
}

//- animation with svg / grouping images
//- max 4-5 animations (running at once)
//- on adding new fish, we append relevant animation
//- group fish with same animation: regenerate randomly

@Preview
@Composable
fun JellyfishAnimation(modifier: Modifier = Modifier) {
    // animated in coroutine (we do this for everything)
    val animatableX = remember { Animatable(0f) }
    val animatableY = remember { Animatable(0f) }
    val animatableRotation = remember { Animatable(0f) }
    val animatableFlip = remember { Animatable(0f) }

    suspend fun animate() {
        coroutineScope {
            launch {
                animatableX.animateTo(
                    targetValue = 100f,
                    animationSpec = tween(
                        durationMillis = 3000,
                        easing = EaseInOutElastic
                    )
                )
                animatableX.animateTo(
                    targetValue = -100f,
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
                    targetValue = 100f,
                    animationSpec = tween(durationMillis = 3000, easing = EaseInOut)
                )
                animatableY.animateTo(
                    targetValue = -100f,
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

    Image(
        painter = painterResource(id = R.drawable.fish),
        contentDescription = "Jellyfish",
        modifier = modifier.offset(
            x = animatableX.value.dp,
            y = 0f.dp
        )
            .graphicsLayer {
                rotationY = animatableFlip.value
            }
    )

    Image(
        painter = painterResource(id = R.drawable.fish),
        contentDescription = "fish2",
        modifier = modifier.offset(
            x = 0f.dp,
            y = animatableY.value.dp
        ).rotate(animatableRotation.value / LocalDensity.current.density)
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



