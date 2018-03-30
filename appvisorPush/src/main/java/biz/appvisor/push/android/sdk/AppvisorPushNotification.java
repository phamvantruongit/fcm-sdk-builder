package biz.appvisor.push.android.sdk;

import android.annotation.TargetApi;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.support.v4.app.NotificationCompat;
import android.util.Log;

import java.util.HashMap;

public class AppvisorPushNotification {
    private static final String TAG = "AppvisorPushNotif";
    protected static void showNotification(String title, String message,
                                           Context context, Class<?> cls, String pushIDStr,
                                           HashMap<String, String> hashMap, boolean vibrationOnOff, NotificationManager notiManager) {
        Log.d(TAG, "onMessageReceived_330");
        AppVisorPushUtil.appVisorPushLog("show Normal Notification start");

        int pushIconResourceId = AppVisorPushUtil.getPushIconID(context);
        int statusbarIconResourceId = AppVisorPushUtil
                .getStatusBarIconID(context);
        String appName = AppVisorPushUtil.getPushAppName(context);

        Intent intent;

        if (cls != null) {
            intent = new Intent(context, cls);
        } else {
            intent = new Intent();
        }

        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        intent.putExtra(AppVisorPushSetting.KEY_APPVISOR_PUSH_INTENT, true);
        intent.putExtra(AppVisorPushSetting.KEY_PUSH_MESSAGE, message);
        intent.putExtra(AppVisorPushSetting.KEY_PUSH_TRACKING_ID, pushIDStr);
        intent.putExtra(AppVisorPushSetting.KEY_PUSH_X,
                hashMap.get(AppVisorPushSetting.KEY_PUSH_X));
        intent.putExtra(AppVisorPushSetting.KEY_PUSH_Y,
                hashMap.get(AppVisorPushSetting.KEY_PUSH_Y));
        intent.putExtra(AppVisorPushSetting.KEY_PUSH_Z,
                hashMap.get(AppVisorPushSetting.KEY_PUSH_Z));
        intent.putExtra(AppVisorPushSetting.KEY_PUSH_W,
                hashMap.get(AppVisorPushSetting.KEY_PUSH_W));

        if (title != null && title.length() > 0) {
            intent.putExtra(AppVisorPushSetting.KEY_PUSH_TITLE, title);
        } else {
            intent.putExtra(AppVisorPushSetting.KEY_PUSH_TITLE, appName);
        }

        PendingIntent contentIntent = PendingIntent.getActivity(context,
                (int) System.currentTimeMillis(), intent,
                PendingIntent.FLAG_UPDATE_CURRENT);

        String notifTitle = "";
        if (title != null && title.length() > 0) {
            notifTitle = title;
        } else {
            notifTitle = appName;
        }

        Resources resources = context.getResources();
        Bitmap largeIconImage = BitmapFactory.decodeResource(resources,
                pushIconResourceId);

        Notification notif = new Notification();
    Log.d(TAG, "onMessageReceived_331");

        if (AppVisorPushSetting.thisApiLevel < 16) {
            // OS Version in Android 3.0 ‾ 4.1
            NotificationCompat.Builder notifBuilder = new NotificationCompat.Builder(context);

            notifBuilder.setContentTitle(notifTitle);
            notifBuilder.setContentText(message);
            notifBuilder.setSmallIcon(statusbarIconResourceId);
            notifBuilder.setLargeIcon(largeIconImage);
            notifBuilder.setContentIntent(contentIntent);

            notif = notifBuilder.build();
        } else if (AppVisorPushSetting.thisApiLevel < 26) {
            // OS Version after Android 4.1
            NotificationCompat.Builder notifBuilder = new NotificationCompat.Builder(context);

            notifBuilder.setContentTitle(notifTitle);
            notifBuilder.setContentText(message);
            notifBuilder.setSmallIcon(statusbarIconResourceId);
            notifBuilder.setLargeIcon(largeIconImage);
            notifBuilder.setContentIntent(contentIntent);
            notifBuilder.setStyle(new NotificationCompat.BigTextStyle().bigText(message));

            notif = notifBuilder.build();

        } else {
            // OS Version after Android 8.0
            notif = notifyWithChannelId(
                    context,
                    message,
                    notifTitle,
                    statusbarIconResourceId,
                    largeIconImage,
                    contentIntent
            );
        }

        notif.flags = Notification.FLAG_AUTO_CANCEL;

        Log.d(TAG, "onMessageReceived_332");
        int pushID = 0;
        try {
            pushID = Integer.parseInt(pushIDStr);
        } catch (NumberFormatException e) {
            AppVisorPushUtil.appVisorPushWaring("NumberFormatException", e);
            pushID = 0;
        }
        Log.d(TAG, "onMessageReceived_333: " + new Integer(pushID).toString());
        notiManager.notify(pushID, notif);
    /*

        AppVisorPushUtil.appVisorPushLog("show Normal Notification Finished");
        */
    }

