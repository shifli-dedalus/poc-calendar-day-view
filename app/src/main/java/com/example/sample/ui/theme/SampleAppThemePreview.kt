package com.example.sample.ui.theme

import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.platform.LocalInspectionMode
import androidx.compose.ui.unit.Density
import androidx.navigation.compose.ComposeNavigator
import androidx.navigation.compose.rememberNavController
import androidx.navigation.testing.TestNavHostController
import com.example.sample.ui.LocalNavController

@Composable
fun SampleAppThemePreview(content: @Composable () -> Unit) {
    val navController = TestNavHostController(LocalContext.current).apply {
        navigatorProvider.addNavigator(ComposeNavigator())
    }
    SampleAppTheme {
        CompositionLocalProvider(
            LocalInspectionMode provides true,
            LocalDensity provides Density(
                density = LocalDensity.current.density,
            ),
            LocalNavController provides navController
        ) {
            Surface {  content() }
        }
    }
}
