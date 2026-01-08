package com.dvinix.karma.features.tasks.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.outlined.List
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.dvinix.karma.core.theme.KarmaTheme

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CategorySelector(
    categories: List<String>,
    selectedCategory: String,
    onCategorySelect: (String) -> Unit,
    onAddCategory: ((String) -> Unit)? = null,
    onDeleteCategory: ((String) -> Unit)? = null,
    hasTasksInCategory: Boolean = false,
    modifier: Modifier = Modifier
) {
    var showEditDialog by remember { mutableStateOf(false) }
    var showAddDialog by remember { mutableStateOf(false) }
    var categoryToDelete by remember { mutableStateOf<String?>(null) }

    Row(
        modifier = modifier
            .fillMaxWidth()
            .padding(vertical = 12.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        // Scrollable category pills
        Row(
            modifier = Modifier
                .weight(1f)
                .horizontalScroll(rememberScrollState()),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            categories.forEach { category ->
                Surface(
                    onClick = { onCategorySelect(category) },
                    shape = RoundedCornerShape(20.dp),
                    color = if (category == selectedCategory) 
                        Color.White.copy(alpha = 0.15f) 
                    else Color(0xFF1A1A1A),
                    modifier = Modifier.height(36.dp)
                ) {
                    Box(
                        contentAlignment = Alignment.Center,
                        modifier = Modifier.padding(horizontal = 16.dp)
                    ) {
                        Text(
                            text = category,
                            color = if (category == selectedCategory) Color.White else Color.Gray,
                            fontSize = 14.sp,
                            fontWeight = if (category == selectedCategory) FontWeight.SemiBold else FontWeight.Normal
                        )
                    }
                }
            }
        }


        Spacer(modifier = Modifier.width(8.dp))

        // Edit icon button
        IconButton(
            onClick = { showEditDialog = true },
            modifier = Modifier
                .size(36.dp)
                .clip(CircleShape)
                .background(Color(0xFF1A1A1A))
        ) {
            Icon(
                imageVector = Icons.Default.Edit,
                contentDescription = "Edit Categories",
                tint = Color.Gray,
                modifier = Modifier.size(18.dp)
            )
        }
    }

    // Edit Categories Sheet
    if (showEditDialog) {
        ModalBottomSheet(
            onDismissRequest = { showEditDialog = false },
            containerColor = Color(0xFF121212),
            sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
        ) {
            ManageCategoriesContent(
                categories = categories,
                onAddCategory = { showAddDialog = true },
                onDeleteCategory = { categoryToDelete = it },
                onDismiss = { showEditDialog = false }
            )
        }
    }

    // Add Category Sheet
    if (showAddDialog) {
        ModalBottomSheet(
            onDismissRequest = { showAddDialog = false },
            containerColor = Color(0xFF121212),
            sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
        ) {
            AddCategoryContent(
                onAdd = { name ->
                    onAddCategory?.invoke(name)
                    showAddDialog = false
                }
            )
        }
    }

    // Delete Confirmation Sheet
    categoryToDelete?.let { category ->
        ModalBottomSheet(
            onDismissRequest = { categoryToDelete = null },
            containerColor = Color(0xFF121212),
            sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
        ) {
            DeleteCategoryContent(
                category = category,
                onConfirm = {
                    onDeleteCategory?.invoke(category)
                    categoryToDelete = null
                    showEditDialog = false
                },
                onCancel = { categoryToDelete = null }
            )
        }
    }
}

@Composable
fun ManageCategoriesContent(
    categories: List<String>,
    onAddCategory: () -> Unit,
    onDeleteCategory: (String) -> Unit,
    onDismiss: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .fillMaxHeight(0.6f)
            .padding(24.dp)
    ) {
        Text(
            "Manage Categories",
            color = Color.White,
            fontWeight = FontWeight.Bold,
            fontSize = 20.sp,
            modifier = Modifier.padding(bottom = 16.dp)
        )

        // Scrollable categories list
        LazyColumn(
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f),
            verticalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            items(categories) { category ->
                Surface(
                    shape = RoundedCornerShape(12.dp),
                    color = Color(0xFF2A2A2A),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 16.dp, vertical = 12.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = category,
                            color = Color.White,
                            modifier = Modifier.weight(1f)
                        )
                        if (category != "Inbox") {
                            IconButton(
                                onClick = { onDeleteCategory(category) },
                                modifier = Modifier.size(32.dp)
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Close,
                                    contentDescription = "Delete",
                                    tint = Color.Red,
                                    modifier = Modifier.size(18.dp)
                                )
                            }
                        }
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        Button(
            onClick = onAddCategory,
            colors = ButtonDefaults.buttonColors(
                containerColor = Color.White,
                contentColor = Color.Black
            ),
            shape = RoundedCornerShape(28.dp),
            modifier = Modifier
                .fillMaxWidth()
                .height(56.dp)
        ) {
            Icon(Icons.Default.Add, contentDescription = null, modifier = Modifier.size(18.dp))
            Spacer(modifier = Modifier.width(8.dp))
            Text("Add Category", style = MaterialTheme.typography.titleMedium)
        }

        Spacer(modifier = Modifier.height(8.dp))

        TextButton(
            onClick = onDismiss,
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Done", color = Color.White)
        }
    }
}

