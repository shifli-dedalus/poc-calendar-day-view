package com.example.sample.ui.theme

import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.navigation.compose.rememberNavController
import com.example.sample.ui.LocalNavController

@Composable
fun SampleAppThemePreview(content: @Composable () -> Unit) {
    SampleAppTheme {
        CompositionLocalProvider(
            LocalNavController provides rememberNavController()
        ) {
            Surface {  content() }
        }
    }
}
