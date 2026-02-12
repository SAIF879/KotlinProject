package org.example.project.camera.presentation.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Typography
import androidx.compose.material3.darkColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp

// ── Professional Color Palette ──────────────────────────────────────────────

object SurveillanceColors {
    // Core
    val Background = Color(0xFF111111)
    val Surface = Color(0xFF1A1A1A)
    val SurfaceVariant = Color(0xFF242424)
    val SurfaceElevated = Color(0xFF2A2A2A)

    // Accent
    val Accent = Color(0xFF3B82F6)        // Refined blue
    val AccentLight = Color(0xFF60A5FA)
    val AccentDim = Color(0xFF2563EB)

    // Status
    val RecRed = Color(0xFFEF4444)
    val RecRedDim = Color(0xFFDC2626)
    val StatusGreen = Color(0xFF22C55E)

    // Text
    val TextPrimary = Color(0xFFF5F5F5)
    val TextSecondary = Color(0xFF9CA3AF)
    val TextTertiary = Color(0xFF6B7280)

    // Overlay
    val OverlayDark = Color(0xCC111111)
    val White = Color(0xFFFFFFFF)
    val Divider = Color(0xFF2E2E2E)
}

// ── Color Scheme ────────────────────────────────────────────────────────────

private val AppColorScheme = darkColorScheme(
    primary = SurveillanceColors.Accent,
    onPrimary = SurveillanceColors.White,
    secondary = SurveillanceColors.AccentLight,
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

// ── Typography ──────────────────────────────────────────────────────────────

private val AppTypography = Typography(
    displayLarge = TextStyle(
        fontWeight = FontWeight.Light,
        fontSize = 34.sp,
        letterSpacing = (-0.5).sp,
    ),
    displayMedium = TextStyle(
        fontWeight = FontWeight.Light,
        fontSize = 28.sp,
        letterSpacing = 0.sp,
    ),
    headlineLarge = TextStyle(
        fontWeight = FontWeight.SemiBold,
        fontSize = 22.sp,
        letterSpacing = 0.sp,
    ),
    headlineMedium = TextStyle(
        fontWeight = FontWeight.Medium,
        fontSize = 18.sp,
        letterSpacing = 0.15.sp,
    ),
    titleLarge = TextStyle(
        fontWeight = FontWeight.Medium,
        fontSize = 16.sp,
        letterSpacing = 0.15.sp,
    ),
    titleMedium = TextStyle(
        fontWeight = FontWeight.Medium,
        fontSize = 14.sp,
        letterSpacing = 0.1.sp,
    ),
    bodyLarge = TextStyle(
        fontWeight = FontWeight.Normal,
        fontSize = 16.sp,
        letterSpacing = 0.5.sp,
    ),
    bodyMedium = TextStyle(
        fontWeight = FontWeight.Normal,
        fontSize = 14.sp,
        letterSpacing = 0.25.sp,
    ),
    labelLarge = TextStyle(
        fontFamily = FontFamily.Monospace,
        fontWeight = FontWeight.Medium,
        fontSize = 12.sp,
        letterSpacing = 0.5.sp,
    ),
    labelMedium = TextStyle(
        fontFamily = FontFamily.Monospace,
        fontWeight = FontWeight.Medium,
        fontSize = 11.sp,
        letterSpacing = 0.5.sp,
    ),
    labelSmall = TextStyle(
        fontFamily = FontFamily.Monospace,
        fontWeight = FontWeight.Normal,
        fontSize = 10.sp,
        letterSpacing = 0.5.sp,
    ),
)

// ── Theme Composable ────────────────────────────────────────────────────────

@Composable
fun SurveillanceTheme(
    content: @Composable () -> Unit,
) {
    MaterialTheme(
        colorScheme = AppColorScheme,
        typography = AppTypography,
        content = content,
    )
}
