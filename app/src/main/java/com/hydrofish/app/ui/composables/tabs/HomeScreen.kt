package com.hydrofish.app.ui.composables.tabs

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.EaseInOut
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
import androidx.compose.foundation.layout.height
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
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.geometry.center
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.RadialGradientShader
import androidx.compose.ui.graphics.Shader
import androidx.compose.ui.graphics.ShaderBrush
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.graphics.vector.Group
import androidx.compose.ui.graphics.vector.Path
import androidx.compose.ui.graphics.vector.PathParser
import androidx.compose.ui.graphics.vector.rememberVectorPainter
import androidx.compose.ui.layout.Layout
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.hydrofish.app.HydroFishViewModel
import com.hydrofish.app.R
import com.hydrofish.app.ui.composables.tabs.FishPaths.fish_v1
import com.hydrofish.app.ui.composables.tabs.FishPaths.fish_v2
import com.hydrofish.app.ui.composables.tabs.FishPaths.fish_v3
import com.hydrofish.app.ui.composables.tabs.FishPaths.fish_v4
import com.hydrofish.app.ui.composables.tabs.FishPaths.fish_v5
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


        // create the fish
        FishAnimation(modifier)

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


object FishPaths {
    val fish_v1 = PathParser().parsePathString(
        """
        M2,45C2,38.95 15.35,20.28 23,15.78C26.94,13.46 28.99,9.66 33.56,8C37.42,6.6 42.97,4.22 47,4C54.06,3.61 60.61,2 68,2C71.5,2 75,2 78.5,2C82.37,2 85.43,3.8 89,4C97.77,4.49 106.76,13.41 113.78,18C120.75,22.56 126.74,29.19 134,33.22C138.38,35.65 144.84,44.49 147.22,49C148.45,51.31 150.06,54.58 151.56,56.5C153.17,58.57 156,65.62 156,63
      
            """.trimIndent()
    ).toNodes()
    val fish_v2 = PathParser().parsePathString(
        "M2,51C5.97,51.44 8.09,56.57 12,57C16.32,57.48 20.51,60.75 25,61C28.72,61.21 32.39,63 36.22,63C40.89,63 45.56,63 50.22,63C57.12,63 67.71,61.02 74,58.22C77.5,56.67 81.36,56.6 85,54.78C88.21,53.17 91.78,51.93 95,50.22C101.75,46.65 108.41,42.44 115,38.78C121.29,35.29 128.63,31.93 133.78,26.78C138.03,22.53 142.37,18.51 146.22,14.22C147.95,12.3 150.05,10.7 151.78,8.78C153.72,6.61 156.76,4.49 158,2"
    ).toNodes()
    val fish_v3 = PathParser().parsePathString(
        "M2,2C2,12.866 5,23.851 5,35C5,45.286 8.602,57.797 4,67"
    ).toNodes()
    val fish_v4 = PathParser().parsePathString(
        "M2,2C2,10.323 5,18.017 5,26.5C5,33.508 8,40.756 8,48"
    ).toNodes()
    val fish_v5 = PathParser().parsePathString(
        "M2,2C2,3.7633 3.5079,3.754 2,3"
    ).toNodes()
}


