package com.hydrofish.app.ui.composables.tabs

import android.Manifest
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import android.provider.Settings
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.selection.toggleable
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.core.content.ContextCompat
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import coil.compose.AsyncImage
import coil.request.ImageRequest
import com.hydrofish.app.R
import com.hydrofish.app.permission.PermissionChecker
import com.hydrofish.app.permission.PermissionResultHandler
import com.hydrofish.app.ui.composables.NavigationRoutes
import com.hydrofish.app.utils.UserSessionRepository
import com.hydrofish.app.viewmodelfactories.SettingsViewModelFactory

/**
 * Composable function that represents the profile screen of the application.
 */
const val NOTIFICATION_KEY = "notification"
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsScreen(permissionChecker: PermissionChecker,
                   permissionResultHandler: PermissionResultHandler,
                   navController: NavHostController,
                   userSessionRepository: UserSessionRepository
) {
    val context = LocalContext.current

    val settingsViewModel: SettingsViewModel = viewModel(factory = SettingsViewModelFactory(userSessionRepository))
    val isUserLoggedIn by settingsViewModel.isLoggedIn.observeAsState(false)
    val userName = remember { userSessionRepository.getUserName() ?: "" }

    val sharedPreferences = remember {
        context.getSharedPreferences("MyPrefs", Context.MODE_PRIVATE)
    }
    val notificationValue = sharedPreferences.getBoolean(NOTIFICATION_KEY, false)
    var item1EnabledState by rememberSaveable { mutableStateOf(notificationValue) }
    var showPermissionExplanationDialog by remember { mutableStateOf(false) }

    var hasPostNotificationsPermission by remember {
        mutableStateOf(
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                ContextCompat.checkSelfPermission(
                    context,
                    Manifest.permission.POST_NOTIFICATIONS
                ) == PackageManager.PERMISSION_GRANTED
            } else {
                true
            }
        )
    }

    val lifecycleOwner = LocalLifecycleOwner.current
    DisposableEffect(lifecycleOwner) {
        val observer = LifecycleEventObserver { _, event ->
            if (event == Lifecycle.Event.ON_START) {
                hasPostNotificationsPermission = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                    ContextCompat.checkSelfPermission(
                        context,
                        Manifest.permission.POST_NOTIFICATIONS
                    ) == PackageManager.PERMISSION_GRANTED
                } else {
                    true
                }
                if (!permissionResultHandler.handlePermissionResult(hasPostNotificationsPermission) || !permissionChecker.canScheduleExactAlarms(context)){
                    item1EnabledState = false
                    sharedPreferences.edit().putBoolean(NOTIFICATION_KEY, item1EnabledState).apply()
                }
            }
        }
        lifecycleOwner.lifecycle.addObserver(observer)

        onDispose {
            lifecycleOwner.lifecycle.removeObserver(observer)
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(title =  { Text(text = "Settings") })
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(paddingValues)
        ) {

            if (isUserLoggedIn && userName != null) {
                Box(
                    contentAlignment = Alignment.Center,
                    modifier = Modifier.fillMaxWidth()
                ) {
//                    Image(
//                        painter = rememberAsyncImagePainter(model = R.drawable.user_avatar), // 替换为实际的头像URL或资源ID
//                        contentDescription = "User Avatar",
//                        modifier = Modifier
//                            .size(64.dp)
//                            .clip(CircleShape)
//                    )
                    AsyncImage(
                        modifier = Modifier
                            .size(64.dp)
                            .clip(CircleShape),
                        model = ImageRequest.Builder(LocalContext.current)
                            .data(data = R.drawable.user_avatar)
                            .crossfade(enable = true)
                            .build(),
                        contentDescription = "User Avatar",
                        contentScale = ContentScale.Crop
                    )
                }
                Text(
                    text = userName,
                    style = MaterialTheme.typography.headlineMedium.copy(fontWeight = FontWeight.Bold),
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 4.dp),
                    textAlign = TextAlign.Center
                )
                Text(
                    text = "Hi there, have you remembered to stay hydrated today?",
                    style = MaterialTheme.typography.bodySmall,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 4.dp),
                    textAlign = TextAlign.Center
                )
            }

            Button(
                onClick = {
                    if (isUserLoggedIn) {
                        settingsViewModel.logout()
                    } else {
                        navController.navigate(NavigationRoutes.Unauthenticated.Login.route)
                    }
                },
                colors = ButtonDefaults.buttonColors(
                    containerColor = if (isUserLoggedIn) MaterialTheme.colorScheme.error else MaterialTheme.colorScheme.primary
                ),
                modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp, vertical = 8.dp)
            ) {
                Text(if (isUserLoggedIn) "Logout" else "Login")
            }

            SettingSwitchItem(
                title = "Notification",
                description = "Turn on to receive notifications",
                checked = item1EnabledState,
                onCheckedChange = {newState ->
                    if (newState) {
                        if (permissionResultHandler.handlePermissionResult(hasPostNotificationsPermission) && permissionChecker.canScheduleExactAlarms(context)){
                            item1EnabledState = true
                            sharedPreferences.edit().putBoolean(NOTIFICATION_KEY, item1EnabledState).apply()
                        } else {
                            if (!permissionChecker.canScheduleExactAlarms(context)){
                                item1EnabledState = false
                                sharedPreferences.edit().putBoolean(NOTIFICATION_KEY, item1EnabledState).apply()
                                val intent = Intent(Settings.ACTION_REQUEST_SCHEDULE_EXACT_ALARM)
                                context.startActivity(intent)
                            }
                            if(!permissionResultHandler.handlePermissionResult(hasPostNotificationsPermission)){
                                item1EnabledState = false
                                sharedPreferences.edit().putBoolean(NOTIFICATION_KEY, item1EnabledState).apply()
                                showPermissionExplanationDialog = true
                            }
                        }
                    } else {
                        item1EnabledState = false
                        sharedPreferences.edit().putBoolean(NOTIFICATION_KEY, item1EnabledState).apply()
                    }
                }
            )
            if (showPermissionExplanationDialog) {
                PermissionExplanationDialog(onDismiss = { showPermissionExplanationDialog = false })
            }
        }
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

@Composable
private fun SettingSwitchItem(
    modifier: Modifier = Modifier,
    checked: Boolean,
    onCheckedChange: (Boolean) -> Unit,
    title: String,
    description: String,
    enabled: Boolean = true,
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 8.dp)
            .toggleable(
                value = checked,
                enabled = enabled,
                role = Role.Switch,
                onValueChange = onCheckedChange
            ),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Column(
            modifier = Modifier.weight(1.0f),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {

            Text(
                text = title,
                style = MaterialTheme.typography.bodyLarge,
                maxLines = 1,
            )
            Text(
                text = description,
                style = MaterialTheme.typography.bodyMedium,
            )
        }

        Switch(
            checked = checked,
            onCheckedChange = null,
            enabled = enabled
        )
    }
}