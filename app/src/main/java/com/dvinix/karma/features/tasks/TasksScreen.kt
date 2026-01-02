package com.dvinix.karma.features.tasks

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.dvinix.karma.core.theme.KarmaTheme
import com.dvinix.karma.features.tasks.components.HorizontalCalendar





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
                Color.Gray.copy(alpha = 0.5f) else Color.Transparent
            Box(
                Modifier.fillMaxSize().background(color, RoundedCornerShape(12.dp)).padding(horizontal = 20.dp),
                contentAlignment = Alignment.CenterEnd
            ) {}
        },
        enableDismissFromStartToEnd = false,
        content = { content() }
    )
}


@Composable
fun TaskInputPopup(onAdd: (String) -> Unit) {
    var text by remember { mutableStateOf("") }
    val focusRequester = remember { FocusRequester() }

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(24.dp)
            .imePadding() // Moves popup up when keyboard appears
    ) {
        BasicTextField(
            value = text,
            onValueChange = { text = it },
            modifier = Modifier
                .fillMaxWidth()
                .focusRequester(focusRequester),
            textStyle = MaterialTheme.typography.headlineSmall.copy(color = Color.White),
            cursorBrush = SolidColor(Color.White),
            decorationBox = { innerTextField ->
                if (text.isEmpty()) Text("I want to...", color = Color.DarkGray, style = MaterialTheme.typography.headlineSmall)
                innerTextField()
            }
        )

        LaunchedEffect(Unit) { focusRequester.requestFocus() }

        Spacer(modifier = Modifier.height(44.dp))

        TextButton(
            onClick = { if (text.isNotBlank()) onAdd(text) },
            modifier = Modifier.align(Alignment.End)
        ) {
            Text("Done", color = Color.White, fontWeight = FontWeight.Bold)
        }
    }
}


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TasksScreen(
    viewModel: TasksViewModel,
    onNavigateToFocus: () -> Unit,
    onNavigateToSettings: () -> Unit
) {
    val tasks by viewModel.uiState.collectAsStateWithLifecycle()
    var showPopup by remember { mutableStateOf(false) }

    Scaffold(
        containerColor = Color.Black,
        topBar = {
            // App Name "KARMA" as requested
            TopAppBar(
                title = {
                    Text(
                        "KARMA",
                        style = MaterialTheme.typography.headlineSmall.copy(
                            fontWeight = FontWeight.ExtraBold,
                            letterSpacing = 2.sp
                        )
                    )
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = Color.Black,
                    titleContentColor = Color.White
                )
            )
        },
        floatingActionButton = {
            FloatingActionButton(
                onClick = { showPopup = true },
                containerColor = Color(0xFF1A1A1A),
                contentColor = Color.White,
                shape = CircleShape
            ) {
                Icon(Icons.Default.Add, contentDescription = null, modifier = Modifier.size(48.dp))
            }
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .padding(padding)
                .padding(horizontal = 20.dp)
                .fillMaxSize()
        ) {
            HorizontalCalendar()

            Text(
                "Inbox",
                style = MaterialTheme.typography.titleLarge.copy(color = Color.White),
                modifier = Modifier.padding(vertical = 12.dp)
            )

            LazyColumn(
                verticalArrangement = Arrangement.spacedBy(4.dp) // Tight, clean spacing
            ) {
                items(tasks, key = { it.id }) { task ->
                    // DELETE BUTTON REMOVED: Now only swipe or check
                    SwipeToDeleteContainer(onDelete = { viewModel.deleteTask(task) }) {
                        TaskItem(
                            task = task,
                            onToggleCompleted = { viewModel.toggleTaskCompletion(task, it) }
                        )
                    }
                }
            }
        }

        if (showPopup) {
            ModalBottomSheet(
                onDismissRequest = { showPopup = false },
                containerColor = Color(0xFF121212)
            ) {
                TaskInputPopup(onAdd = { title ->
                    viewModel.addTask(title)
                    showPopup = false
                })
            }
        }
    }
}

