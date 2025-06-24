package com.example.sample

import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.platform.LocalInspectionMode
import androidx.compose.ui.unit.Density
import androidx.navigation.compose.ComposeNavigator
import androidx.navigation.testing.TestNavHostController
import app.cash.paparazzi.DeviceConfig.Companion.PIXEL_5
import app.cash.paparazzi.Paparazzi
import com.airbnb.android.showkase.models.Showkase
import com.airbnb.android.showkase.models.ShowkaseBrowserComponent
import com.example.sample.ui.LocalNavController
import com.google.testing.junit.testparameterinjector.TestParameter
import com.google.testing.junit.testparameterinjector.TestParameterInjector
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith


class ComponentPreview(
    val showkaseBrowserComponent: ShowkaseBrowserComponent
) {
    val content: @Composable () -> Unit = showkaseBrowserComponent.component
    override fun toString(): String =
        (showkaseBrowserComponent.group + ":" + showkaseBrowserComponent.componentName)
            .replace('[', '_')
            .replace(' ', '_')
            .replace(':', '_')
}

@RunWith(TestParameterInjector::class)
class ComposePaparazziTests {

    class PreviewProvider : TestParameter.TestParameterValuesProvider {
        override fun provideValues(): List<ComponentPreview> {

            val map = Showkase.getMetadata().componentList.map(::ComponentPreview)
            println("values of map: $map")
            map[0].showkaseBrowserComponent
            return map
        }
    }

    @get:Rule
    val paparazzi = Paparazzi(
        deviceConfig = PIXEL_5,
    )

    @Test
    fun preview_tests(
        @TestParameter(valuesProvider = PreviewProvider::class) componentPreview: ComponentPreview,
        @TestParameter(value = ["1.0", "1.5"]) fontScale: Float,
    ) {
        paparazzi.snapshot {
            val context = LocalContext.current
            val navController = TestNavHostController(context).apply {
                navigatorProvider.addNavigator(ComposeNavigator())
            }

            CompositionLocalProvider(
                LocalInspectionMode provides true,
                LocalDensity provides Density(
                    density = LocalDensity.current.density,
                    fontScale = fontScale
                ),
                LocalNavController provides navController
            ) {
                componentPreview.content()
            }
        }
    }
}
