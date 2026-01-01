package com.dvinix.karma.features.tasks

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.dp
import com.dvinix.karma.data.local.Task




@Composable
fun RoundCheckbox(
    isCompleted: Boolean,
    onCheckedChange: (Boolean) -> Unit,
    accentColor: Color
) {
    Box(
        modifier = Modifier
            .size(24.dp)
            .clip(CircleShape)
            .background(if (isCompleted) accentColor else Color.Transparent)
            .border(2.dp, if (isCompleted) accentColor else Color.DarkGray, CircleShape)
            .clickable { onCheckedChange(!isCompleted) },
        contentAlignment = Alignment.Center
    ) {
        if (isCompleted) {
            Icon(
                imageVector = Icons.Default.Check,
                contentDescription = null,
                modifier = Modifier.size(16.dp),
                tint = Color.Black // Contrast against the accent color
            )
        }
    }
}


@Composable
fun TaskItem(
    task: Task,
    onToggleCompleted: (Boolean) -> Unit
) {
    Surface(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(12.dp),
        color = Color(0xFF1A1A1A)
    ) {
        Row(
            modifier = Modifier.height(IntrinsicSize.Min),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Colored Accent Line
            Box(
                modifier = Modifier
                    .fillMaxHeight()
                    .width(4.dp)
                    .background(Color(task.colorHex))
            )

            Column(
                modifier = Modifier
                    .padding(16.dp)
                    .weight(1f)
            ) {
                Text(
                    text = task.title,
                    style = MaterialTheme.typography.titleMedium.copy(
                        fontWeight = FontWeight.Bold,
                        color = if (task.isCompleted) Color.Gray else Color.White,
                        textDecoration = if (task.isCompleted) TextDecoration.LineThrough else null
                    )
                )
                if (task.description.isNotEmpty()) {
                    Text(
                        text = task.description,
                        style = MaterialTheme.typography.bodySmall.copy(
                            color = if (task.isCompleted) Color.DarkGray else Color.Gray
                        )
                    )
                }
            }

            // The New Round Checkbox
            Box(modifier = Modifier.padding(end = 16.dp)) {
                RoundCheckbox(
                    isCompleted = task.isCompleted,
                    onCheckedChange = onToggleCompleted,
                    accentColor = Color(task.colorHex)
                )
            }
        }
    }
}