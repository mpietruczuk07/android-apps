package pl.edu.pb.shoppingapp.webService;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.media.RingtoneManager;
import android.net.Uri;

import androidx.annotation.NonNull;
import androidx.core.app.NotificationCompat;

import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;

import pl.edu.pb.shoppingapp.activity.MainActivity;
import pl.edu.pb.shoppingapp.entity.Notification;
import pl.edu.pb.shoppingapp.model.NotificationViewModel;
import pl.edu.pb.shoppingapp.R;

public class NotificationService extends FirebaseMessagingService {
    NotificationViewModel model;

    private static final int REQUEST_CODE = 100;
    private static final String NOTIFICATION_CHANNEL_NAME = "General";

    @Override
    public void onMessageReceived(@NonNull RemoteMessage message) {
        super.onMessageReceived(message);

        boolean areNotificationsEnabled = getSharedPreferences("user_notifications", Context.MODE_PRIVATE)
                .getBoolean("notifications", true);

        if(areNotificationsEnabled){
            showNotification(message.getNotification().getTitle(), message.getNotification().getBody());
        }

        addNotification(message.getNotification().getTitle(), message.getNotification().getBody());
    }

    private void showNotification(String messageTitle, String messageBody){
        Intent intent = new Intent(this, MainActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        PendingIntent pendingIntent = PendingIntent.getActivity(this, REQUEST_CODE, intent,
                PendingIntent.FLAG_IMMUTABLE);

        String channelId = "GENERAL";
        Uri defaultSoundUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);

        NotificationCompat.Builder notificationBuilder =
                new NotificationCompat.Builder(this, channelId)
                        .setSmallIcon(R.drawable.ic_baseline_shopping_bag_24)
                        .setContentTitle(messageTitle)
                        .setContentText(messageBody)
                        .setAutoCancel(true)
                        .setSound(defaultSoundUri)
                        .setContentIntent(pendingIntent);

        NotificationManager notificationManager =
                (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);

        NotificationChannel channel = new NotificationChannel(channelId,
                NOTIFICATION_CHANNEL_NAME,
                NotificationManager.IMPORTANCE_DEFAULT);
        notificationManager.createNotificationChannel(channel);

        notificationManager.notify(0, notificationBuilder.build());
    }

    private void addNotification(String messageTitle, String messageBody){
        model = MainActivity.model;
        Notification notification = new Notification(messageTitle, messageBody);
        model.insert(notification);
    }
}

