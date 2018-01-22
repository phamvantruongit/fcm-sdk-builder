package jp.co.bravesoft.my_gcm_example2;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.support.v4.app.NotificationCompat;
import android.util.Log;

import biz.appvisor.push.android.sdk.IAppvisorPushBackgroundService;

/**
 * Created by bsfuji on 2017/05/12.
 */

public class BackgroundPushNotificationReceiveService extends Service
{
    private Context applicationContext  = null;
    private NotificationManager notificationManager = null;
    private Intent intent              = null;

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId)
    {
        this.applicationContext  = this.getApplicationContext();
        this.notificationManager = (NotificationManager)getSystemService(Context.NOTIFICATION_SERVICE);
        this.intent              = intent;

        Log.d("onStartCommand", "onStartCommand");
        if (null != intent)
        {
            Log.d("onStartCommand", notificationMessage());

            (new IAppvisorPushBackgroundService() {
                public void execute() {
                    AsyncNetworkTask task = new AsyncNetworkTask(getApplicationContext());
                    task.execute("http://dev-p.app-visor.com/");
                }
            }).execute();

        }
        return super.onStartCommand(intent, flags, startId);
    }

    private void fireNotification()
    {
        NotificationCompat.Builder builder = this.commonNotificationBuilder();
        builder.setContentIntent(this.contentIntent());

        this.notificationManager.notify(1, this.buildNotification(builder));
    }

    private NotificationCompat.Builder commonNotificationBuilder()
    {
        NotificationCompat.Builder builder = new NotificationCompat.Builder(getApplicationContext());
        builder.setContentTitle("Receive Background Notification");
        builder.setContentText(this.notificationMessage());
        builder.setSmallIcon(this.statusbarIconResourceId());
        builder.setLargeIcon(this.largeIconImage());
        builder.setStyle(this.notificationStyle());

        return builder;
    }

    private Intent appIntent()
    {
        Intent intent = new Intent(this.applicationContext, MainActivity.class);
        intent.putExtras(this.intent.getExtras());
        return intent;
    }

    private PendingIntent contentIntent()
    {
        return PendingIntent.getActivity(this.applicationContext, (int) System.currentTimeMillis(), this.appIntent(), PendingIntent.FLAG_UPDATE_CURRENT);
    }

    private Notification buildNotification(NotificationCompat.Builder builder)
    {
        Notification notification = builder.build();
        notification.defaults |= Notification.DEFAULT_SOUND;
        notification.flags = Notification.FLAG_AUTO_CANCEL;
        return notification;
    }

    private int statusbarIconResourceId()
    {
        return R.mipmap.ic_launcher;
    }

    private Bitmap largeIconImage()
    {
        return BitmapFactory.decodeResource(this.applicationContext.getResources(), R.mipmap.ic_launcher);
    }

    private NotificationCompat.Style notificationStyle()
    {
        return new NotificationCompat.BigTextStyle().bigText(this.notificationMessage());
    }

    private String notificationMessage()
    {
        Bundle bundle = this.intent.getExtras();

        StringBuilder stringBuilder = new StringBuilder();
        for (String key : bundle.keySet())
        {
            Object value = bundle.get(key);
            if (!(value instanceof String))
            {
                value = value.toString();
            }
            stringBuilder.append(String.format("%s: %s%s", key, value, System.getProperty("line.separator")));
        }

        return stringBuilder.toString();
    }
}
