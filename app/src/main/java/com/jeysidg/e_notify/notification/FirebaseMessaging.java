package com.jeysidg.e_notify.notification;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.core.app.NotificationCompat;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;
import com.jeysidg.e_notify.VisitorformActivity;

public class FirebaseMessaging extends FirebaseMessagingService {

    @Override
    public void onMessageReceived(@NonNull RemoteMessage message) {
        super.onMessageReceived(message);

        //get current user from sp
        SharedPreferences sp = getSharedPreferences("SP_USER", MODE_PRIVATE);
        String saveCurrentUser = sp.getString("Current_USERID", "None");

        String sent = message.getData().get("sent");
        String user = message.getData().get("user");
        FirebaseUser fUser = FirebaseAuth.getInstance().getCurrentUser();
        if (fUser != null && sent.equals(fUser.getUid())){
            if (!saveCurrentUser.equals(user)){
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O){
                    SendAndAboveNotification(message);
                } else {
                    SendNormalNotification(message);
                }


            }
        }

    }

    private void SendNormalNotification(RemoteMessage message) {

        String user = message.getData().get("user");
        String icon = message.getData().get("icon");
        String title = message.getData().get("title");
        String body = message.getData().get("body");

        RemoteMessage.Notification notification = message.getNotification();
        int i = Integer.parseInt(user.replaceAll("[\\D]", ""));
        Intent intent = new Intent(this, VisitorformActivity.class);
        Bundle bundle = new Bundle();
        bundle.putString("hisUid", user);
        intent.putExtras(bundle);
        intent.addFlags(intent.FLAG_ACTIVITY_CLEAR_TOP);
        PendingIntent pIntent = PendingIntent.getActivity(this, i, intent, PendingIntent.FLAG_ONE_SHOT | PendingIntent.FLAG_IMMUTABLE);


        Uri defSoundUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
        NotificationCompat.Builder builder = new NotificationCompat.Builder(this)
                .setSmallIcon(Integer.parseInt(icon))
                .setContentText(body)
                .setContentTitle(title)
                .setAutoCancel(true)
                .setSound(defSoundUri)
                .setContentIntent(pIntent);

        NotificationManager notificationManager = (NotificationManager)getSystemService(Context.NOTIFICATION_SERVICE);
        int j = 0;
        if (i>0){
            j=i;
        }
        notificationManager.notify(j, builder.build());

    }
    private void SendAndAboveNotification(RemoteMessage message) {

        String user = message.getData().get("user");
        String icon = message.getData().get("icon");
        String title = message.getData().get("title");
        String body = message.getData().get("body");

        RemoteMessage.Notification notification = message.getNotification();
        int i = Integer.parseInt(user.replaceAll("[\\D]", ""));
        Intent intent = new Intent(this, VisitorformActivity.class);
        Bundle bundle = new Bundle();
        bundle.putString("hisUid", user);
        intent.putExtras(bundle);
        intent.addFlags(intent.FLAG_ACTIVITY_CLEAR_TOP);
        PendingIntent pIntent = PendingIntent.getActivity(this, i, intent, PendingIntent.FLAG_ONE_SHOT | PendingIntent.FLAG_IMMUTABLE);


        Uri defSoundUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);

        OreoUpNotification notification1 = new OreoUpNotification(this);
        Notification.Builder builder = notification1.getONotifications(title, body, pIntent, defSoundUri, icon);
        int j = 0;
        if (i>0){
            j=i;
        }
        notification1.getManager().notify(j, builder.build());

    }
}
