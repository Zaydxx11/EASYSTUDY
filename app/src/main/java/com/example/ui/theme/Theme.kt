package com.example.ui.theme

import android.os.Build
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.ColorScheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.dynamicDarkColorScheme
import androidx.compose.material3.dynamicLightColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext

fun getEasyStudyDarkScheme(accent: String): ColorScheme {
    val primaryColor = when (accent.uppercase()) {
        "CYAN" -> CyanLight
        "EMERALD" -> EmeraldLight
        "PURPLE" -> Color(0xFFC084FC)
        else -> IndigoLight
    }
    val containerColor = when (accent.uppercase()) {
        "CYAN" -> CyanDark
        "EMERALD" -> Color(0xFF065F46)
        "PURPLE" -> Color(0xFF581C87)
        else -> IndigoDark
    }

    return darkColorScheme(
        primary = primaryColor,
        onPrimary = Color.White,
        primaryContainer = containerColor,
        onPrimaryContainer = Color.White,
        secondary = CyanLight,
        onSecondary = Color.Black,
        secondaryContainer = CyanDark,
        onSecondaryContainer = Color.White,
        tertiary = EmeraldLight,
        onTertiary = Color.Black,
        background = DarkBg,
        onBackground = Color(0xFFF1F5F9),
        surface = DarkSurface,
        onSurface = Color(0xFFF1F5F9),
        surfaceVariant = DarkSurfaceVariant,
        onSurfaceVariant = Color(0xFFCBD5E1),
        outline = DarkBorder
    )
}

fun getEasyStudyLightScheme(accent: String): ColorScheme {
    val primaryColor = when (accent.uppercase()) {
        "CYAN" -> CyanAccent
        "EMERALD" -> EmeraldSuccess
        "PURPLE" -> Color(0xFF9333EA)
        else -> IndigoPrimary
    }
    val containerColor = when (accent.uppercase()) {
        "CYAN" -> Color(0xFFCFFAFE)
        "EMERALD" -> Color(0xFFD1FAE5)
        "PURPLE" -> Color(0xFFF3E8FF)
        else -> Color(0xFFE0E7FF)
    }

    return lightColorScheme(
        primary = primaryColor,
        onPrimary = Color.White,
        primaryContainer = containerColor,
        onPrimaryContainer = primaryColor,
        secondary = CyanAccent,
        onSecondary = Color.White,
        secondaryContainer = Color(0xFFCFFAFE),
        onSecondaryContainer = CyanDark,
        tertiary = EmeraldSuccess,
        onTertiary = Color.White,
        background = LightBg,
        onBackground = Color(0xFF0F172A),
        surface = LightSurface,
        onSurface = Color(0xFF0F172A),
        surfaceVariant = LightSurfaceVariant,
        onSurfaceVariant = Color(0xFF334155),
        outline = LightBorder
    )
}

@Composable
fun MyApplicationTheme(
    themeMode: String = "SYSTEM",      // "SYSTEM", "DARK", "LIGHT"
    themeAccent: String = "INDIGO",    // "INDIGO", "CYAN", "EMERALD", "PURPLE"
    dynamicColor: Boolean = false,
    content: @Composable () -> Unit
) {
    val systemInDark = isSystemInDarkTheme()
    val isDark = when (themeMode.uppercase()) {
        "DARK" -> true
        "LIGHT" -> false
        else -> systemInDark
    }

    val colorScheme = when {
        dynamicColor && Build.VERSION.SDK_INT >= Build.VERSION_CODES.S -> {
            val context = LocalContext.current
            if (isDark) dynamicDarkColorScheme(context) else dynamicLightColorScheme(context)
        }
        isDark -> getEasyStudyDarkScheme(themeAccent)
        else -> getEasyStudyLightScheme(themeAccent)
    }

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        content = content
    )
}
