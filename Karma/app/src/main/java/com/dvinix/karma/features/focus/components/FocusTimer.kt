package com.dvinix.karma.features.focus.components

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlinx.coroutines.delay

@Composable
fun FocusTimer(durationInMinutes: Int, onTimerFinish: () -> Unit) {
    var timeLeft by remember { mutableStateOf(durationInMinutes * 60) } // Convert minutes to seconds
    var isRunning by remember { mutableStateOf(true) }

    LaunchedEffect(isRunning) {
        while (isRunning && timeLeft > 0) {
            delay(1000) // Delay for 1 second
            timeLeft--
        }
        if (timeLeft == 0) {
            isRunning = false
            onTimerFinish()
        }
    }

    val minutes = timeLeft / 60
    val seconds = timeLeft % 60

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text(
            text = String.format("%02d:%02d", minutes, seconds),
            style = MaterialTheme.typography.headlineLarge,
            fontSize = 48.sp
        )
        Spacer(modifier = Modifier.height(16.dp))
        Button(
            onClick = { isRunning = !isRunning },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text(if (isRunning) "Pause" else "Resume")
        }
    }
}