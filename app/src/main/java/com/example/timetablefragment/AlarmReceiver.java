package com.example.timetablefragment;

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.widget.Toast;

import androidx.core.app.NotificationCompat;

public class AlarmReceiver extends BroadcastReceiver {
    private NotificationManager mNotificationManager;
    private static final int NOTIFICATION_ID = 0;
    private static final String PRIMARY_CHANNEL_ID = "primary_notification_channel";

    @Override
    public void onReceive(Context context, Intent intent) {
        mNotificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        deliverNotification(context, intent);
    }
    private void deliverNotification(Context context, Intent intent) {
        Intent contentIntent = new Intent(context, EditEvent.class);
        PendingIntent contentPendingIntent = PendingIntent.getActivity(context, NOTIFICATION_ID, contentIntent, PendingIntent.FLAG_UPDATE_CURRENT);
        // Build the notification
        Bundle bundle = new Bundle();
        String title = intent.getExtras().getString("title");
        String eventDescription = intent.getExtras().getString("eventDescription");
        String organizers = intent.getExtras().getString("organizers");
        String venue = intent.getExtras().getString("venue");
        int startHour = intent.getExtras().getInt("startHour");
        int startMin = intent.getExtras().getInt("startMin");
        String notificationContent = String.format("Start Time: %01d:%02d \n Venue: %3s \n Organizers: %4s \n Event Description: %5s",
                startHour, startMin, venue, organizers, eventDescription);
        NotificationCompat.Builder builder = new NotificationCompat.Builder
                (context, PRIMARY_CHANNEL_ID)
                .setSmallIcon(R.drawable.ic_person)
                .setContentTitle(title)
                .setContentText(notificationContent)
                .setContentIntent(contentPendingIntent)
                .setPriority(NotificationCompat.PRIORITY_HIGH)
                .setAutoCancel(true)
                .setDefaults(NotificationCompat.DEFAULT_ALL);

        // Deliver the notification
        mNotificationManager.notify(NOTIFICATION_ID, builder.build());
        //Toast.makeText(EditEvent.retrieveContext(), "xxxxxx", Toast.LENGTH_SHORT).show();
    }
}