@Composable
fun AddCategoryContent(
    onAdd: (String) -> Unit
) {
    var newCategoryName by remember { mutableStateOf("") }
    val focusRequester = remember { FocusRequester() }

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .fillMaxHeight(0.4f)
            .padding(24.dp)
            .imePadding()
    ) {
        Text(
            text = "Create a new category",
            color = Color.Gray,
            fontSize = 22.sp,
            fontWeight = FontWeight.Medium,
            textAlign = TextAlign.Center,
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 16.dp)
        )

        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(64.dp)
                .background(
                    color = Color(0xFFF5F5F5),
                    shape = RoundedCornerShape(16.dp)
                )
                .border(
                    width = 1.dp,
                    brush = Brush.linearGradient(
                        colors = listOf(
                            Color.White.copy(alpha = 0.5f),
                            Color.Gray.copy(alpha = 0.2f)
                        )
                    ),
                    shape = RoundedCornerShape(16.dp)
                )
                .padding(horizontal = 20.dp),
            contentAlignment = Alignment.CenterStart
        ) {
            BasicTextField(
                value = newCategoryName,
                onValueChange = { newCategoryName = it },
                modifier = Modifier
                    .fillMaxWidth()
                    .focusRequester(focusRequester),
                textStyle = MaterialTheme.typography.bodyLarge.copy(color = Color.DarkGray),
                cursorBrush = SolidColor(Color.DarkGray),
                singleLine = true,
                keyboardOptions = KeyboardOptions(imeAction = ImeAction.Done),
                keyboardActions = KeyboardActions(
                    onDone = {
                        if (newCategoryName.isNotBlank()) {
                            onAdd(newCategoryName)
                        }
                    }
                ),
                decorationBox = { innerTextField ->
                    if (newCategoryName.isEmpty()) {
                        Text(
                            "Category name",
                            color = Color.Gray,
                            style = MaterialTheme.typography.bodyLarge
                        )
                    }
                    innerTextField()
                }
            )
        }

        LaunchedEffect(Unit) { focusRequester.requestFocus() }

        Spacer(modifier = Modifier.height(24.dp))

        Button(
            onClick = {
                if (newCategoryName.isNotBlank()) {
                    onAdd(newCategoryName)
                }
            },
            modifier = Modifier
                .fillMaxWidth()
                .height(56.dp),
            shape = RoundedCornerShape(28.dp),
            colors = ButtonDefaults.buttonColors(
                containerColor = Color(0xFF4A4A4A),
                contentColor = Color.White
            )
        ) {
            Text("Add Category", style = MaterialTheme.typography.titleMedium)
        }
    }
}

@Composable
fun DeleteCategoryContent(
    category: String,
    onConfirm: () -> Unit,
    onCancel: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .fillMaxHeight(0.3f)
            .padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            "Delete Category?",
            color = Color.White,
            fontWeight = FontWeight.Bold,
            fontSize = 20.sp,
            modifier = Modifier.padding(bottom = 8.dp)
        )

        Text(
            "All tasks in \"$category\" will be moved to Inbox.",
            color = Color.Gray,
            textAlign = TextAlign.Center,
            modifier = Modifier.padding(bottom = 24.dp)
        )

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Button(
                onClick = onCancel,
                modifier = Modifier
                    .weight(1f)
                    .height(56.dp),
                shape = RoundedCornerShape(28.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color(0xFF2A2A2A),
                    contentColor = Color.White
                )
            ) {
                Text("Cancel", style = MaterialTheme.typography.titleMedium)
            }

            Button(
                onClick = onConfirm,
                modifier = Modifier
                    .weight(1f)
                    .height(56.dp),
                shape = RoundedCornerShape(28.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color.Red,
                    contentColor = Color.White
                )
            ) {
                Text("Delete", style = MaterialTheme.typography.titleMedium)
            }
        }
    }
}


@Preview(showBackground = true, backgroundColor = 0xFF000000)
@Composable
fun CategorySelectorEmptyPreview() {
    KarmaTheme {
        CategorySelector(
            categories = listOf("Inbox", "Work", "Personal"),
            selectedCategory = "Work",
            onCategorySelect = {},
            onAddCategory = {},
            onDeleteCategory = {},
            hasTasksInCategory = false // Shows empty state
        )
    }
}

@Preview(showBackground = true, backgroundColor = 0xFF000000)
@Composable
fun CategorySelectorSingleCategoryPreview() {
    KarmaTheme {
        CategorySelector(
            categories = listOf("Inbox"),
            selectedCategory = "Inbox",
            onCategorySelect = {},
            onAddCategory = {},
            onDeleteCategory = {},
            hasTasksInCategory = true
        )
    }
}