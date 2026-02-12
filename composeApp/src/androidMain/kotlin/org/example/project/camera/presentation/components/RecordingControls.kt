package org.example.project.camera.presentation.components

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.spring
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsPressedAsState
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp

/**
 * Animated record/stop button.
 *
 * - **Idle**: Large red circle (classic camera record button look)
 * - **Recording**: Smaller red rounded-square (stop indicator)
 * - Press animation provides tactile feedback
 */
@Composable
fun RecordingControls(
    isRecording: Boolean,
    onToggleRecording: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val interactionSource = remember { MutableInteractionSource() }
    val isPressed by interactionSource.collectIsPressedAsState()

    val scale by animateFloatAsState(
        targetValue = if (isPressed) 0.85f else 1f,
        animationSpec = spring(stiffness = Spring.StiffnessMedium),
        label = "record_btn_scale",
    )

    val innerColor by animateColorAsState(
        targetValue = if (isRecording) Color(0xFFEF4444) else Color(0xFFDC2626),
        label = "record_btn_color",
    )

    val innerSize by animateFloatAsState(
        targetValue = if (isRecording) 28f else 56f,
        animationSpec = spring(stiffness = Spring.StiffnessMedium),
        label = "record_btn_inner_size",
    )

    val innerCornerRadius by animateFloatAsState(
        targetValue = if (isRecording) 8f else 28f,
        animationSpec = spring(stiffness = Spring.StiffnessMedium),
        label = "record_btn_corner",
    )

    Box(
        modifier = modifier
            .size(72.dp)
            .scale(scale)
            .clip(CircleShape)
            .border(4.dp, Color.White.copy(alpha = 0.9f), CircleShape)
            .clickable(
                interactionSource = interactionSource,
                indication = null,
                onClick = onToggleRecording,
            ),
        contentAlignment = Alignment.Center,
    ) {
        Box(
            modifier = Modifier
                .size(innerSize.dp)
                .clip(RoundedCornerShape(innerCornerRadius.dp))
                .background(innerColor),
        )
    }
}