    protected static void showUrlNotification(String title, String message,
                                              Context context, String pushIDStr, boolean vibrationOnOff, NotificationManager notiManager) {
        AppVisorPushUtil.appVisorPushLog("show Url Notification start");

        int pushIconResourceId = AppVisorPushUtil.getPushIconID(context);
        int statusbarIconResourceId = AppVisorPushUtil
                .getStatusBarIconID(context);
        String appName = AppVisorPushUtil.getPushAppName(context);
        String appTrackingID = AppVisorPushUtil.getAppTrackingKey(context);
        String deviceUUID = AppVisorPushUtil.getDeviceUUID(context,
                appTrackingID);
        if (null == pushIDStr || pushIDStr.equals("")) {
            AppVisorPushUtil
                    .appVisorPushLog("pushIDStr is empty,can't make url,failed.");
            return;
        }

        long arrivedTimeStamp = System.currentTimeMillis();

        String urlByString = String.format(
                "%s?%s=user&%s=callback&%s=%s&%s=%s&%s=%s&%s=%d",
                AppVisorPushSetting.PUSH_ARRIVED_URL,
                AppVisorPushSetting.PARAM_C, AppVisorPushSetting.PARAM_A,
                AppVisorPushSetting.PARAM_APP_TRACKING_KEY, appTrackingID,
                AppVisorPushSetting.PARAM_DEVICE_UUID, deviceUUID,
                AppVisorPushSetting.PARAM_PUSH_TRACKING_ID, pushIDStr,
                AppVisorPushSetting.PARAM_ARRIVED_TIME, arrivedTimeStamp);
        AppVisorPushUtil.appVisorPushLog("url :" + urlByString);

        Intent notificationIntent = new Intent(context, NotificationStartService.class);
        notificationIntent.putExtra("url", urlByString);
        notificationIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);

        if (title != null && title.length() > 0) {
            notificationIntent.putExtra(AppVisorPushSetting.KEY_PUSH_TITLE,
                    title);
        } else {
            notificationIntent.putExtra(AppVisorPushSetting.KEY_PUSH_TITLE,
                    appName);
        }

        PendingIntent contentIntent = PendingIntent.getService(context,
                (int) System.currentTimeMillis(), notificationIntent,
                PendingIntent.FLAG_UPDATE_CURRENT);

        String notifTitle = "";
        if (title != null && title.length() > 0) {
            notifTitle = title;
        } else {
            notifTitle = appName;
        }

        Resources resources = context.getResources();
        Bitmap largeIconImage = BitmapFactory.decodeResource(resources,
                pushIconResourceId);

        Notification notif = new Notification();

        if (AppVisorPushSetting.thisApiLevel < 16) {
            // OS Version in Android 3.0 ‾ 4.1
            NotificationCompat.Builder notifBuilder = new NotificationCompat.Builder(context);

            notifBuilder.setContentTitle(notifTitle);
            notifBuilder.setContentText(message);
            notifBuilder.setSmallIcon(statusbarIconResourceId);
            notifBuilder.setLargeIcon(largeIconImage);
            notifBuilder.setContentIntent(contentIntent);

            notif = notifBuilder.build();
        } else if (AppVisorPushSetting.thisApiLevel < 26) {
            // OS Version after Android 4.1
            NotificationCompat.Builder notifBuilder = new NotificationCompat.Builder(context);

            notifBuilder.setContentTitle(notifTitle);
            notifBuilder.setContentText(message);
            notifBuilder.setSmallIcon(statusbarIconResourceId);
            notifBuilder.setLargeIcon(largeIconImage);
            notifBuilder.setContentIntent(contentIntent);
            notifBuilder.setStyle(new NotificationCompat.BigTextStyle().bigText(message));

            notif = notifBuilder.build();

        } else {
            // OS Version after Android 8.0
//			notif = this.notifyWithChannelId(context, message, notifTitle, statusbarIconResourceId, contentIntent);
            notif = notifyWithChannelId(
                    context,
                    message,
                    notifTitle,
                    statusbarIconResourceId,
                    largeIconImage,
                    contentIntent
            );
        }

        notif.flags = Notification.FLAG_AUTO_CANCEL;

        int pushID = 0;
        try {
            pushID = Integer.parseInt(pushIDStr);
        } catch (NumberFormatException e) {
            AppVisorPushUtil.appVisorPushWaring("NumberFormatException", e);
            pushID = 0;
        }
        //notiManager.notify(pushID, notif);

        AppVisorPushUtil.appVisorPushLog("show Url Notification end");

        Log.d(TAG, "done");
    }

    @TargetApi(26)
    protected static Notification notifyWithChannelId(Context context, String message,
                                                      String notifTitle, int statusbarIconResourceId,
                                                      Bitmap largeIconImage, PendingIntent contentIntent) {

        AppVisorPushUtil.appVisorPushLog("Push notification to default channel id");

        return new Notification.Builder(context, AppVisorPushSetting.DEFAULT_NOTIFICATION_CHANNEL_ID)
                .setContentTitle(notifTitle)
                .setContentText(message)
                .setSmallIcon(statusbarIconResourceId)
                .setLargeIcon(largeIconImage)
                .setContentIntent(contentIntent)
                .build();
    }
}
