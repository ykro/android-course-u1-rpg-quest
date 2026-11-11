package com.awa2027.rpgquest.ui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

private val LightColors = lightColorScheme(
    primary = Color(0xFF6D28D9),
    onPrimary = Color(0xFFFFFFFF),
    primaryContainer = Color(0xFFEDE9FE),
    onPrimaryContainer = Color(0xFF2E1065),
    secondary = Color(0xFF0F766E),
    onSecondary = Color(0xFFFFFFFF),
    secondaryContainer = Color(0xFFCCFBF1),
    onSecondaryContainer = Color(0xFF042F2E),
    tertiary = Color(0xFFB45309),
    background = Color(0xFFF8F7F4),
    onBackground = Color(0xFF18181B),
    surface = Color(0xFFFFFFFF),
    onSurface = Color(0xFF18181B),
    surfaceVariant = Color(0xFFF1F0EC),
    surfaceContainerLow = Color(0xFFFFFFFF),
    surfaceContainer = Color(0xFFF4F3EF),
    surfaceContainerHigh = Color(0xFFF1F0EC),
    surfaceContainerHighest = Color(0xFFEDEBE5),
    onSurfaceVariant = Color(0xFF3F3F46),
    outline = Color(0xFFD4D4D8),
    error = Color(0xFFB91C1C)
)

/** Tema solo claro: la app no cambia a modo oscuro aunque el sistema lo use. */
@Composable
fun RpgQuestTheme(content: @Composable () -> Unit) {
    MaterialTheme(colorScheme = LightColors, content = content)
}
