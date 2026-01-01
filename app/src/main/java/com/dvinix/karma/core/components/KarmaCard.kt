package com.dvinix.karma.core.components

import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Card
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

@Composable
fun KarmaCard(text: String) {
    Card(modifier = Modifier.padding(8.dp)) {
        Text(text = text, modifier = Modifier.padding(16.dp))
    }
}
