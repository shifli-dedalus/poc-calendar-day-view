package com.example.sample

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.runtime.CompositionLocalProvider
import com.example.sample.ui.LocalNavController
import com.example.sample.ui.SampleAppNavHost
import com.example.sample.ui.getNavHostController
import com.example.sample.ui.appNavGraphBuilder
import com.example.sample.ui.theme.SampleAppTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            SampleAppTheme {
                val navHostController = getNavHostController()
                CompositionLocalProvider(
                    LocalNavController provides navHostController
                ) {
                    SampleAppNavHost(
                        navHostController,
                        { appNavGraphBuilder() }
                    )
                }

            }
        }
    }
}
