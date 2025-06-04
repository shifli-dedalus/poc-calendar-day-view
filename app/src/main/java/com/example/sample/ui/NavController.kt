package com.example.sample.ui

import androidx.compose.runtime.Composable
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.sample.ui.screens.CustomCreateEventScreen
import com.example.sample.ui.screens.MainContentScreen
import kotlinx.serialization.Serializable

@Composable
fun getNavHostController() = rememberNavController()

@Composable
fun SampleAppNavHost(navHostController: NavHostController, builder: NavGraphBuilder.() -> Unit) {
    NavHost(
        navController = navHostController,
        startDestination = MainPageRoute,
        builder = builder
    )
}


fun NavGraphBuilder.appNavGraphBuilder() {
    composable<MainPageRoute> {
        MainContentScreen()
    }
    composable<CustomCreateEventRoute> {
        CustomCreateEventScreen()
    }
}


// Routes
@Serializable
data object MainPageRoute

@Serializable
data object CustomCreateEventRoute