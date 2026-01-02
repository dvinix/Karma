package com.dvinix.karma.features.tasks.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import java.time.DayOfWeek
import java.time.LocalDate
import java.time.format.TextStyle
import java.time.temporal.TemporalAdjusters
import java.util.*

// Data class to hold our dynamic date info
data class CalendarDay(
    val dayName: String,
    val dayNumber: Int,
    val fullDate: LocalDate
)

@Composable
fun HorizontalCalendar(modifier: Modifier = Modifier) {
    val today = remember { LocalDate.now() }
    val currentWeek = remember {
        // Find Monday of the current week
        val monday = today.with(TemporalAdjusters.previousOrSame(DayOfWeek.MONDAY))
        (0..6).map { i ->
            val date = monday.plusDays(i.toLong())
            CalendarDay(
                dayName = date.dayOfWeek.getDisplayName(TextStyle.SHORT, Locale.getDefault()).uppercase(),
                dayNumber = date.dayOfMonth,
                fullDate = date
            )
        }
    }

    Column(modifier = modifier.fillMaxWidth().padding(vertical = 16.dp)) {
        // Current Month Header
        Text(
            text = today.month.getDisplayName(TextStyle.FULL, Locale.getDefault()),
            color = Color.Gray,
            style = MaterialTheme.typography.labelMedium,
            modifier = Modifier.padding(horizontal = 4.dp)
        )

        Spacer(modifier = Modifier.height(12.dp))

        LazyRow(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            items(currentWeek) { day ->
                val isToday = day.fullDate == today

                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    modifier = Modifier
                        .clip(RoundedCornerShape(12.dp))
                        .background(if (isToday) Color(0xFF1A1A1A) else Color.Transparent)
                        .padding(horizontal = 12.dp, vertical = 10.dp)
                ) {
                    Text(
                        text = day.dayName,
                        color = if (isToday) Color.White else Color.DarkGray,
                        style = MaterialTheme.typography.labelSmall
                    )
                    Text(
                        text = day.dayNumber.toString(),
                        color = Color.White,
                        fontWeight = if (isToday) FontWeight.Bold else FontWeight.Normal,
                        style = MaterialTheme.typography.titleMedium
                    )
                }
            }
        }
    }
}