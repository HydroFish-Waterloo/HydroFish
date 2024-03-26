package com.hydrofish.app.ui.composables

import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import androidx.navigation.navigation
import com.hydrofish.app.api.ApiClient
import com.hydrofish.app.ui.composables.unauthenticated.login.LoginScreen
import com.hydrofish.app.ui.composables.unauthenticated.registration.RegistrationScreen
import com.hydrofish.app.utils.IUserSessionRepository
import com.hydrofish.app.viewmodelfactories.LoginViewModelFactory
import com.hydrofish.app.viewmodelfactories.RegisterViewModelFactory

/**
 * Login, registration, forgot password screens nav graph builder
 * (Unauthenticated user)
 */
fun NavGraphBuilder.unauthenticatedGraph(navController: NavController, userSessionRepository: IUserSessionRepository) {

    navigation(
        route = NavigationRoutes.Unauthenticated.NavigationRoute.route,
        startDestination = NavigationRoutes.Unauthenticated.Login.route
    ) {

        // Login
        composable(route = NavigationRoutes.Unauthenticated.Login.route) {
            LoginScreen(
                onNavigateToRegistration = {
                    navController.navigate(route = NavigationRoutes.Unauthenticated.Registration.route)
                },
                onNavigateToForgotPassword = {},
                onNavigateToAuthenticatedRoute = {
//                    navController.navigate(route = NavigationRoutes.Authenticated.NavigationRoute.route) {
//                        popUpTo(route = NavigationRoutes.Unauthenticated.NavigationRoute.route) {
//                            inclusive = true
//                        }
//                    }
                    navController.popBackStack(route = NavigationRoutes.Unauthenticated.Login.route, inclusive = true)
                },
                onNavigateBack = {
                    navController.navigateUp()
                },
                userSessionRepository = userSessionRepository,
                loginViewModel = viewModel(factory = LoginViewModelFactory(userSessionRepository, ApiClient.apiService)),
            )
        }

        // Registration
        composable(route = NavigationRoutes.Unauthenticated.Registration.route) {
            RegistrationScreen(
                onNavigateBack = {
                    navController.navigateUp()
                },
                onNavigateToAuthenticatedRoute = {
//                    navController.navigate(route = NavigationRoutes.Authenticated.NavigationRoute.route) {
//                        popUpTo(route = NavigationRoutes.Unauthenticated.NavigationRoute.route) {
//                            inclusive = true
//                        }
//                    }
                    navController.popBackStack(route = NavigationRoutes.Unauthenticated.Login.route, inclusive = true)
                },
                userSessionRepository = userSessionRepository,
                registrationViewModel = viewModel(factory = RegisterViewModelFactory(userSessionRepository, ApiClient.apiService)),
            )
        }
    }
}

/**
 * Authenticated screens nav graph builder
 */
fun NavGraphBuilder.authenticatedGraph(navController: NavController) {
    navigation(
        route = NavigationRoutes.Authenticated.NavigationRoute.route,
        startDestination = NavigationRoutes.Authenticated.Dashboard.route
    ) {
        // Dashboard
        composable(route = NavigationRoutes.Authenticated.Dashboard.route) {
//            DashboardScreen()
        }
    }
}