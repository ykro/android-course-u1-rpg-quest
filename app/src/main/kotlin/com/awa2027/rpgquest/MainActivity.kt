package com.awa2027.rpgquest

import android.graphics.Color
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.SystemBarStyle
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import com.awa2027.rpgquest.ui.navigation.AppNavHost
import com.awa2027.rpgquest.ui.theme.RpgQuestTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        // Barras del sistema claras siempre, igual que el tema.
        val lightBars = SystemBarStyle.light(Color.TRANSPARENT, Color.TRANSPARENT)
        enableEdgeToEdge(statusBarStyle = lightBars, navigationBarStyle = lightBars)
        super.onCreate(savedInstanceState)
        setContent {
            RpgQuestTheme {
                AppNavHost()
            }
        }
    }
}
