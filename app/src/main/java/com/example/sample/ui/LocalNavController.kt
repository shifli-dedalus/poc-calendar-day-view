package com.example.sample.ui

import androidx.compose.runtime.ProvidableCompositionLocal
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.navigation.NavHostController

val LocalNavController: ProvidableCompositionLocal<NavHostController> = staticCompositionLocalOf {
    error("Error: NoNavHostController on composition local")
}