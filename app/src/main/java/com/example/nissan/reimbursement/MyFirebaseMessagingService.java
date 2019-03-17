package com.example.nissan.reimbursement;


import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;

import java.util.Map;

public class MyFirebaseMessagingService extends FirebaseMessagingService {

    @Override
    public void onMessageReceived(RemoteMessage remoteMessage) {
        Map<String, String> data = remoteMessage.getData();
        String title1 = data.get("title");
        String body1 = data.get("body");

//
//        String title=remoteMessage.getNotification().getTitle();
//        String body=remoteMessage.getNotification().getBody();

        MyNotificationManager.getmInstance(getApplicationContext()).displayNotification(title1,body1);

    }
}