object SharkPaths {
    val shark_v1 = PathParser().parsePathString(
        """M2,73C6.22,68.25 8.19,62.37 12.78,57.78C16.99,53.57 22.52,51.99 27.22,48.22C30.57,45.55 37.66,41 42,41C44.52,41 49.39,39.69 51.22,38.22C54.06,35.95 59.38,38.69 61.78,36.78C66.99,32.61 78.36,34 85,34C88.33,34 89.55,36.89 92.22,34.22C94.26,32.19 96.85,29.6 98.22,27C101.01,21.73 108.54,18.97 113.22,15.22C115.41,13.47 118.42,11.59 121,10.22C122.68,9.33 128.06,7.68 129,6.5C129.81,5.49 135.95,3.4 137.5,3.06C140.86,2.31 143.79,2 147.5,2C149.33,2 151.17,2 153,2C156.35,2 153.75,2.57 153,3.5C151.02,5.97 148.04,7.95 146,10.5C141.82,15.73 141.66,21.43 139.94,27.44C139.29,29.75 137.32,33.85 138.06,36.44C138.29,37.25 145.52,39 146.5,39C153.12,39 159.31,42 165.5,42C171.66,42 175.1,47.48 179.5,51C183.31,54.04 187.3,58.57 191.56,61C196.6,63.88 199.06,69.61 203.22,73.78C204.36,74.92 207.8,78.42 208,80C208.29,82.34 210.76,81.14 207.56,82.06C206.47,82.37 205.12,84.18 204,84.78C199.22,87.31 195.09,93.33 190.78,96.78C186,100.6 178.97,101.48 174.22,106.22C168.93,111.51 158.2,115.79 150.56,116C145.05,116.15 139.51,116 134,116C130.69,116 124.52,117.14 121.44,115.78C118.85,114.62 114.84,114.17 112,114C108.83,113.81 105.16,111.19 102,111C99.86,110.87 94.2,108.2 92,107.22C88.83,105.81 85.06,106.31 82,104.78C76.76,102.16 81.81,104.75 82,108C82.2,111.32 85.05,114.44 85.94,117.56C87.53,123.09 92.85,130.28 97.5,134C98.76,135.01 102.9,140 100.5,140C98.4,140 92.32,138.15 91,136.5C89.13,134.16 84.14,133.18 82,130.5C80.14,128.17 77.57,126.57 75.5,124.5C73.49,122.49 69.33,120.77 68,118.44C66.74,116.25 63.47,112.57 61.5,111C55.82,106.46 58.92,98 50.5,98C43.13,98 37.69,95.94 31.5,92.5C29.26,91.25 26.62,89.52 25,87.5C23.13,85.16 19.36,85.2 18,83.5C16.67,81.84 12.36,80.1 10.44,79C7.58,77.36 6.5,74.25 4,73"
      """.trimIndent()
    ).toNodes()
    val shark_v2 = PathParser().parsePathString(
        "M2,37C6.235,36.153 10.478,27.653 13,24.5C14.69,22.388 20.603,15.3 23,15C27.004,14.5 31.173,8.906 35.5,7.944C40.843,6.757 46.497,3 52,3C54.471,3 58.323,3.742 60.222,2.222C61.133,1.493 63.767,2.567 62.444,2.944C61.039,3.346 59.905,4.229 58.556,5C54.31,7.426 51.606,11.243 48.778,14.778C46.235,17.957 43.067,23.514 41.944,27.444C40.844,31.295 36.875,36.311 38.056,40.444C39.217,44.511 43.175,48.73 46.222,51.778C50.016,55.572 52.688,60.36 56,64.5C57.571,66.464 60.766,66.957 62,68.5C62.537,69.171 70.983,68.573 68.778,70.778C66.325,73.23 56.983,74 53.5,74C48.29,74 40.034,75.585 36,72C31.152,67.691 26.886,63.358 23,58.5C19.945,54.681 16.87,50.426 13.222,46.778C10.344,43.9 5.775,40.55 4,37"
    ).toNodes()
    val shark_v3 = PathParser().parsePathString(
        "M17,19C14.349,19 11.558,20 8.5,20C5.117,20 4.962,16.953 3.222,14.778C0.025,10.781 3.069,3 8.5,3C12.504,3 22,-0.883 22,6.5C22,12.803 25.236,18 16,18"
    ).toNodes()
}

