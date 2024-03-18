package com.hydrofish.app.ui.composables

import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.hydrofish.app.nav.NavItem
import com.hydrofish.app.permission.RealPermissionChecker
import com.hydrofish.app.permission.RealPermissionResultHandler
import com.hydrofish.app.ui.composables.tabs.AchievementsScreen
import com.hydrofish.app.ui.composables.tabs.HistoryScreen
import com.hydrofish.app.ui.composables.tabs.HomeScreen
import com.hydrofish.app.ui.composables.tabs.ReminderScreen
import com.hydrofish.app.ui.composables.tabs.SettingsScreen
import com.hydrofish.app.utils.UserSessionRepository

/**
 * Composable function that defines the navigation screens and their corresponding destinations.
 *
 * @param navController The navigation controller used for handling navigation between screens.
 */
@Composable
fun NavigationScreens(modifier: Modifier, navController: NavHostController) {
    val context = LocalContext.current

    val userSessionRepository = remember {
        UserSessionRepository(context)
    }

    NavHost(navController, startDestination = NavItem.Home.path) {
        composable(NavItem.Home.path) { HomeScreen(modifier = modifier, userSessionRepository) }
        composable(NavItem.History.path) { HistoryScreen(userSessionRepository, navController) }
        composable(NavItem.Reminder.path) { ReminderScreen(RealPermissionChecker()) }
        composable(NavItem.Achievements.path) { AchievementsScreen() }
        composable(NavItem.Settings.path) { SettingsScreen(RealPermissionChecker(), RealPermissionResultHandler(), navController, userSessionRepository)}
        unauthenticatedGraph(navController, userSessionRepository)
    }
}