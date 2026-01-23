package com.dvinix.karma.features.focus

import androidx.compose.animation.core.*
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.dvinix.karma.data.local.Task
import kotlinx.coroutines.delay
import kotlinx.coroutines.isActive

@Composable
fun FocusScreen(
    task: Task? = null,
    onBack: () -> Unit = {}
) {
    var timeLeftInSeconds by remember { mutableStateOf(25 * 60) } // 25 minutes default
    var isRunning by remember { mutableStateOf(false) }
    var totalTime by remember { mutableStateOf(25 * 60) }

    // Timer logic
    LaunchedEffect(isRunning) {
        while (isRunning && timeLeftInSeconds > 0 && isActive) {
            delay(1000)
            timeLeftInSeconds--
        }
        if (timeLeftInSeconds == 0) {
            isRunning = false
        }
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.Black)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {

            Spacer(modifier = Modifier.weight(1f))

            // Circular Timer - Click to Play/Pause
            Box(
                modifier = Modifier
                    .size(280.dp)
                    .clickable(
                        onClick = { isRunning = !isRunning },
                        indication = null,
                        interactionSource = remember { MutableInteractionSource() }
                    ),
                contentAlignment = Alignment.Center
            ) {
                // Background circle
                Canvas(modifier = Modifier.fillMaxSize()) {
                    drawCircle(
                        color = Color(0xFF1A1A1A),
                        style = Stroke(width = 20f)
                    )
                }

                // Progress circle
                val progress = timeLeftInSeconds.toFloat() / totalTime.toFloat()
                Canvas(modifier = Modifier.fillMaxSize()) {
                    drawArc(
                        color = Color.White,
                        startAngle = -90f,
                        sweepAngle = 360f * progress,
                        useCenter = false,
                        style = Stroke(width = 20f, cap = StrokeCap.Round)
                    )
                }

                // Time text
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    val minutes = timeLeftInSeconds / 60
                    val seconds = timeLeftInSeconds % 60
                    Text(
                        text = String.format("%02d:%02d", minutes, seconds),
                        color = Color.White,
                        fontSize = 64.sp,
                        fontWeight = FontWeight.Light,
                        letterSpacing = 2.sp
                    )
                }
            }

            Spacer(modifier = Modifier.weight(1f))

            // Reset button (minimal)
            TextButton(
                onClick = {
                    isRunning = false
                    timeLeftInSeconds = totalTime
                },
                modifier = Modifier.padding(bottom = 32.dp)
            ) {
                Text(
                    text = "Reset",
                    color = Color.Gray,
                    fontSize = 16.sp
                )
            }


        }
    }
}
