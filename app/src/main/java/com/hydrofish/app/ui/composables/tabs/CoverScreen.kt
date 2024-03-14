package com.hydrofish.app.ui.composables.tabs

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import com.hydrofish.app.ui.composables.NavigationRoutes

@Composable
fun LoginScreen(navController: NavHostController) {
    // Implement your login UI here
    // This is just a placeholder
    Column(
        modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(text = "Please log in to access this page")
        // Add your login form fields, buttons, etc. here
        // Button to navigate to the login page
        Button(
            onClick = { navController.navigate(NavigationRoutes.Unauthenticated.Login.route)}, // Assuming "login" is the destination route
            modifier = Modifier.padding(top = 16.dp) // Add padding for spacing
        ) {
            Text(text = "Go to login page")
        }
    }
}
