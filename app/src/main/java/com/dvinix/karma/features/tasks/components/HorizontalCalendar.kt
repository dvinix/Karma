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
import java.text.SimpleDateFormat
import java.util.*

// Data class to hold our dynamic date info
data class CalendarDay(
    val dayName: String,
    val dayNumber: Int,
    val fullDate: Calendar
)

@Composable
fun HorizontalCalendar(modifier: Modifier = Modifier) {
    val today = remember { Calendar.getInstance() }
    val currentWeek = remember {
        // Find Monday of the current week
        val calendar = Calendar.getInstance()
        calendar.set(Calendar.DAY_OF_WEEK, Calendar.MONDAY)
        (0..6).map { i ->
            val date = calendar.clone() as Calendar
            date.add(Calendar.DAY_OF_WEEK, i)
            CalendarDay(
                dayName = SimpleDateFormat("EEE", Locale.getDefault()).format(date.time).uppercase(),
                dayNumber = date.get(Calendar.DAY_OF_MONTH),
                fullDate = date
            )
        }
    }

    Column(modifier = modifier.fillMaxWidth().padding(vertical = 16.dp)) {
        // Current Month Header
        Text(
            text = SimpleDateFormat("MMMM", Locale.getDefault()).format(today.time),
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
                val isToday = isSameDay(day.fullDate, today)

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

private fun isSameDay(cal1: Calendar, cal2: Calendar): Boolean {
    return cal1.get(Calendar.YEAR) == cal2.get(Calendar.YEAR) &&
            cal1.get(Calendar.DAY_OF_YEAR) == cal2.get(Calendar.DAY_OF_YEAR)
}