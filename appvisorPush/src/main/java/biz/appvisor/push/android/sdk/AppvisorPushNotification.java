package biz.appvisor.push.android.sdk;

import android.annotation.TargetApi;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.ContextWrapper;
import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.support.v4.app.NotificationCompat;

import java.util.HashMap;

public class AppvisorPushNotification {
    protected static void showNotification(String title, String message,
                                           Context context, Class<?> cls, String pushIDStr,
                                           HashMap<String, String> hashMap, boolean vibrationOnOff, NotificationManager notiManager) {
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

        if (AppVisorPushSetting.thisApiLevel < 26) {
            notif.defaults |= Notification.DEFAULT_SOUND;
            if (checkIsVibrateEnable(context) && vibrationOnOff) {
                notif.defaults |= Notification.DEFAULT_VIBRATE;
            }
        }

        notif.flags = Notification.FLAG_AUTO_CANCEL;

        int pushID = 0;
        try {
            pushID = Integer.parseInt(pushIDStr);
        } catch (NumberFormatException e) {
            AppVisorPushUtil.appVisorPushWaring("NumberFormatException", e);
            pushID = 0;
        }
        notiManager.notify(pushID, notif);

        AppVisorPushUtil.appVisorPushLog("show Normal Notification Finished");
    }

    private static boolean checkIsVibrateEnable(Context context) {
        boolean result = false;
        ApplicationInfo appInfo = context.getApplicationInfo();
        String appPackageName = appInfo.packageName;
        String permissonName = "android.permission.VIBRATE";

        AppVisorPushUtil
                .appVisorPushLog("check vibrate permission for packageName: "
                        + appPackageName);

        int permissonValue = context.getPackageManager().checkPermission(
                permissonName, appPackageName);

        if (permissonValue == PackageManager.PERMISSION_GRANTED) {
            result = true;

            AppVisorPushUtil
                    .appVisorPushLog("vibrate PERMISSION_GRANTED for packageName: "
                            + appPackageName);

        } else {
            result = false;

            AppVisorPushUtil
                    .appVisorPushLog("vibrate PERMISSION_DENIED for packageName: "
                            + appPackageName);
        }

        return result;
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

        if (AppVisorPushSetting.thisApiLevel < 26) {
            notif.defaults |= Notification.DEFAULT_SOUND;
            if (checkIsVibrateEnable(context) && vibrationOnOff) {
                notif.defaults |= Notification.DEFAULT_VIBRATE;
            }
        }

        notif.flags = Notification.FLAG_AUTO_CANCEL;

        int pushID = 0;
        try {
            pushID = Integer.parseInt(pushIDStr);
        } catch (NumberFormatException e) {
            AppVisorPushUtil.appVisorPushWaring("NumberFormatException", e);
            pushID = 0;
        }
        notiManager.notify(pushID, notif);

        AppVisorPushUtil.appVisorPushLog("show Url Notification end");
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
                .setStyle(new Notification.BigTextStyle().bigText(message))
                .build();
    }

    protected static void showRichNotification(String title, String message,
                                        final Context context, String className, String pushIDStr,
                                        HashMap<String, String> hashMap, boolean vibrationOnOff,
                                        String contentFlg, String contentURL, String urlFlag, ContextWrapper contextWrapper) {

        AppVisorPushUtil.appVisorPushLog("show Rich Notification start");

        final RichPush richPush = new RichPush(title, message, className, pushIDStr,
                hashMap, vibrationOnOff, contentFlg, contentURL, urlFlag);

        Intent intent = new Intent(context, RichPushIntentService.class);
        intent.putExtra("richPush", richPush);
        contextWrapper.startService(intent);
    }

}