object CartoonFishPaths {
    val ctfish_v1 = PathParser().parsePathString(
        "M5.417,22.919C5.345,22.728 5.328,22.59 5.341,22.377C5.39,21.527 5.463,21.505 6.069,20.444C7.57,17.813 9.37,15.27 11.184,12.865C13.669,9.568 16.543,5.004 20.888,4.031C22.782,3.607 24.718,3.355 26.66,3.511C34.392,4.133 39.826,10.09 42.185,17.131C44.437,23.852 42.002,30.855 37.804,36.27C35.414,39.353 32.552,41.796 28.954,43.346C26.156,44.551 22.6,44.261 19.754,43.324C13.03,41.108 5.915,34.932 5.642,27.349C5.362,19.58 11.229,13.896 17.848,10.888C21.973,9.013 26.609,8.817 30.767,10.691C39.465,14.609 40.373,25.47 37.771,33.62C36.485,37.647 33.917,41.95 29.469,42.864C25.178,43.745 20.43,41.097 17.695,37.913C12.804,32.219 9.194,21.809 16.633,16.616C19.835,14.38 24.316,14.42 27.744,16.145C31.644,18.108 33.842,21.8 33.543,26.183C33.233,30.748 30.749,36.115 27.235,39.074C24.014,41.787 20.167,41.482 17.071,38.625C13.705,35.519 13.651,29.824 15.811,26.051C18.258,21.779 26.145,18.276 28.319,24.77C29.322,27.764 28.546,31.213 25.91,33.034C22.923,35.097 20.486,33.383 19.469,30.235C18.387,26.885 19.929,22.492 22.542,20.236C24.501,18.543 27.192,19.219 28.593,21.32C30.993,24.92 24.98,27.325 23.303,23.97C22.425,22.214 23.927,20.482 25.745,20.466C27.804,20.447 28.84,23.678 28.571,25.318C28.277,27.109 27.081,28.537 25.165,28.477C22.854,28.405 21.507,25.721 21.77,23.653C22.256,19.824 27.114,18.741 30.165,20.17C33.786,21.866 34.363,26.583 33.116,29.978C32.055,32.868 29.902,34.873 26.84,35.361C22.382,36.072 18.148,33.703 15.68,30.049C13.534,26.872 13.336,21.483 15.724,18.406C16.822,16.991 18.296,15.395 20.061,14.836C21.857,14.267 23.924,14.339 25.745,14.715C29.464,15.484 32.273,18.041 33.303,21.78C34.29,25.364 32.482,29.226 29.864,31.577C28.682,32.638 27.437,33.481 25.816,33.625C23.65,33.817 21.361,33.29 19.448,32.272C16.999,30.97 14.615,27.821 15.154,24.874C15.767,21.529 19.491,19.587 22.405,18.659C26.272,17.427 30.516,18.693 32.755,22.24C36.357,27.947 31.24,43.283 23.128,37.563C20.255,35.537 19.444,31.933 19.196,28.609C19.013,26.159 19.966,22.183 22.602,21.287C25.896,20.168 28.286,22.157 30.006,24.836C32.058,28.032 31.643,31.692 29.579,34.824C28.247,36.845 26.18,37.767 23.851,36.643C20.83,35.185 21.285,29.862 22,27.278C22.899,24.025 25.131,21.285 28.462,20.312C30.933,19.59 34.275,20.399 35.252,23.061C36.089,25.341 34.363,28.774 32.897,30.449C32.073,31.391 30.881,32.234 29.611,32.398C27.781,32.636 26.432,31.159 25.422,29.841C21.859,25.189 22.382,14.486 30.028,14.486C32.426,14.486 35.832,15.866 36.764,18.242C37.476,20.059 38.004,24.34 35.197,24.836C32.716,25.274 29.996,22.294 28.626,20.586C26.997,18.555 24.833,15.921 24.628,13.204C24.281,8.606 29.582,9.564 32.278,10.893C32.83,11.165 34.529,11.839 34.814,12.503C34.954,12.83 34.207,12.13 33.927,11.912C32.923,11.129 31.917,10.368 30.849,9.672C27.728,7.637 24.74,6.033 20.937,6.994C19.186,7.437 17.538,9.268 16.72,10.806C16.388,11.432 15.664,13.105 16.764,13.456C18.594,14.04 21.025,12.914 22.679,12.207C24.721,11.334 26.758,10.408 28.774,9.475C29.901,8.953 29.172,8.361 28.374,7.876C26.378,6.662 23.958,5.509 21.687,4.962C17.399,3.93 12.282,4.67 9.349,8.232C6.63,11.535 6.112,16.406 6.031,20.509C5.927,25.753 6.924,31.522 10.757,35.356C10.804,35.403 11.223,35.94 11.206,35.591C11.168,34.825 10.922,34.031 10.762,33.286C10.022,29.823 9.417,26.54 9.787,22.985C10.027,20.687 10.415,18.152 11.452,16.052C12.095,14.75 12.199,15.499 12.076,16.501C11.557,20.754 11.133,24.814 11.748,29.074C11.898,30.112 12.012,31.149 12.307,32.158C12.725,33.591 11.985,31.045 11.956,30.898C11.092,26.447 10.946,21.866 11.365,17.355C11.568,15.166 11.955,13.006 12.235,10.827C12.35,9.935 12.514,8.934 12.301,8.04C12.098,7.191 11.255,7.085 10.499,7.213C8.526,7.548 6.878,9.479 5.801,11.035C2.784,15.395 2.994,20.668 3.271,25.756C3.389,27.928 4.191,30.109 4.968,32.119C6.069,34.968 7.269,37.875 8.928,40.454C11.446,44.371 15.711,47.88 20.362,48.816C26.399,50.032 31.145,45.795 35.285,42.042C40.189,37.597 43.958,31.386 43.839,24.595C43.737,18.746 42.336,11.372 37.131,7.837C33.024,5.049 27.451,4.941 22.69,5.324C21.854,5.391 20.38,5.822 19.579,5.472C19.261,5.332 19.845,4.792 19.908,4.738C20.647,4.09 21.638,3.703 22.591,3.511C25.659,2.894 29.041,3.781 31.966,4.628C34.84,5.461 37.623,6.958 40.071,8.659C43.541,11.069 46.359,14.37 46.9,18.697C47.237,21.395 46.434,24.126 45.646,26.681C44.982,28.833 44.17,30.952 43.248,33.006C41.627,36.614 39.627,39.983 36.446,42.415C32.106,45.732 26.304,47.772 20.828,46.609C19.488,46.325 18.147,45.927 16.879,45.405C16.201,45.125 18.279,45.537 18.341,45.547C22.91,46.268 27.58,48.422 32.24,47.223C34.744,46.579 36.863,45.626 38.746,43.827C41.218,41.466 42.843,38.621 44.124,35.482C45.007,33.315 45.795,30.864 45.909,28.505C45.992,26.774 45.533,25.01 45.241,23.313"
            .trimIndent()
    ).toNodes()
    val ctfish_v2 = PathParser().parsePathString(
        "M5.603,24.514C5.204,24.056 5.145,23.498 5.095,22.894C4.904,20.591 5.184,18.33 5.848,16.12C7.193,11.645 9.914,7.753 14.313,5.87C18.132,4.235 23.276,4.714 26.593,7.265C30.344,10.149 32.409,14.795 32.476,19.484C32.53,23.199 31.216,27.487 28.75,30.346C23.653,36.258 14.16,34.96 9.645,29.055C7.181,25.833 6.399,21.144 7.634,17.288C8.952,13.171 12.936,9.207 17.361,8.65C22.063,8.058 26.295,10.7 27.874,15.169C29.436,19.588 27.338,25.615 23.795,28.485C21.633,30.237 18.681,30.272 16.88,28C13.885,24.222 14.033,18.33 16.598,14.359C18.01,12.171 21.439,10.831 23.39,13.153C25.748,15.959 24.822,21.374 22.288,23.779C21.42,24.603 20.136,25.621 18.84,25.353C16.792,24.93 16.181,22.428 16.805,20.708C17.397,19.076 18.884,16.927 20.78,16.704C22.084,16.551 22.926,17.664 22.796,18.838C22.465,21.858 16.164,20.709 18.124,17.336C18.97,15.879 20.948,14.36 22.749,14.886C23.459,15.093 23.692,15.825 23.673,16.497C23.63,17.952 22.809,19.062 21.765,19.992C20.051,21.519 18.234,22.574 15.91,21.985C13.839,21.46 12.807,19.152 13.272,17.176C13.554,15.975 14.521,14.811 15.684,14.377C16.101,14.222 17.014,14.198 16.169,14.67C14.495,15.604 13.005,16.471 11.821,18.042C10.329,20.023 10.916,22.702 11.972,24.778C12.949,26.699 14.79,28.438 16.626,29.526C16.932,29.708 19.941,31.281 20.291,30.732C20.557,30.313 17.42,29.569 17.215,29.526C16.068,29.285 13.861,28.866 12.801,29.686C12.316,30.062 14.927,32.211 15.189,32.456C16.112,33.319 16.897,34.316 17.728,35.264C18.998,36.712 21.208,36.559 22.947,36.418C26.787,36.106 30.013,33.911 32.481,31.034C35.346,27.694 37.473,23.528 36.626,19.027C35.88,15.06 33.909,10.92 31.049,8.028C28.205,5.151 23.958,3.385 19.9,3.492C16.153,3.59 12.226,3.82 9.475,6.615C7.849,8.267 6.801,10.358 5.877,12.456C4.069,16.558 2.885,20.849 3.408,25.358C3.98,30.28 6.355,34.97 10.747,37.543C21.43,43.803 36.159,38.09 39.33,26.041C41.425,18.08 36.089,11.042 29.165,7.693C25.095,5.725 20.578,6.864 18.096,10.75C16.514,13.226 15.703,16.328 15.203,19.191C14.685,22.16 14.717,25.033 15.363,27.976C15.754,29.756 16.641,31.549 16.819,33.361C16.934,34.532 15.606,34.88 14.713,35.037C12.463,35.434 10.134,34.579 8.293,33.313C7.566,32.814 6.915,32.221 6.409,31.495C5.903,30.77 6.03,30.341 6.112,29.517"
            .trimIndent()
    ).toNodes()
    val ctfish_v3 = PathParser().parsePathString(
        "M40.47,2.93C34.19,2.93 27.74,8.45 23.78,12.8C12.89,24.75 9.04,39.48 5.93,54.9C1.42,77.29 1.66,100.83 5.42,123.3C9.82,149.55 20.06,172.42 33.36,195.23C49.56,223.03 68.71,246.17 95.03,264.78C123.26,284.74 153.59,298.39 187.01,307.04C221.38,315.94 257.64,318.05 292.71,311.78C316.47,307.54 340.88,298.37 362.14,287.11C385.71,274.64 403.97,253.68 416.95,230.66C427.94,211.19 432.88,189.17 426.53,167.35C419.97,144.78 406.41,128.87 387.32,115.45C367.01,101.18 344.08,92.27 320.61,84.63C289.99,74.66 258.84,66.48 227.38,59.61C200.9,53.82 174.24,48.47 148.18,40.93C126.96,34.79 106.36,26.5 85.32,19.72C72.28,15.52 59.86,10.46 47.07,5.69C43.84,4.48 37.81,1.6 34.7,4.09"
            .trimIndent()
    ).toNodes()
    val ctfish_v4 = PathParser().parsePathString(
        "M2.45,210.76C11.3,190.12 20.7,170.98 33.92,152.58C60.48,115.62 96.27,86.61 135.54,64.01C185.49,35.26 240.43,11.56 298.19,5.52C337.99,1.36 381.02,1.43 420.09,10.25C452.01,17.47 482.62,31.23 505.43,55.16C530.49,81.45 547.81,112.94 556.46,148.2C564.43,180.71 560.67,215 551.99,246.97C544.25,275.47 529.04,298.91 511.51,322.27M140.16,181.64C140.21,179.15 140.83,176.99 141.63,174.61C145.2,164.04 150,154.3 156.16,145.01C167.65,127.67 182.25,112.56 197.67,98.69C211.47,86.29 228.39,73.58 247.02,69.85M231.1,181.64C231.21,178.61 232.23,176.09 233.38,173.26C236.91,164.52 240.84,155.92 245.84,147.91C253.93,134.95 264.34,123.08 275.73,112.93C283.43,106.06 292.56,99.3 302.72,96.76M328.26,200.62C333.56,190.58 337.95,180.29 344.58,170.98C352.43,159.93 361.5,149.21 371.42,139.95C377.3,134.47 383.77,127.73 391.37,124.6C392.86,123.99 394.52,123.24 396.02,123.99M399.21,230.78C408.8,219 419.63,207.46 432.39,199.1C443.24,191.99 455.36,189.23 466.98,183.95"
            .trimIndent()
    ).toNodes()
    val ctfish_v5 = PathParser().parsePathString(
        "M38.38,29.4C60.24,28.62 89.72,32.38 97.94,56.55C106.67,82.2 95.6,113.49 77.72,132.74M54.23,211.21C76.55,218.93 86.47,246.93 83.64,268.37C81.09,287.75 74.34,308.41 62.6,324.26C59.55,328.38 55.19,332.68 50.4,334.48M26.12,388.77C49.44,412.09 56.3,450.4 34.78,477.27C31.49,481.39 8.65,504.8 1.85,498M135.99,322.35C141.77,322.65 147.36,325.97 151.81,329.37C163.6,338.39 163.9,349.68 160.75,363.37C155.01,388.28 141.83,408.25 122.57,424.54M209.44,406.02C227.54,424.12 224.74,454.29 217.74,476.46C212.55,492.89 202.02,509.11 189.85,521.27C185.62,525.51 179.97,529.32 174.31,531.21M242.65,198.43C251.2,203.44 260.13,208.71 266.78,216.18C276.13,226.66 279.75,241.68 279.52,255.49C279.23,272.87 274.42,294.17 260.54,305.74M127.5,129.06C148.24,129.06 170.07,133.81 177.24,155.38C181.99,169.69 180.01,189.17 171.56,201.85M197.49,2.53C205.6,5.77 214.42,7.34 222.65,10.35C236.96,15.59 247.44,24.44 253.09,38.89C262.19,62.14 255.46,86.9 250.77,110.35M329.26,111.28C349.36,109.79 370.34,120.12 373.32,141.66C376.17,162.34 369.43,183.14 356.01,198.79M409.84,215.22C415.34,217.97 420.97,220.46 426.21,223.71C432.27,227.49 438.97,232.18 442.06,238.9C445.09,245.49 444.87,253.9 444.13,260.91C441.72,283.74 431.07,304.92 414.98,321.02M327.6,316.31C340.69,331.39 342.07,351.66 342.07,370.8C342.07,374.3 342.01,376.99 341.64,380.41C340.87,387.41 335.97,392.58 331.5,397.74C322.31,408.34 312.37,419.03 301.37,427.82C297.89,430.6 298.73,429.99 295.9,429.82M481.37,319.3C487.35,322.83 489.83,330.11 491.46,336.53C494.17,347.2 491.98,358.73 488.84,369.06C483.45,386.8 473.23,404.89 457.81,415.68M387.36,411.38C389.36,420.71 385.9,430.78 383.1,439.58C377.39,457.5 367.97,474.8 357.05,490.06C346,505.51 332.16,519.14 316.46,529.86C309.99,534.28 304.17,537.2 296.43,537.2"
            .trimIndent()
    ).toNodes()
    val ctfish_v6 = PathParser().parsePathString(
        "M283.7,82.6C239,80.3 191.1,96.7 168.3,138.1C136.9,195.3 151.1,268.9 212.1,300.1C247.4,318.1 285.6,327.2 323,309C366.9,287.6 391.4,237.7 397.9,191.3C403,155 387.7,122.9 357.8,102C331.3,83.4 304.2,77.8 272.2,79.8M154.1,171C151.8,173.2 149.1,174.5 146.3,175.8C140.9,178.6 137.1,183.8 133,188.1C122.2,199.7 114.8,213.5 104.9,225.7C98.4,233.6 92.4,241.9 86.1,249.9C81.9,255.2 76.4,262.2 69.8,264.8C64.6,266.9 57.7,265.1 52.8,263.1C45.3,260.1 36.8,253.5 28.4,254.5C19.7,255.5 15.8,261.4 12.1,269C7.8,278 6.1,286.6 7.3,296.5C8.6,306.2 13.9,313.7 22.2,319C26.5,321.7 30.9,323.8 35.7,325.4C39.9,326.9 44.4,327.1 48.7,328.5C50,328.9 51.3,328.9 49.8,329.7C46.3,331.6 42.6,333.4 38.9,335.2C29.5,339.6 22.4,346.2 16.9,355.1C10.4,365.4 4.8,376.6 3.6,388.8C2.9,396.9 4.3,403.5 12.9,405.6C27.4,409.3 41.6,405.2 53.3,396.1C61.4,389.9 69.4,383.3 77.1,376.6C80.7,373.4 84.4,368.4 88.9,366.5M75,392.6C75,411.4 73.2,430.2 74.1,449.1C75.9,486 86.6,523.3 103.5,556C124.7,597.4 160.3,634 194,665.1C245.3,712.3 305.5,748.1 370.6,772.9C443.4,800.5 523.8,802.4 600.7,798.2C675.1,794.1 754.4,769.5 818.1,730.5C914.2,671.6 985.4,581 1048.4,489.3M1060.5,365.9C1062,334 1089.3,301.2 1108.3,278C1144.3,234.2 1194.9,205.4 1249.5,192C1281.7,184 1318.2,183.7 1342.7,210.2C1368.7,238.3 1350.6,281.5 1334.8,309.7C1312,350.5 1279.8,387.6 1245.1,418.7C1225.3,436.5 1202.6,452.5 1177,460.7C1168.9,463.3 1161.3,463.9 1153,463.9C1151.6,463.9 1147.1,464 1145.8,465C1143.2,467.2 1153.2,472.6 1154.7,473.9C1171.4,488.1 1185.7,503.7 1197.7,522.1C1214.6,547.9 1230.4,577.3 1239.6,606.9C1246.1,628 1253,653.8 1249,676C1246.1,691.5 1238.6,702.3 1223.2,707.7C1193.8,717.9 1164.6,708.7 1140.4,691.3C1102.8,664.3 1079.4,625.9 1063.6,583C1049.9,545.9 1047.1,512 1051.3,473M289.3,82.4C293.2,77.5 298.2,74.1 303.5,70.7C320.6,59.7 338.1,48.8 356.4,40C391.1,23.3 428.4,12.7 466.6,7.9C564.2,-4.4 677.4,5.9 766.4,49.7C802.8,67.6 836.6,88.6 867.6,114.9C902.7,144.7 929.8,181.6 954.8,220.1C994.1,280.8 1032.5,347.3 1095.3,386.6M92.6,234.6C91.1,233.5 90.2,232 89.2,230.5C85.6,224.4 82.7,217.9 80.9,211.1C76.1,193.3 79.1,173.1 81.5,155.2C86,121.5 103.9,88.7 133.5,70.9C161.9,53.8 196.7,47.3 229.2,54C242.2,56.7 254.8,60.5 266.8,66.1C275.1,70 282.5,75.6 291,79.1"
            .trimIndent()
    ).toNodes()

}

