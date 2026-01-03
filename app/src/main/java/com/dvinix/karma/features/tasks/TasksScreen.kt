package com.dvinix.karma.features.tasks


import android.Manifest
import android.os.Build
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Icon
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.dvinix.karma.features.tasks.components.HorizontalCalendar
import androidx.compose.ui.text.input.ImeAction
import java.util.Calendar
import com.dvinix.karma.R




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



@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TaskInputPopup(
    onAdd: (String, Long?, Int?, Int?) -> Unit
) {

    val permissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission()
    ) { isGranted ->
        // Logic if user grants/denies
    }

    var text by remember { mutableStateOf("") }
    var selectedDateMillis by remember { mutableStateOf<Long?>(null) }
    var selectedHour by remember { mutableStateOf<Int?>(null) }
    var selectedMinute by remember { mutableStateOf<Int?>(null) }

    var showDatePicker by remember { mutableStateOf(false) }
    var showTimePicker by remember { mutableStateOf(false) }
    var reminderEnabled by remember { mutableStateOf(false) }

    val focusRequester = remember { FocusRequester() }

    LaunchedEffect(Unit) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            permissionLauncher.launch(Manifest.permission.POST_NOTIFICATIONS)
        }
    }

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .fillMaxHeight(.5f)
            .padding(24.dp)
            .imePadding()
    ) {
        // Back to BasicTextField for that "clean" look
        BasicTextField(
            value = text,
            onValueChange = { text = it },
            modifier = Modifier
                .fillMaxWidth()
                .focusRequester(focusRequester),
            textStyle = MaterialTheme.typography.headlineSmall.copy(color = Color.White),
            cursorBrush = SolidColor(Color.White),
            keyboardOptions = KeyboardOptions(imeAction = ImeAction.Done),
            keyboardActions = KeyboardActions(
                onDone = { if (text.isNotBlank()) onAdd(text, selectedDateMillis, selectedHour, selectedMinute) }
            ),
            decorationBox = { innerTextField ->
                if (text.isEmpty()) Text("Add a Task...", color = Color.DarkGray, style = MaterialTheme.typography.headlineSmall)
                innerTextField()
            }
        )

        LaunchedEffect(Unit) { focusRequester.requestFocus() }

        Spacer(modifier = Modifier.height(16.dp))

        // Minimalist Reminder Toggle
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .clickable {
                    reminderEnabled = !reminderEnabled
                    if (reminderEnabled) showDatePicker = true
                },
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                painter = painterResource(id = R.drawable.reminder),
                contentDescription = null,
                tint = if (reminderEnabled) Color.White else Color.DarkGray
            )
            Spacer(modifier = Modifier.width(12.dp))
            Text(
                text = if (reminderEnabled) "Reminder: On" else "Add Reminder",
                color = if (reminderEnabled) Color.White else Color.DarkGray
            )
        }
    }

    // Picker Logic
    if (showDatePicker) {
        DatePickerModal(
            onDateSelected = {
                selectedDateMillis = it
                showDatePicker = false
                showTimePicker = true
            },
            onDismiss = { showDatePicker = false }
        )
    }

    if (showTimePicker) {
        TimePickerModal(
            onConfirm = { h, m ->
                selectedHour = h
                selectedMinute = m
                showTimePicker = false
            },
            onDismiss = { showTimePicker = false }
        )
    }
}
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TimePickerModal(
    onConfirm: (Int, Int) -> Unit,
    onDismiss: () -> Unit,
) {
    val currentTime = Calendar.getInstance()
    val timePickerState = rememberTimePickerState(
        initialHour = currentTime.get(Calendar.HOUR_OF_DAY),
        initialMinute = currentTime.get(Calendar.MINUTE),
        is24Hour = false // This enables AM/PM selection
    )

    AlertDialog(
        onDismissRequest = onDismiss,
        confirmButton = {
            TextButton(onClick = { onConfirm(timePickerState.hour, timePickerState.minute) }) {
                Text("Confirm", color = Color.White)
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) { Text("Cancel", color = Color.Gray) }
        },
        containerColor = Color(0xFF1A1A1A), // Charcoal Theme
        text = {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Text("Set Time", color = Color.White, modifier = Modifier.padding(bottom = 16.dp))
                TimeInput(
                    state = timePickerState,
                    colors = TimePickerDefaults.colors(
                        periodSelectorSelectedContainerColor = Color.White,
                        periodSelectorSelectedContentColor = Color.Black,
                        timeSelectorSelectedContainerColor = Color(0xFF333333),
                        timeSelectorSelectedContentColor = Color.White
                    )
                )
            }
        }
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DatePickerModal(
    onDateSelected: (Long?) -> Unit,
    onDismiss: () -> Unit
) {
    val datePickerState = rememberDatePickerState()
    DatePickerDialog(
        onDismissRequest = onDismiss,
        confirmButton = {
            TextButton(onClick = {
                onDateSelected(datePickerState.selectedDateMillis)
                onDismiss()
            }) {
                Text("OK", color = Color.White)
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancel", color = Color.Gray)
            }
        },
        colors = DatePickerDefaults.colors(
            containerColor = Color(0xFF1A1A1A) // Matching your minimalist dark theme
        )
    ) {
        DatePicker(state = datePickerState)
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
            Text(
                text = "KARMA",
                style = MaterialTheme.typography.headlineLarge.copy(
                    fontWeight = FontWeight.Black,
                    letterSpacing = 4.sp
                ),
                color = Color.White,
                modifier = Modifier.padding(top = 8.dp) // Minimal gap from notch
            )

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
                containerColor = Color(0xFF121212),
                // This ensures the sheet reaches the 50% height we set in the Column
                sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
            ) {
                TaskInputPopup(
                    onAdd = { title, date, hour, min ->
                        // Now you can save these to your Room Database!
                        viewModel.addTask(title, date, hour, min)
                        showPopup = false
                    }
                )
            }
        }
    }
}

