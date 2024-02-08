package com.hydrofish.app.nav

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.Star

sealed class NavItem {
    object Home :
        Item(path = NavPath.HOME.toString(), title = NavTitle.HOME, icon = Icons.Default.Home)

    object History :
        Item(path = NavPath.HISTORY.toString(), title = NavTitle.HISTORY, icon = Icons.Default.DateRange)

    object Reminder :
        Item(path = NavPath.REMINDER.toString(), title = NavTitle.REMINDER, icon = Icons.Default.Notifications)

    object Achievements :
        Item(path = NavPath.ACHIEVEMENTS.toString(), title = NavTitle.ACHIEVEMENTS, icon = Icons.Default.Star)

    object Settings :
        Item(
            path = NavPath.SETTINGS.toString(), title = NavTitle.SETTINGS, icon = Icons.Default.Settings)
}