@Composable
fun FishAnimation(modifier: Modifier) {
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

    val vectorPainterCartoonFish = rememberVectorPainter(
        defaultWidth = 200.46f.dp,
        defaultHeight = 563.1f.dp,
        viewportHeight = 563.1f,
        viewportWidth = 530.46f,
        autoMirror = true,
    ) { viewPortWidth, viewPortHeight ->
        val duration = 3000
        val transitionR = rememberInfiniteTransition()
        val transitionL = rememberInfiniteTransition()
        val translationR by transitionR.animateFloat(
            initialValue = 0f,
            targetValue = 400f,
            animationSpec = infiniteRepeatable(
                tween(duration, easing = EaseInOut),
                repeatMode = RepeatMode.Reverse
            )
        )
        val translationL by transitionL.animateFloat(
            initialValue = 400f,
            targetValue = 0f,
            animationSpec = infiniteRepeatable(
                tween(duration, easing = EaseInOut),
                repeatMode = RepeatMode.Reverse
            )
        )

        Group(
            name = "fish_1", translationX = translationR
        ) {
            Group("fish_1") {
                Path(
                    pathData = fish_v1,
                    fill = SolidColor(Color.White),
                    fillAlpha = 0.49f
                )
                Path(
                    pathData = fish_v2,
                    fill = SolidColor(Color.White),
                    fillAlpha = 0.49f
                )
                Path(
                    pathData = fish_v3,
                    fill = SolidColor(Color.White),
                    fillAlpha = 0.49f
                )
                Path(
                    pathData = fish_v4,
                    fill = SolidColor(Color.White),
                    fillAlpha = 0.49f
                )
                Group(name = "eye",
                    scaleY = blinkScaleAnimation.value,
                    pivotY = 233f
                ){
                    Path(
                        pathData = fish_v5,
                        fill = SolidColor(Color.White),
                        fillAlpha = blinkAlphaAnimation.value
                    )

                }

            }
        }
    }

    @Composable
    fun Fish(x: Float, isRight: Boolean) {
        val fishPainter = rememberVectorPainter(
            defaultWidth = 200.46f.dp,
            defaultHeight = 563.1f.dp,
            viewportHeight = 563.1f,
            viewportWidth = 200.46f,
            autoMirror = true,
        ) { viewPortWidth, viewPortHeight ->
            Group(
                name = "fish",//, translationX = x
                scaleX = 0.6f, // Scale down the fish horizontally (adjust this value as needed)
                scaleY = 0.6f,
            ) {
                Group("fish1") {
                    Path(
                        pathData = fish_v1,
                        fill = SolidColor(Color.White),
                        fillAlpha = 0.4f
                    )
                    Path(
                        pathData = fish_v2,
                        fill = SolidColor(Color.White),
                        fillAlpha = 0.4f
                    )
                    Path(
                        pathData = fish_v3,
                        fill = SolidColor(Color.Black),
                        fillAlpha = 1f
                    )
                    Path(
                        pathData = fish_v4,
                        fill = SolidColor(Color.White),
                        fillAlpha = 1f
                    )
                    Path(
                        pathData = fish_v5,
                        fill = SolidColor(Color.Red),
                        fillAlpha = 1f
                    )
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

        }
        if (isRight) {
            Image(
                fishPainter,
                contentDescription = "Fish_right",
                modifier = Modifier
                    .fillMaxSize()
                    .padding(
                        end =0.dp,
                        top = 300.dp)
                    .offset(x.dp)
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
                    .padding(
                        start = 0.dp,
                        top = 300.dp
                    )
                    .offset(x.dp)
            )

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
                if (isRightFishActive) {
                    Fish(translationR, isRightFishActive)
                } else {
                    Fish(translationL, isRightFishActive)
                }
            }
        ) { measurables, constraints ->
            layout(constraints.maxWidth, constraints.maxHeight) {
                val placeable = measurables[0].measure(constraints)
                val x = if (isRightFishActive) translationR else translationL
                placeable.place(x.toInt(), 0)
            }
        }
    }



}