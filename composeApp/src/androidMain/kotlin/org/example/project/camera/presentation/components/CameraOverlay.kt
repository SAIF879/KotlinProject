package org.example.project.camera.presentation.components

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlinx.coroutines.delay
import org.example.project.camera.presentation.theme.SurveillanceColors
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

/**
 * CCTV-style heads-up display overlay drawn on top of the camera preview.
 *
 * Includes:
 * - Corner brackets (viewfinder)
 * - "CAM-01 | REAR/FRONT" label
 * - Live timestamp
 * - "LIVE" badge with pulsing dot
 * - Subtle horizontal grid/scan lines
 */
@Composable
fun CameraOverlay(
    isFrontCamera: Boolean,
    modifier: Modifier = Modifier,
) {
    var currentTime by remember { mutableStateOf(getCurrentTimestamp()) }

    // Tick the clock every second
    LaunchedEffect(Unit) {
        while (true) {
            currentTime = getCurrentTimestamp()
            delay(1_000L)
        }
    }

    Box(modifier = modifier.fillMaxSize()) {
        // Corner brackets + grid lines (Canvas layer)
        Canvas(modifier = Modifier.fillMaxSize()) {
            val w = size.width
            val h = size.height
            val bracketLen = 40f
            val bracketStroke = 2.5f
            val margin = 32f
            val color = SurveillanceColors.CornerBracket.copy(alpha = 0.8f)

            // ─── Corner Brackets ─────────────────────────────
            // Top-left
            drawLine(color, Offset(margin, margin), Offset(margin + bracketLen, margin), bracketStroke, StrokeCap.Square)
            drawLine(color, Offset(margin, margin), Offset(margin, margin + bracketLen), bracketStroke, StrokeCap.Square)
            // Top-right
            drawLine(color, Offset(w - margin, margin), Offset(w - margin - bracketLen, margin), bracketStroke, StrokeCap.Square)
            drawLine(color, Offset(w - margin, margin), Offset(w - margin, margin + bracketLen), bracketStroke, StrokeCap.Square)
            // Bottom-left
            drawLine(color, Offset(margin, h - margin), Offset(margin + bracketLen, h - margin), bracketStroke, StrokeCap.Square)
            drawLine(color, Offset(margin, h - margin), Offset(margin, h - margin - bracketLen), bracketStroke, StrokeCap.Square)
            // Bottom-right
            drawLine(color, Offset(w - margin, h - margin), Offset(w - margin - bracketLen, h - margin), bracketStroke, StrokeCap.Square)
            drawLine(color, Offset(w - margin, h - margin), Offset(w - margin, h - margin - bracketLen), bracketStroke, StrokeCap.Square)

            // ─── Center crosshair ────────────────────────────
            val cx = w / 2
            val cy = h / 2
            val crossSize = 16f
            val crossColor = SurveillanceColors.NeonGreen.copy(alpha = 0.3f)
            drawLine(crossColor, Offset(cx - crossSize, cy), Offset(cx + crossSize, cy), 1f)
            drawLine(crossColor, Offset(cx, cy - crossSize), Offset(cx, cy + crossSize), 1f)

            // ─── Subtle scan lines ───────────────────────────
            val lineStep = h / 120
            for (i in 0..120) {
                drawLine(
                    color = SurveillanceColors.GridLine,
                    start = Offset(0f, i * lineStep),
                    end = Offset(w, i * lineStep),
                    strokeWidth = 0.5f,
                )
            }
        }

        // ─── Top-left: Camera label ──────────────────────────
        Column(
            modifier = Modifier
                .align(Alignment.TopStart)
                .padding(start = 20.dp, top = 56.dp),
        ) {
            Text(
                text = "CAM-01 // ${if (isFrontCamera) "FRONT" else "REAR"}",
                color = SurveillanceColors.NeonGreen,
                fontSize = 11.sp,
                fontFamily = FontFamily.Monospace,
                fontWeight = FontWeight.Bold,
                letterSpacing = 2.sp,
            )
            Text(
                text = currentTime,
                color = SurveillanceColors.TextSecondary,
                fontSize = 10.sp,
                fontFamily = FontFamily.Monospace,
                letterSpacing = 1.sp,
            )
        }

        // ─── Top-right: LIVE badge ───────────────────────────
        Row(
            modifier = Modifier
                .align(Alignment.TopEnd)
                .padding(end = 20.dp, top = 56.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            // Solid green dot
            Canvas(modifier = Modifier.size(6.dp)) {
                drawCircle(color = SurveillanceColors.NeonGreen)
            }
            Spacer(modifier = Modifier.width(6.dp))
            Text(
                text = "LIVE",
                color = SurveillanceColors.NeonGreen,
                fontSize = 11.sp,
                fontFamily = FontFamily.Monospace,
                fontWeight = FontWeight.Bold,
                letterSpacing = 2.sp,
            )
        }
    }
}

private fun getCurrentTimestamp(): String {
    val sdf = SimpleDateFormat("yyyy-MM-dd  HH:mm:ss", Locale.getDefault())
    return sdf.format(Date())
}
