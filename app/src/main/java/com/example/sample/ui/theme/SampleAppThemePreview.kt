package com.example.sample.ui.theme

import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.platform.LocalInspectionMode
import androidx.compose.ui.unit.Density
import androidx.navigation.compose.rememberNavController
import com.example.sample.ui.LocalNavController

@Composable
fun SampleAppThemePreview(content: @Composable () -> Unit) {
    SampleAppTheme {
        CompositionLocalProvider(
            LocalInspectionMode provides true,
            LocalDensity provides Density(
                density = LocalDensity.current.density,
            )
        ) {
            Surface {  content() }
        }
    }
}
