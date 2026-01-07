package com.dvinix.karma.features.tasks


import android.Manifest
import android.os.Build
import android.widget.Space
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.border
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
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowRight
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.KeyboardArrowRight
import androidx.compose.material.icons.outlined.List
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.input.key.Key
import androidx.compose.ui.input.key.KeyEventType
import androidx.compose.ui.input.key.key
import androidx.compose.ui.input.key.onPreviewKeyEvent
import androidx.compose.ui.input.key.type
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.dvinix.karma.features.tasks.components.HorizontalCalendar
import com.dvinix.karma.features.tasks.components.CategorySelector
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.style.TextAlign
import java.util.Calendar
import com.dvinix.karma.R
import androidx.compose.ui.tooling.preview.Preview
import com.dvinix.karma.core.theme.KarmaTheme


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

    val onPerformAdd = {
        if (text.isNotBlank()) {

            onAdd(text, selectedDateMillis, selectedHour, selectedMinute)
            text = ""
            reminderEnabled = false
            selectedDateMillis = null

        }
    }
    LaunchedEffect(Unit) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            permissionLauncher.launch(Manifest.permission.POST_NOTIFICATIONS)
        }
    }

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .fillMaxHeight(.4f)
            .padding(24.dp)
            .imePadding()
    ) {
        // 1. Placeholder above the field
        Text(
            text = "Pause. What matters right now?",
            color = Color.Gray,
            fontSize = 22.sp,
            fontWeight = FontWeight.Medium,
            style = MaterialTheme.typography.bodyMedium,
            textAlign = TextAlign.Center,
            modifier = Modifier.padding(bottom = 16.dp)
        )

        // 2. Gradient Curved Border Input Field
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(64.dp)
                .background(
                    color = Color(0xFFF5F5F5), // Light off-white background
                    shape = RoundedCornerShape(16.dp)
                )

                .border(
                    width = 1.dp,
                    brush = Brush.linearGradient(
                        colors = listOf(Color.White.copy(alpha = 0.5f), Color.Gray.copy(alpha = 0.2f))
                    ),
                    shape = RoundedCornerShape(16.dp)
                )
                .padding(horizontal = 20.dp),
            contentAlignment = Alignment.CenterStart
        ) {
            BasicTextField(
                value = text,
                onValueChange = { text = it },
                modifier = Modifier
                    .fillMaxWidth()
                    .focusRequester(focusRequester)
                    .onPreviewKeyEvent {

                        if (it.key == Key.Enter && it.type == KeyEventType.KeyDown) {
                            if (text.isNotBlank()) onAdd(text, selectedDateMillis, selectedHour, selectedMinute)
                            true
                        } else false
                    },
                textStyle = MaterialTheme.typography.bodyLarge.copy(color = Color.DarkGray),
                cursorBrush = SolidColor(Color.DarkGray),
                keyboardOptions = KeyboardOptions(imeAction = ImeAction.Done),
                keyboardActions = KeyboardActions(
                    onDone = { if (text.isNotBlank()) onAdd(text, selectedDateMillis, selectedHour, selectedMinute) }
                )
            )
        }

        LaunchedEffect(Unit) { focusRequester.requestFocus() }

        Spacer(modifier = Modifier.height(24.dp))

        // 3. Add Reminder Card (Matching the image style)
        Surface(
            onClick = {
                reminderEnabled = !reminderEnabled
                if (reminderEnabled) showDatePicker = true
            },
            shape = RoundedCornerShape(16.dp),
            color = Color.White,
            shadowElevation = 2.dp,
            modifier = Modifier.fillMaxWidth().height(60.dp)
        ) {
            Row(
                modifier = Modifier.padding(horizontal = 16.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    painter = painterResource(id = R.drawable.reminder),
                    contentDescription = null,
                    tint = if (reminderEnabled) Color(0xFF2E4D44) else Color.Gray,
                    modifier = Modifier.size(24.dp)
                )
                Spacer(modifier = Modifier.width(16.dp))
                Text(
                    text = if (reminderEnabled) "Reminder: Set" else "Add Reminder",
                    color = if (reminderEnabled) Color(0xFF2E4D44) else Color.Gray,
                    modifier = Modifier.weight(1f)
                )
                Icon(
                    imageVector = Icons.Filled.KeyboardArrowRight,
                    contentDescription = null,
                    tint = Color.LightGray
                )
            }
        }

        Spacer(modifier = Modifier.height(24.dp))

        Button(
            onClick = { onPerformAdd() },
            modifier = Modifier
                .fillMaxWidth()
                .height(56.dp),
            shape = RoundedCornerShape(28.dp),
            colors = ButtonDefaults.buttonColors(
                containerColor = Color(0xFF4A4A4A), // Dark Charcoal
                contentColor = Color.White
            )
        ) {
            Text("Add Task", style = MaterialTheme.typography.titleMedium)
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
    val categories by viewModel.categories.collectAsStateWithLifecycle()
    val selectedCategory by viewModel.selectedCategory.collectAsStateWithLifecycle()
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
                modifier = Modifier.padding(top = 8.dp)
            )

            HorizontalCalendar()

            // Category Selector
            CategorySelector(
                categories = categories,
                selectedCategory = selectedCategory,
                onCategorySelect = { viewModel.selectCategory(it) },
                onAddCategory = { viewModel.addCategory(it) },
                onDeleteCategory = { viewModel.deleteCategory(it) }
            )

            // Task List or Empty State
            if (tasks.isEmpty()) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .weight(1f),
                    contentAlignment = Alignment.TopCenter
                ) {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        modifier =  Modifier.padding(top = 120.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Outlined.List,
                            contentDescription = "No Tasks",
                            tint = Color.Gray.copy(alpha = 0.3f),
                            modifier = Modifier.size(120.dp)
                        )
                        Text(
                            text = "What Matters Now?",
                            color = Color.White.copy(alpha = 0.6f),
                            fontSize = 16.sp,
                            textAlign = TextAlign.Center
                        )
                    }
                }
            } else {
                LazyColumn(
                    modifier = Modifier
                        .fillMaxWidth()
                        .weight(1f),
                    verticalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    items(tasks, key = { it.id }) { task ->

                        SwipeToDeleteContainer(onDelete = { viewModel.deleteTask(task) }) {
                            TaskItem(
                                task = task,
                                onToggleCompleted = { viewModel.toggleTaskCompletion(task, it) }
                            )
                        }
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
                        // Add task to the currently selected category
                        viewModel.addTask(title, date, hour, min, selectedCategory)
                        showPopup = false
                    }
                )
            }
        }
    }
}


@Preview(showBackground = true, backgroundColor = 0XFF000000)
@Composable
fun TaskScreen(){}


