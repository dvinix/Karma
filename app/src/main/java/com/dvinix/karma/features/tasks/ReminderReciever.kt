package com.dvinix.karma.features.tasks



import android.app.NotificationManager
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.widget.RemoteViews
import androidx.core.app.NotificationCompat
import com.dvinix.karma.R

class ReminderReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context, intent: Intent) {
        val taskTitle = intent.getStringExtra("TASK_TITLE") ?: "What's matter right now?"

        val notificationLayout = RemoteViews(context.packageName, R.layout.custom_notification)
        notificationLayout.setTextViewText(R.id.notification_content, taskTitle)

        val notificationManager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

        val notification = NotificationCompat.Builder(context, "karma_channel")
            .setSmallIcon(R.drawable.reminder) // Ensure this icon exists in res/drawable
            .setContentTitle("KARMA")
            .setContentText(taskTitle)
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setAutoCancel(true)
            .build()


        notificationManager.notify(taskTitle.hashCode(), notification)
    }
}