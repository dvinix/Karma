package com.dvinix.karma.features.tasks.components

import android.widget.Space
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
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

    // Edit Categories Dialog
    if (showEditDialog) {
        AlertDialog(
            onDismissRequest = { showEditDialog = false },
            containerColor = Color(0xFF1A1A1A),
            title = {
                Text("Manage Categories", color = Color.White, fontWeight = FontWeight.Bold)
            },
            text = {
                Column {
                    categories.forEach { category ->
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 8.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = category,
                                color = Color.White,
                                modifier = Modifier.weight(1f)
                            )
                            if (category != "Inbox") {
                                IconButton(
                                    onClick = { categoryToDelete = category },
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

                    Spacer(modifier = Modifier.height(16.dp))

                    Button(
                        onClick = { showAddDialog = true },
                        colors = ButtonDefaults.buttonColors(
                            containerColor = Color.White,
                            contentColor = Color.Black
                        ),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Icon(Icons.Default.Add, contentDescription = null, modifier = Modifier.size(18.dp))
                        Spacer(modifier = Modifier.width(8.dp))
                        Text("Add Category")
                    }
                }
            },
            confirmButton = {
                TextButton(onClick = { showEditDialog = false }) {
                    Text("Done", color = Color.White)
                }
            }
        )
    }

    // Add Category Dialog
    if (showAddDialog) {
        var newCategoryName by remember { mutableStateOf("") }

        AlertDialog(
            onDismissRequest = { showAddDialog = false },
            containerColor = Color(0xFF1A1A1A),
            title = {
                Text("New Category", color = Color.White, fontWeight = FontWeight.Bold)
            },
            text = {
                OutlinedTextField(
                    value = newCategoryName,
                    onValueChange = { newCategoryName = it },
                    placeholder = { Text("Category name", color = Color.Gray) },
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedTextColor = Color.White,
                        unfocusedTextColor = Color.White,
                        focusedBorderColor = Color.White,
                        unfocusedBorderColor = Color.Gray,
                        cursorColor = Color.White
                    ),
                    singleLine = true
                )
            },
            confirmButton = {
                TextButton(
                    onClick = {
                        if (newCategoryName.isNotBlank()) {
                            onAddCategory?.invoke(newCategoryName)
                            showAddDialog = false
                        }
                    }
                ) {
                    Text("Add", color = Color.White)
                }
            },
            dismissButton = {
                TextButton(onClick = { showAddDialog = false }) {
                    Text("Cancel", color = Color.Gray)
                }
            }
        )
    }

    // Delete Confirmation Dialog
    categoryToDelete?.let { category ->
        AlertDialog(
            onDismissRequest = { categoryToDelete = null },
            containerColor = Color(0xFF1A1A1A),
            title = {
                Text("Delete Category?", color = Color.White, fontWeight = FontWeight.Bold)
            },
            text = {
                Text(
                    "All tasks in \"$category\" will be moved to Inbox.",
                    color = Color.Gray
                )
            },
            confirmButton = {
                TextButton(
                    onClick = {
                        onDeleteCategory?.invoke(category)
                        categoryToDelete = null
                        showEditDialog = false
                    }
                ) {
                    Text("Delete", color = Color.Red)
                }
            },
            dismissButton = {
                TextButton(onClick = { categoryToDelete = null }) {
                    Text("Cancel", color = Color.Gray)
                }
            }
        )
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