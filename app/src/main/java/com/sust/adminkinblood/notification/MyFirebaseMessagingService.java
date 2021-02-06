package com.sust.adminkinblood.notification;

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Intent;
import android.widget.Toast;


import androidx.annotation.NonNull;
import androidx.core.app.NotificationCompat;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.messaging.FirebaseMessaging;
import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;
import com.sust.adminkinblood.R;
import com.sust.adminkinblood.Requests;

import java.util.Random;

public class MyFirebaseMessagingService extends FirebaseMessagingService {


    @Override
    public void onNewToken(@NonNull String s) {
        super.onNewToken(s);
        FirebaseMessaging.getInstance().getToken()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()){
                        String refreshToken = task.getResult();
                        updateToken(refreshToken);
                    } else {
                        Toast.makeText(this, "FCM token receive failed", Toast.LENGTH_SHORT).show();
                    }
                });
    }


    @Override
    public void onMessageReceived(@NonNull RemoteMessage remoteMessage) {
        super.onMessageReceived(remoteMessage);

        Random random = new Random();

        String bloodGroup = remoteMessage.getData().get("bloodGroup");
        String hospital = remoteMessage.getData().get("donorHaveToGoLocationName");
        String newOrCancel = remoteMessage.getData().get("newOrCancel");

        Intent intent = new Intent(this, Requests.class);
        PendingIntent pendingIntent = PendingIntent.getActivity(this, 6969, intent, PendingIntent.FLAG_UPDATE_CURRENT);

        assert newOrCancel != null;
        switch (newOrCancel) {
            case "newRequest": {
                NotificationCompat.Builder builder = new NotificationCompat.Builder(getApplicationContext(), App.EVENT_ID)
                        .setSmallIcon(R.drawable.ic_app_logo_24dp)
                        .setContentTitle("New blood request arrived!")
                        .setContentText("Blood Group: " + bloodGroup + " Hospital: " + hospital)
                        .setPriority(NotificationCompat.PRIORITY_MAX)
                        .setContentIntent(pendingIntent)
                        .setAutoCancel(true);

                NotificationManager manager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
                assert manager != null;
                manager.notify(random.nextInt(100000), builder.build());
                break;
            }
            case "cancelRequest": {
                NotificationCompat.Builder builder = new NotificationCompat.Builder(getApplicationContext(), App.EVENT_ID)
                        .setSmallIcon(R.drawable.ic_app_logo_24dp)
                        .setContentTitle("A request has been canceled!")
                        .setContentText("Blood Group: " + bloodGroup + " Hospital: " + hospital)
                        .setPriority(NotificationCompat.PRIORITY_MAX)
                        .setContentIntent(pendingIntent)
                        .setAutoCancel(true);

                NotificationManager manager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
                assert manager != null;
                manager.notify(random.nextInt(100000), builder.build());
                break;
            }
            case "cancelRequestRequest": {
                NotificationCompat.Builder builder = new NotificationCompat.Builder(getApplicationContext(), App.EVENT_ID)
                        .setSmallIcon(R.drawable.ic_app_logo_24dp)
                        .setContentTitle("Request canceled! Notify donor!")
                        .setContentText("Blood Group: " + bloodGroup + " Hospital: " + hospital)
                        .setPriority(NotificationCompat.PRIORITY_MAX)
                        .setContentIntent(pendingIntent)
                        .setAutoCancel(true);

                NotificationManager manager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
                assert manager != null;
                manager.notify(random.nextInt(100000), builder.build());
                break;
            }
        }
    }

    private void updateToken(String refreshToken) {
        FirebaseUser firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        Token token = new Token(refreshToken);
        assert firebaseUser != null;
        FirebaseDatabase.getInstance().getReference().child("Tokens").child("Admins").child(firebaseUser.getUid()).setValue(token);
    }

}
