package com.connectmesh.ui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

val Ink = Color(0xFF12201D)
val Forest = Color(0xFF0F5D4D)
val Mint = Color(0xFFB8F1D8)
val Cream = Color(0xFFFAF9F3)
val Sand = Color(0xFFE9E7DE)
val Coral = Color(0xFFFF8A70)

private val LightColors = lightColorScheme(
    primary = Forest, onPrimary = Color.White, primaryContainer = Mint,
    secondary = Coral, background = Cream, surface = Color.White,
    onBackground = Ink, onSurface = Ink, surfaceVariant = Sand
)
private val DarkColors = darkColorScheme(primary = Mint, secondary = Coral)

@Composable
fun ConnectMeshTheme(content: @Composable () -> Unit) {
    MaterialTheme(colorScheme = LightColors, content = content)
}
