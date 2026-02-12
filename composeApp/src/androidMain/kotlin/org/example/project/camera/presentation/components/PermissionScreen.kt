package org.example.project.camera.presentation.components

import android.content.Intent
import android.net.Uri
import android.provider.Settings
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import org.example.project.camera.presentation.theme.SurveillanceColors

/**
 * Surveillance-themed permission request screen.
 *
 * Shows a shield icon with "ACCESS DENIED" heading
 * and a tactical "AUTHORIZE" button.
 */
@Composable
fun PermissionScreen(
    onRequestPermission: () -> Unit,
    isPermanentlyDenied: Boolean,
) {
    val context = LocalContext.current

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(SurveillanceColors.Background)
            .padding(horizontal = 40.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center,
    ) {
        // Shield / lock icon
        ShieldIcon()

        Spacer(modifier = Modifier.height(32.dp))

        Text(
            text = "ACCESS DENIED",
            color = SurveillanceColors.RecRed,
            fontSize = 22.sp,
            fontFamily = FontFamily.Monospace,
            fontWeight = FontWeight.Bold,
            letterSpacing = 4.sp,
        )

        Spacer(modifier = Modifier.height(12.dp))

        Text(
            text = "CAMERA & AUDIO AUTHORIZATION\nREQUIRED TO PROCEED",
            color = SurveillanceColors.TextSecondary,
            fontSize = 12.sp,
            fontFamily = FontFamily.Monospace,
            letterSpacing = 1.sp,
            textAlign = TextAlign.Center,
            lineHeight = 20.sp,
        )

        Spacer(modifier = Modifier.height(8.dp))

        Text(
            text = "SEC CLEARANCE: LEVEL 1",
            color = SurveillanceColors.NeonGreen.copy(alpha = 0.5f),
            fontSize = 10.sp,
            fontFamily = FontFamily.Monospace,
            letterSpacing = 2.sp,
        )

        Spacer(modifier = Modifier.height(40.dp))

        if (isPermanentlyDenied) {
            Text(
                text = "[ PERMISSION PERMANENTLY DENIED ]",
                color = SurveillanceColors.RecRed.copy(alpha = 0.7f),
                fontSize = 10.sp,
                fontFamily = FontFamily.Monospace,
                letterSpacing = 1.sp,
                textAlign = TextAlign.Center,
            )

            Spacer(modifier = Modifier.height(16.dp))

            Button(
                onClick = {
                    val intent = Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS).apply {
                        data = Uri.fromParts("package", context.packageName, null)
                    }
                    context.startActivity(intent)
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(48.dp),
                shape = RoundedCornerShape(4.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = SurveillanceColors.SurfaceVariant,
                    contentColor = SurveillanceColors.Cyan,
                ),
            ) {
                Text(
                    text = "[ OPEN SETTINGS ]",
                    fontFamily = FontFamily.Monospace,
                    fontWeight = FontWeight.Bold,
                    letterSpacing = 2.sp,
                    fontSize = 12.sp,
                )
            }
        } else {
            Button(
                onClick = onRequestPermission,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(48.dp),
                shape = RoundedCornerShape(4.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = SurveillanceColors.NeonGreen,
                    contentColor = SurveillanceColors.Background,
                ),
            ) {
                Text(
                    text = "[ AUTHORIZE ]",
                    fontFamily = FontFamily.Monospace,
                    fontWeight = FontWeight.Bold,
                    letterSpacing = 3.sp,
                    fontSize = 13.sp,
                )
            }
        }
    }
}

@Composable
private fun ShieldIcon() {
    Canvas(modifier = Modifier.size(72.dp)) {
        val center = Offset(size.width / 2, size.height / 2)
        val w = size.width
        val h = size.height

        // Shield outline (simplified as a pointed-bottom shape)
        val shieldColor = SurveillanceColors.RecRed.copy(alpha = 0.8f)

        // Outer circle
        drawCircle(
            color = shieldColor,
            radius = w / 2.5f,
            center = center,
            style = Stroke(width = 2.5f),
        )

        // Lock body (rectangle)
        val lockW = w * 0.22f
        val lockH = h * 0.18f
        val lockTop = center.y + h * 0.02f
        drawRect(
            color = shieldColor,
            topLeft = Offset(center.x - lockW, lockTop),
            size = androidx.compose.ui.geometry.Size(lockW * 2, lockH),
            style = Stroke(width = 2f),
        )

        // Lock shackle (arc)
        val shackleRadius = lockW * 0.7f
        drawArc(
            color = shieldColor,
            startAngle = 180f,
            sweepAngle = 180f,
            useCenter = false,
            topLeft = Offset(center.x - shackleRadius, lockTop - shackleRadius * 1.5f),
            size = androidx.compose.ui.geometry.Size(shackleRadius * 2, shackleRadius * 2),
            style = Stroke(width = 2f, cap = StrokeCap.Square),
        )

        // Keyhole dot
        drawCircle(
            color = shieldColor,
            radius = 2.5f,
            center = Offset(center.x, lockTop + lockH * 0.45f),
        )
    }
}
