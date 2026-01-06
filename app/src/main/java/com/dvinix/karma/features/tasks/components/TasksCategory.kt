package com.dvinix.karma.features.tasks.components

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import java.util.UUID

/**
 * Data model for your categories.
 * Professional Tip: Using IDs allows for safe task association even if names change.
 */
data class Category(
    val id: String = UUID.randomUUID().toString(),
    val name: String,
    val tasks: List<String> = emptyList() // Each category holds its own list of tasks
)

@Composable
fun CategoryHeader(
    modifier: Modifier = Modifier,
    onCategorySelected: (Category) -> Unit = {},
    onCategoryAdded: () -> Unit = {} // Added parameter for external handling
) {
    // State management for categories
    var categories by remember {
        mutableStateOf(listOf(
            Category(name = "Inbox"),
            Category(name = "Work"),
            Category(name = "Personal")
        ))
    }
    var selectedCategoryId by remember { mutableStateOf(categories.first().id) }

    // UI states for Dialogs
    var showAddDialog by remember { mutableStateOf(false) }
    var editingCategory by remember { mutableStateOf<Category?>(null) }
    var categoryNameInput by remember { mutableStateOf("") }

    Column(modifier = modifier.fillMaxWidth()) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 8.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            // Left Side: Display Selected Category Name
            Text(
                text = categories.find { it.id == selectedCategoryId }?.name ?: "Categories",
                style = MaterialTheme.typography.titleLarge.copy(color = Color.White)
            )

            // Right Side: Add Category Button
            IconButton(
                onClick = {
                    categoryNameInput = ""
                    showAddDialog = true
                    onCategoryAdded() // Triggers the external callback provided in the query
                },
                modifier = Modifier
                    .size(32.dp)
                    .background(Color.White.copy(alpha = 0.1f), RoundedCornerShape(8.dp))
            ) {
                Icon(
                    imageVector = Icons.Default.Add,
                    contentDescription = "Add Category",
                    tint = Color.White
                )
            }
        }

        // Horizontal Category Switcher
        LazyRow(
            contentPadding = PaddingValues(horizontal = 16.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            modifier = Modifier.padding(bottom = 12.dp)
        ) {
            items(categories, key = { it.id }) { category ->
                CategoryChip(
                    category = category,
                    isSelected = category.id == selectedCategoryId,
                    onClick = {
                        selectedCategoryId = category.id
                        onCategorySelected(category)
                    },
                    onLongClick = {
                        editingCategory = category
                        categoryNameInput = category.name
                    }
                )
            }
        }
    }

    // Add/Edit Dialog Logic
    if (showAddDialog || editingCategory != null) {
        AlertDialog(
            onDismissRequest = {
                showAddDialog = false
                editingCategory = null
            },
            title = { Text(if (showAddDialog) "New Category" else "Edit Category") },
            text = {
                OutlinedTextField(
                    value = categoryNameInput,
                    onValueChange = { categoryNameInput = it },
                    label = { Text("Category Name") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )
            },
            confirmButton = {
                TextButton(
                    onClick = {
                        if (categoryNameInput.isNotBlank()) {
                            if (showAddDialog) {
                                categories = categories + Category(name = categoryNameInput)
                            } else {
                                categories = categories.map {
                                    if (it.id == editingCategory?.id) it.copy(name = categoryNameInput) else it
                                }
                            }
                        }
                        showAddDialog = false
                        editingCategory = null
                    }
                ) {
                    Text("Save")
                }
            },
            dismissButton = {
                if (editingCategory != null) {
                    TextButton(
                        onClick = {
                            categories = categories.filter { it.id != editingCategory?.id }
                            if (selectedCategoryId == editingCategory?.id && categories.isNotEmpty()) {
                                selectedCategoryId = categories.first().id
                            }
                            editingCategory = null
                        }
                    ) {
                        Text("Delete", color = MaterialTheme.colorScheme.error)
                    }
                }
            }
        )
    }
}

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun CategoryChip(
    category: Category,
    isSelected: Boolean,
    onClick: () -> Unit,
    onLongClick: () -> Unit
) {
    Box(
        modifier = Modifier
            .background(
                color = if (isSelected) Color.White else Color.White.copy(alpha = 0.05f),
                shape = RoundedCornerShape(20.dp)
            )
            .combinedClickable(
                onClick = onClick,
                onLongClick = onLongClick
            )
            .padding(horizontal = 16.dp, vertical = 6.dp)
    ) {
        Text(
            text = category.name,
            style = MaterialTheme.typography.labelLarge,
            color = if (isSelected) Color.Black else Color.White
        )
    }
}