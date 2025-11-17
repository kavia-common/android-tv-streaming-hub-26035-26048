package com.example.android_tv_frontend

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.SideEffect
import androidx.compose.ui.graphics.Color
import androidx.core.view.WindowCompat
import com.example.android_tv_frontend.navigation.TVNavGraph
import com.example.android_tv_frontend.ui.theme.OceanTVTheme

/**
 * PUBLIC_INTERFACE
 * MainActivity
 * Entry point for the Android TV app using Jetpack Compose for TV.
 * Sets up the Ocean Professional theme and hosts the navigation graph.
 */
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        WindowCompat.setDecorFitsSystemWindows(window, false)
        setContent {
            OceanTVTheme {
                TVNavGraph()
            }
        }
    }
}
