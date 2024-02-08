package com.hydrofish.app.ui.composables

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.hydrofish.app.nav.NavItem
import com.hydrofish.app.ui.composables.tabs.AchievementsScreen
import com.hydrofish.app.ui.composables.tabs.HistoryScreen
import com.hydrofish.app.ui.composables.tabs.HomeScreen
import com.hydrofish.app.ui.composables.tabs.SettingsScreen
import com.hydrofish.app.ui.composables.tabs.ReminderScreen

/**
 * Composable function that defines the navigation screens and their corresponding destinations.
 *
 * @param navController The navigation controller used for handling navigation between screens.
 */
@Composable
fun NavigationScreens(modifier: Modifier, navController: NavHostController) {
    NavHost(navController, startDestination = NavItem.Home.path) {
        composable(NavItem.Home.path) { HomeScreen(modifier = modifier) }
        composable(NavItem.History.path) { HistoryScreen() }
        composable(NavItem.Reminder.path) { ReminderScreen() }
        composable(NavItem.Achievements.path) { AchievementsScreen() }
        composable(NavItem.Settings.path) { SettingsScreen() }
    }
}