package com.dawinder.btnjc.nav

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.List
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.Star

sealed class NavItem {
    object Home :
        Item(path = NavPath.HOME.toString(), title = NavTitle.HOME, icon = Icons.Default.Home)

    object Search :
        Item(path = NavPath.HISTORY.toString(), title = NavTitle.HISTORY, icon = Icons.Default.DateRange)

    object List :
        Item(path = NavPath.ACHIEVEMENTS.toString(), title = NavTitle.ACHIEVEMENTS, icon = Icons.Default.Star)

    object Profile :
        Item(
            path = NavPath.SETTINGS.toString(), title = NavTitle.SETTINGS, icon = Icons.Default.Settings)
}