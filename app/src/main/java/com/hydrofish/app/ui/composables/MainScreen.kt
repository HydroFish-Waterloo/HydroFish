package com.hydrofish.app.ui.composables

import android.annotation.SuppressLint
import android.content.Intent
import android.net.Uri
import android.provider.Settings
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.BottomAppBar
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.navigation.NavHostController

/**
 * Composable function that represents the main screen of the application.
 *
 * @param navController The navigation controller used for handling navigation between screens.
 */
@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
@Composable
fun MainScreen(navController: NavHostController, showDialog: Boolean, onDismiss: () -> Unit) {
    if (showDialog) {
        PermissionExplanationDialog(onDismiss)
    }
    Scaffold(bottomBar = {
        BottomAppBar { BottomNavigationBar(navController = navController) }
    }) {
        innerPadding ->
        NavigationScreens(
            modifier = Modifier.padding(innerPadding),
            navController = navController)
    }
}


@Composable
fun PermissionExplanationDialog(onDismiss: () -> Unit) {
    val context = LocalContext.current
    AlertDialog(
        onDismissRequest = { onDismiss() },
        title = { Text("Permission Needed") },
        text = { Text("This app requires notification permission to function properly.") },
        confirmButton = {
            TextButton(onClick = {
                val intent = Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS).apply {
                    data = Uri.fromParts("package", context.packageName, null)
                    flags = Intent.FLAG_ACTIVITY_NEW_TASK
                }
                context.startActivity(intent)
            }) {
                Text("Open Settings")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Dismiss")
            }
        }
    )
}