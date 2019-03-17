package com.example.nissan.reimbursement;

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.support.v4.app.NotificationCompat;
import android.util.Log;

import java.util.Random;

public class MyNotificationManager {
    private Context mCtx;
    private static  MyNotificationManager mInstance;

    private MyNotificationManager(Context context){
        mCtx=context;
    }

    public static  synchronized MyNotificationManager getmInstance(Context context){
        if(mInstance==null){
            mInstance=new MyNotificationManager(context);
        }
        return mInstance;
    }

    public void displayNotification(String title,String body)
    {
        NotificationCompat.Builder notificationBuilder = new NotificationCompat.Builder(mCtx,Constants.CHANNEL_ID);
        notificationBuilder.setContentTitle(title);
        notificationBuilder.setContentText(body);
        notificationBuilder.setSmallIcon(R.drawable.icon);

        Intent intent=new Intent(mCtx,DashboardActivity.class);
        PendingIntent pendingIntent=PendingIntent.getActivity(mCtx,0,intent,PendingIntent.FLAG_UPDATE_CURRENT);

        notificationBuilder.setContentIntent(pendingIntent);

        NotificationManager notificationManager =(NotificationManager)mCtx.getSystemService(Context.NOTIFICATION_SERVICE);
        if(notificationManager != null)
        {


            notificationManager.notify((int)System.currentTimeMillis(),notificationBuilder.build());
        }



    }
}
