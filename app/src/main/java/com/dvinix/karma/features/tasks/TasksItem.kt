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
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.dvinix.karma.core.theme.KarmaTheme
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
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 12.dp),
        // This ensures the checkbox and title are perfectly aligned
        verticalAlignment = Alignment.CenterVertically
    ) {
        RoundCheckbox(
            isCompleted = task.isCompleted,
            onCheckedChange = onToggleCompleted,
            accentColor = Color.White
        )

        Spacer(modifier = Modifier.width(16.dp))

        Text(
            text = task.title,
            style = MaterialTheme.typography.bodyLarge.copy(
                color = if (task.isCompleted) Color.DarkGray else Color.White,
                textDecoration = if (task.isCompleted) TextDecoration.LineThrough else null,
                fontWeight = FontWeight.Medium
            ),
            modifier = Modifier.weight(1f)
        )
        // Delete icon removed to match your minimalist "Inbox" reference
    }
}


@Preview(showBackground = true, backgroundColor = 0x000000)
@Composable
fun KarmaTaskPreview() {
    MaterialTheme {
        Surface(color = Color.Black, modifier = Modifier.fillMaxSize()) {
            TaskItem(
                task = Task(title = "Write blog post", isCompleted = false),
                onToggleCompleted = {}
            )
        }
    }
}
