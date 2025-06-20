package com.example.sample

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import com.airbnb.android.showkase.models.Showkase

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContent {
            startActivity(Showkase.getBrowserIntent(this))
            finish()
        }


//        startActivity(ShowkaseBrowserActivity.getIntent(this, "app"))
//        finish()


//        setContent {
//            SampleAppTheme {
//                ShowkaseComposable()
//            }
//        }

        /*enableEdgeToEdge()
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
        }*/
    }
}
