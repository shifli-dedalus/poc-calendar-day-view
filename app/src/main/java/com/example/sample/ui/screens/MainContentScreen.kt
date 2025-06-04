package com.example.sample.ui.screens

import android.annotation.SuppressLint
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import com.example.sample.ui.CustomCreateEventRoute
import com.example.sample.ui.LocalNavController
import com.example.sample.ui.theme.SampleAppThemePreview

@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
@Composable
fun MainContentScreen(modifier: Modifier = Modifier) {
    val navHostController = LocalNavController.current

    Scaffold(
        modifier = modifier.fillMaxSize(),
        floatingActionButton = {
            FloatingActionButton(
                onClick = {
                    navHostController.navigate(route = CustomCreateEventRoute)
                },
                shape = CircleShape,
                containerColor = Color(0xFFFFAA00),
                contentColor = Color.White
            ) {
                Icon(
                    Icons.Filled.Add,
                    contentDescription = null
                )
            }
        }
    ) {
        DayTimeSlotScreen(modifier)
    }
}


@Preview
@Composable
private fun MainContentScreenPreview() =
    SampleAppThemePreview {
        MainContentScreen()
    }