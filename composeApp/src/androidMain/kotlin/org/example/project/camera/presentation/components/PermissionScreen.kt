package org.example.project.camera.presentation.components

import android.content.Intent
import android.net.Uri
import android.provider.Settings
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import org.example.project.camera.presentation.theme.SurveillanceColors

/**
 * Professional permission request screen.
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
        // Icon
        Box(
            modifier = Modifier
                .size(72.dp)
                .clip(CircleShape)
                .background(
                    Brush.linearGradient(
                        colors = listOf(
                            SurveillanceColors.Accent,
                            SurveillanceColors.AccentDim,
                        ),
                    )
                ),
            contentAlignment = Alignment.Center,
        ) {
            Text(
                text = "🎥",
                fontSize = 28.sp,
            )
        }

        Spacer(modifier = Modifier.height(28.dp))

        Text(
            text = "Camera Access Required",
            color = SurveillanceColors.TextPrimary,
            fontSize = 22.sp,
            fontWeight = FontWeight.SemiBold,
        )

        Spacer(modifier = Modifier.height(12.dp))

        Text(
            text = "To use the security monitor, please grant\ncamera and microphone permissions.",
            color = SurveillanceColors.TextSecondary,
            fontSize = 14.sp,
            textAlign = TextAlign.Center,
            lineHeight = 22.sp,
        )

        Spacer(modifier = Modifier.height(40.dp))

        if (isPermanentlyDenied) {
            Text(
                text = "Permission was denied. Please enable it\nin your device settings.",
                color = SurveillanceColors.TextTertiary,
                fontSize = 13.sp,
                textAlign = TextAlign.Center,
                lineHeight = 20.sp,
            )

            Spacer(modifier = Modifier.height(20.dp))

            Button(
                onClick = {
                    val intent = Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS).apply {
                        data = Uri.fromParts("package", context.packageName, null)
                    }
                    context.startActivity(intent)
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(50.dp),
                shape = RoundedCornerShape(12.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = SurveillanceColors.SurfaceVariant,
                    contentColor = SurveillanceColors.TextPrimary,
                ),
            ) {
                Text(
                    text = "Open Settings",
                    fontWeight = FontWeight.Medium,
                    fontSize = 15.sp,
                )
            }
        } else {
            Button(
                onClick = onRequestPermission,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(50.dp),
                shape = RoundedCornerShape(12.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = SurveillanceColors.Accent,
                    contentColor = SurveillanceColors.White,
                ),
            ) {
                Text(
                    text = "Grant Permission",
                    fontWeight = FontWeight.Medium,
                    fontSize = 15.sp,
                )
            }
        }
    }
}
