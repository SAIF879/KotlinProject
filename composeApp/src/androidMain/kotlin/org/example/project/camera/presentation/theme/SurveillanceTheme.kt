package org.example.project.camera.presentation.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Typography
import androidx.compose.material3.darkColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp

// ── Surveillance Color Palette ──────────────────────────────────────────────

object SurveillanceColors {
    val Background = Color(0xFF0A0E17)
    val Surface = Color(0xFF111827)
    val SurfaceVariant = Color(0xFF1A2035)
    val NeonGreen = Color(0xFF00FF41)
    val NeonGreenDim = Color(0xFF00CC33)
    val Cyan = Color(0xFF00E5FF)
    val RecRed = Color(0xFFFF1744)
    val RecRedDim = Color(0xFFD50000)
    val TextPrimary = Color(0xFFE0E0E0)
    val TextSecondary = Color(0xFF8892A4)
    val OverlayDark = Color(0xCC0A0E17)   // 80% opacity
    val OverlayLight = Color(0x330A0E17)  // 20% opacity
    val GridLine = Color(0x1A00FF41)       // 10% green
    val CornerBracket = Color(0xFF00FF41)
    val White = Color(0xFFFFFFFF)
}

// ── Color Scheme ────────────────────────────────────────────────────────────

private val SurveillanceColorScheme = darkColorScheme(
    primary = SurveillanceColors.NeonGreen,
    onPrimary = SurveillanceColors.Background,
    secondary = SurveillanceColors.Cyan,
    onSecondary = SurveillanceColors.Background,
    background = SurveillanceColors.Background,
    onBackground = SurveillanceColors.TextPrimary,
    surface = SurveillanceColors.Surface,
    onSurface = SurveillanceColors.TextPrimary,
    surfaceVariant = SurveillanceColors.SurfaceVariant,
    onSurfaceVariant = SurveillanceColors.TextSecondary,
    error = SurveillanceColors.RecRed,
    onError = SurveillanceColors.White,
)

// ── Typography (Monospace) ──────────────────────────────────────────────────

private val SurveillanceTypography = Typography(
    displayLarge = TextStyle(
        fontFamily = FontFamily.Monospace,
        fontWeight = FontWeight.Bold,
        fontSize = 32.sp,
        letterSpacing = 4.sp,
    ),
    displayMedium = TextStyle(
        fontFamily = FontFamily.Monospace,
        fontWeight = FontWeight.Bold,
        fontSize = 24.sp,
        letterSpacing = 3.sp,
    ),
    headlineLarge = TextStyle(
        fontFamily = FontFamily.Monospace,
        fontWeight = FontWeight.SemiBold,
        fontSize = 20.sp,
        letterSpacing = 2.sp,
    ),
    headlineMedium = TextStyle(
        fontFamily = FontFamily.Monospace,
        fontWeight = FontWeight.Medium,
        fontSize = 18.sp,
        letterSpacing = 1.5.sp,
    ),
    titleLarge = TextStyle(
        fontFamily = FontFamily.Monospace,
        fontWeight = FontWeight.SemiBold,
        fontSize = 16.sp,
        letterSpacing = 1.sp,
    ),
    titleMedium = TextStyle(
        fontFamily = FontFamily.Monospace,
        fontWeight = FontWeight.Medium,
        fontSize = 14.sp,
        letterSpacing = 1.sp,
    ),
    bodyLarge = TextStyle(
        fontFamily = FontFamily.Monospace,
        fontWeight = FontWeight.Normal,
        fontSize = 14.sp,
        letterSpacing = 0.5.sp,
    ),
    bodyMedium = TextStyle(
        fontFamily = FontFamily.Monospace,
        fontWeight = FontWeight.Normal,
        fontSize = 12.sp,
        letterSpacing = 0.5.sp,
    ),
    labelLarge = TextStyle(
        fontFamily = FontFamily.Monospace,
        fontWeight = FontWeight.SemiBold,
        fontSize = 12.sp,
        letterSpacing = 1.5.sp,
    ),
    labelMedium = TextStyle(
        fontFamily = FontFamily.Monospace,
        fontWeight = FontWeight.Medium,
        fontSize = 10.sp,
        letterSpacing = 1.sp,
    ),
    labelSmall = TextStyle(
        fontFamily = FontFamily.Monospace,
        fontWeight = FontWeight.Normal,
        fontSize = 9.sp,
        letterSpacing = 1.sp,
    ),
)

// ── Theme Composable ────────────────────────────────────────────────────────

@Composable
fun SurveillanceTheme(
    content: @Composable () -> Unit,
) {
    MaterialTheme(
        colorScheme = SurveillanceColorScheme,
        typography = SurveillanceTypography,
        content = content,
    )
}
