package com.dvinix.karma.features.tasks

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle




@Composable
fun ColorPickerRow(
    selectedColor: Color,
    onColorSelected: (Color) -> Unit
) {
    val colors = listOf(
        Color(0xFFEF5350), // Red
        Color(0xFF66BB6A), // Green
        Color(0xFF42A5F5), // Blue
        Color(0xFFFFCA28), // Amber
        Color(0xFFAB47BC), // Purple
        Color(0xFF8D6E63)  // Brown
    )

    Row(
        modifier = Modifier.padding(vertical = 8.dp),
        horizontalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        colors.forEach { color ->
            Surface(
                modifier = Modifier
                    .size(32.dp)
                    .clickable { onColorSelected(color) },
                shape = CircleShape,
                color = color,
                border = if (selectedColor == color)
                    BorderStroke(2.dp, Color.White) else null
            ) {}
        }
    }
}


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SwipeToDeleteContainer(
    onDelete: () -> Unit,
    content: @Composable () -> Unit
) {
    val dismissState = rememberSwipeToDismissBoxState(
        confirmValueChange = { value ->
            if (value == SwipeToDismissBoxValue.EndToStart) {
                onDelete()
                true
            } else false
        }
    )

    SwipeToDismissBox(
        state = dismissState,
        backgroundContent = {
            val color = if (dismissState.dismissDirection == SwipeToDismissBoxValue.EndToStart)
                Color.Red.copy(alpha = 0.5f) else Color.Transparent
            Box(
                Modifier.fillMaxSize().background(color, RoundedCornerShape(12.dp)).padding(horizontal = 20.dp),
                contentAlignment = Alignment.CenterEnd
            ) {
                Icon(Icons.Default.Delete, contentDescription = "Delete", tint = Color.White)
            }
        },
        enableDismissFromStartToEnd = false,
        content = { content() }
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TasksScreen(
    viewModel: TasksViewModel,
    onNavigateToFocus: () -> Unit
) {
    val tasks by viewModel.uiState.collectAsStateWithLifecycle()

    // States for our new dynamic fields
    var taskTitle by remember { mutableStateOf("") }
    var taskDescription by remember { mutableStateOf("") }
    var selectedColor by remember { mutableStateOf(0xFF42A5F5) } // Default Blue

    Scaffold(
        containerColor = Color.Black, // Aesthetic pure black background
        topBar = {
            CenterAlignedTopAppBar(
                title = { Text("KARMA", style = MaterialTheme.typography.headlineSmall.copy(fontWeight = FontWeight.Light, letterSpacing = 4.sp)) },
                colors = TopAppBarDefaults.centerAlignedTopAppBarColors(containerColor = Color.Black, titleContentColor = Color.White)
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .padding(padding)
                .padding(horizontal = 20.dp)
                .fillMaxSize()
        ) {
            // --- Input Section ---
            Column(modifier = Modifier.padding(vertical = 16.dp)) {
                BasicTextField(
                    value = taskTitle,
                    onValueChange = { taskTitle = it },
                    textStyle = MaterialTheme.typography.titleLarge.copy(color = Color.White, fontWeight = FontWeight.Bold),
                    cursorBrush = SolidColor(Color.White),
                    decorationBox = { innerTextField ->
                        if (taskTitle.isEmpty()) Text("Task Title", color = Color.DarkGray, style = MaterialTheme.typography.titleLarge)
                        innerTextField()
                    }
                )

                Spacer(modifier = Modifier.height(8.dp))

                BasicTextField(
                    value = taskDescription,
                    onValueChange = { taskDescription = it },
                    textStyle = MaterialTheme.typography.bodyMedium.copy(color = Color.Gray),
                    cursorBrush = SolidColor(Color.Gray),
                    decorationBox = { innerTextField ->
                        if (taskDescription.isEmpty()) Text("Short description...", color = Color.DarkGray)
                        innerTextField()
                    }
                )

                Spacer(modifier = Modifier.height(16.dp))

                // Minimalist Color Picker
                ColorPickerRow(selectedColor = Color(selectedColor)) {
                    selectedColor = it.toArgb().toLong()
                }

                Button(
                    onClick = {
                        if (taskTitle.isNotBlank()) {
                            viewModel.addTask(taskTitle, taskDescription, selectedColor)
                            taskTitle = ""; taskDescription = ""
                        }
                    },
                    modifier = Modifier.fillMaxWidth().padding(top = 12.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = Color(selectedColor)),
                    shape = RoundedCornerShape(8.dp)
                ) {
                    Text("Add Task", fontWeight = FontWeight.Bold)
                }
            }

            // --- Swipeable List Section ---
            LazyColumn(
                modifier = Modifier.weight(1f),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                items(
                    items = tasks,
                    key = { it.id } // Vital for smooth animations and swipe performance
                ) { task ->
                    // 1. The Container handles the "Background" (Delete gesture)
                    SwipeToDeleteContainer(
                        onDelete = { viewModel.deleteTask(task) }
                    ) {
                        // 2. The TaskItem handles the "Foreground" (Checkbox and text)
                        TaskItem(
                            task = task,
                            onToggleCompleted = { isChecked ->
                                viewModel.toggleTaskCompletion(task, isChecked)
                            }
                        )
                    }
                }
            }
        }
    }
}