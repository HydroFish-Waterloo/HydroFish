package com.hydrofish.app.ui.composables.tabs

import android.content.Context
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.LocalContentColor
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import com.hydrofish.app.R
import com.hydrofish.app.ui.composables.NavigationRoutes
import com.hydrofish.app.utils.IUserSessionRepository
import com.hydrofish.app.viewmodelfactories.AchievementsViewModelFactory

/**
 * Composable function that represents the list screen of the application.
 */

data class Achievement(
    val id: Int,
    val name: String,
    val icon: Int,
    val unlockScore: Int
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AchievementsScreen(
    userSessionRepository: IUserSessionRepository,
    navController: NavHostController
) {
    val achievementsViewModel: AchievementsViewModel = viewModel(factory = AchievementsViewModelFactory(userSessionRepository))
    val isUserLoggedIn by achievementsViewModel.isLoggedIn.observeAsState(false)

    val score by achievementsViewModel.scoreLiveData.observeAsState(1)

    val context = LocalContext.current
    val sharedPreferences = context.getSharedPreferences("YourSharedPreferencesName", Context.MODE_PRIVATE)

    val achievements = listOf(
        Achievement(1, "First check-in", R.drawable.achievement, 2),
        Achievement(2, "Check-In 3 days", R.drawable.achievement, 4),
        Achievement(3, "Check-In 7 days", R.drawable.achievement, 8),
        Achievement(4, "Check-In 10 days", R.drawable.achievement, 11),
        Achievement(5, "Check-In 14 days", R.drawable.achievement, 15),
        Achievement(6, "Check-In 30 days", R.drawable.achievement, 31)
    )
    Box(modifier = Modifier.fillMaxSize()) {
        Scaffold(
            topBar = {
                TopAppBar(
                    title = { Text(text = "Achievements") },
                    actions = {
//                        IconButton(onClick = {
//                            val newScore = sharedPreferences.getInt("score", 0) + 1
//                            with(sharedPreferences.edit()) {
//                                putInt("score", newScore)
//                                apply()
//                            }
//                            Log.e("AchievementsScreen", "New score: " + sharedPreferences.getInt("score", 0).toString())
//                        }) {
//                            Icon(Icons.Filled.Add, contentDescription = "Increase Score")
//                        }
                    }
                )
            }
        ) { innerPadding ->
            Box(modifier = Modifier.padding(innerPadding)) {
                AchievementsContent(score = score, achievements = achievements, isUserLoggedIn = isUserLoggedIn)
            }
        }


//        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
//            Text(
//                text = score.toString(),
//                style = typography.titleLarge,
//                color = md_theme_light_inversePrimary
//            )
//        }

        if (!isUserLoggedIn) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(MaterialTheme.colorScheme.onSurface.copy(alpha = 0.3f))
                    .clickable {},
                contentAlignment = Alignment.Center
            ) {
                Column(
                    modifier = Modifier.align(Alignment.Center),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Spacer(modifier = Modifier.weight(1f))
                    Text(
                        text = "Please log in to access this page",
                        style = MaterialTheme.typography.headlineMedium.copy(color = MaterialTheme.colorScheme.onPrimary),
                        textAlign = TextAlign.Center
                    )
                    Spacer(modifier = Modifier.height(16.dp))
                    Button(
                        onClick = {
                            navController.navigate(NavigationRoutes.Unauthenticated.Login.route)
                        }
                    ) {
                        Text("Go to login page")
                    }
                    Spacer(modifier = Modifier.weight(1f))
                }
            }
        }
    }
}

@Composable
fun AchievementsContent(score: Int, achievements: List<Achievement>, isUserLoggedIn: Boolean = false) {
    Column(modifier = Modifier.fillMaxWidth(), horizontalAlignment = Alignment.CenterHorizontally) {
        for (achievement in achievements.chunked(3)) {
            Row(modifier = Modifier.fillMaxWidth()) {
                achievement.forEach { ach ->
                    AchievementItem(achievement = ach, isUnlocked = (score >= ach.unlockScore && isUserLoggedIn), modifier = Modifier.weight(1f).padding(4.dp))
                }
            }
        }
    }
}

@Composable
fun AchievementItem(achievement: Achievement, isUnlocked: Boolean, modifier: Modifier = Modifier,) {
    // This Column is a direct child in a Row, so weight should work here
    Column(
        modifier = modifier,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Image(
            painter = painterResource(id = achievement.icon),
            contentDescription = achievement.name,
            modifier = Modifier
                .size(64.dp)
                .alpha(if (isUnlocked) 1f else 0.3f),
            contentScale = ContentScale.Crop
        )
        Text(
            text = achievement.name,
            style = MaterialTheme.typography.bodyMedium,
            color = if (isUnlocked) LocalContentColor.current else Color.Gray,
            textAlign = TextAlign.Center,
            modifier = Modifier.fillMaxWidth()
        )
    }
}
