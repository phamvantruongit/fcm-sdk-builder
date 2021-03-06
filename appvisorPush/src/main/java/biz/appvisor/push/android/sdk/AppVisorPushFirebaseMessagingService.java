package biz.appvisor.push.android.sdk;

import android.annotation.TargetApi;
import android.app.NotificationManager;
import android.app.job.JobInfo;
import android.app.job.JobScheduler;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.os.PersistableBundle;

import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

/**
 * Created by hirayamatakaaki on 2017/03/29.
 */

public class AppVisorPushFirebaseMessagingService extends FirebaseMessagingService
{

    /**
     * Called when message is received.
     *
     * @param remoteMessage Object representing the message received from Firebase Cloud Messaging.
     */
    // [START receive_message]
    @Override
    public void onMessageReceived(RemoteMessage remoteMessage) {
        AppVisorPushUtil.appVisorPushLog("onMessageReceived");
        // [START_EXCLUDE]
        // There are two types of messages data messages and notification messages. Data messages are handled
        // here in onMessageReceived whether the app is in the foreground or background. Data messages are the type
        // traditionally used with GCM. Notification messages are only received here in onMessageReceived when the app
        // is in the foreground. When the app is in the background an automatically generated notification is displayed.
        // When the user taps on the notification they are returned to the app. Messages containing both notification
        // and data payloads are treated as notification messages. The Firebase console always sends notification
        // messages. For more see: https://firebase.google.com/docs/cloud-messaging/concept-options
        // [END_EXCLUDE]

        // TODO(developer): Handle FCM messages here.
        // Not getting messages here? See why this may be: https://goo.gl/39bRNJ
        //Log.d(TAG, "From: " + remoteMessage.getFrom());

        Map<String, String> m = remoteMessage.getData();
        String appvisorPushFlag = m.get(AppVisorPushSetting.KEY_APPVISOR_PUSH_INTENT);
        if (null == appvisorPushFlag || !appvisorPushFlag.equals("1")) {
            AppVisorPushUtil
                    .appVisorPushLog("ignore message which is not came from appvisor.");
            return;
        }

//        // Check if message contains a data payload.
//        if (m.size() > 0) {
//            Log.d(TAG, "Message data payload: " + m);
//            // Check if data needs to be processed by long running job
//            if ( true) {
//                // For long-running tasks (10 seconds or more) use Firebase Job Dispatcher.
////                scheduleJob();
//            } else {
//                // Handle message within 10 seconds
//                handleNow();
//            }
//        }
//        for (Map.Entry<String, String> entry : m.entrySet()) {
//            System.out.println("key ->" + entry.getKey() + ", value->" + entry.getValue());
//        }

        // Check if message contains a notification payload.
        if (remoteMessage.getNotification() != null) {
            AppVisorPushUtil.appVisorPushLog(
                    "Message Notification Body: " + remoteMessage.getNotification());
        }

        // Also if you intend on generating your own notifications as a result of a received FCM
        // message, here is where that should be initiated. See sendNotification method below.


        HashMap<String, String> hashMap = new HashMap<String, String>();

        String w = m.get(AppVisorPushSetting.KEY_PUSH_W);
        String x = m.get(AppVisorPushSetting.KEY_PUSH_X);
        String y = m.get(AppVisorPushSetting.KEY_PUSH_Y);
        String z = m.get(AppVisorPushSetting.KEY_PUSH_Z);
        hashMap.put(AppVisorPushSetting.KEY_PUSH_W, w);
        hashMap.put(AppVisorPushSetting.KEY_PUSH_X, x);
        hashMap.put(AppVisorPushSetting.KEY_PUSH_Y, y);
        hashMap.put(AppVisorPushSetting.KEY_PUSH_Z, z);

        String title = m.get(AppVisorPushSetting.KEY_PUSH_TITLE);
        String message = m.get(AppVisorPushSetting.KEY_PUSH_MESSAGE);
        String pushIdStr = m.get(AppVisorPushSetting.KEY_PUSH_TRACKING_ID);
        String urlFlag = m.get(AppVisorPushSetting.KEY_PUSH_URL);
        Context context = getApplicationContext();

        String contentFlag = m.get(AppVisorPushSetting.KEY_PUSH_CONTENT_FLAG);
        String contentUrl = m.get(AppVisorPushSetting.KEY_PUSH_CONTENT_URL);

        boolean vibrationOnOff = m.containsKey(AppVisorPushSetting.KEY_PUSH_VIBRATION) &&
            m.get(AppVisorPushSetting.KEY_PUSH_VIBRATION).equals("1");

        if ("1".equals(m.get(AppVisorPushSetting.KEY_BACKGROUND_NOTIFICATION))) {
            if (AppVisorPushSetting.thisApiLevel < 26) {
                startBackgroundService(context, m);
            } else {
                startJobService(context, m);
            }
        }

        if ("1".equals(m.get(AppVisorPushSetting.KEY_SILENCE_NOTIFICATION))) {
            return;
        }

        String clsName = AppVisorPushUtil
                .getPushCallbackClassName(context);

        if (AppVisorPushSetting.thisApiLevel >= 16 && contentFlag != null) {
            // OS Version after Android 4.1 && Rich Push

            if (AppVisorPushSetting.thisApiLevel < 26) {
                AppvisorPushNotification.showRichNotification(title, message, context,
                        clsName, pushIdStr, hashMap, vibrationOnOff, contentFlag, contentUrl, urlFlag, this);

            }
            else  {
                AppvisorPushNotification.showRichNotificationWithJobService(title, message, context,
                        clsName, pushIdStr, hashMap, vibrationOnOff, contentFlag, contentUrl, urlFlag, this);
            }
            return;
        }

        if (urlFlag != null) {
            AppvisorPushNotification.showUrlNotification(
                    title,
                    message,
                    context,
                    pushIdStr,
                    vibrationOnOff,
                    (NotificationManager)getSystemService(Context.NOTIFICATION_SERVICE));
        } else {
            // Normal Mode
            Class<?> callBackClass = null;
            try {
                callBackClass = Class.forName(clsName);
            } catch (ClassNotFoundException e) {
//					e.printStackTrace();
            }

            AppvisorPushNotification.showNotification(
                    title,
                    message,
                    context,
                    callBackClass,
                    pushIdStr,
                    hashMap,
                    vibrationOnOff,
                    (NotificationManager)getSystemService(Context.NOTIFICATION_SERVICE));
        }
    }

    private void startBackgroundService(Context context, Map<String, String> m) {
        Class<?> callBackService = AppVisorPushUtil.getPushCallbackServiceClass(context);
        if (callBackService == null) {
            AppVisorPushUtil.appVisorPushLog("callBackService is empty to launch background service.");
            return;
        }

        Intent intent = new Intent();
        Iterator i = m.keySet().iterator();
        while (i.hasNext()) {
            String key = (String) i.next();
            String value = m.get(key);
            intent.putExtra(key, value);
        }
        intent.putExtra(AppVisorPushSetting.KEY_APPVISOR_PUSH_INTENT, true);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        intent.setClass(context, callBackService);

        startService(intent);
    }

    @TargetApi(26)
    private void startJobService(Context context, Map<String, String> m) {
        Class<?> callBackService = AppVisorPushUtil.getPushCallbackJobServiceClass(context);
        if (callBackService == null) {
            AppVisorPushUtil.appVisorPushLog("callBackService is empty to launch job service.");
            return;
        }

        PersistableBundle bundle = new PersistableBundle();
        Iterator i = m.keySet().iterator();
        while (i.hasNext()) {
            String key = (String) i.next();
            bundle.putString(key, m.get(key));
        }

        ComponentName mServiceName = new ComponentName(this, callBackService);
        JobScheduler scheduler = (JobScheduler) getSystemService(Context.JOB_SCHEDULER_SERVICE);
        JobInfo jobInfo = new JobInfo.Builder(0, mServiceName)
                .setMinimumLatency(3000)
                .setOverrideDeadline(10000)
                .setRequiredNetworkType(JobInfo.NETWORK_TYPE_ANY)
                .setExtras(bundle)
                .build();

        scheduler.schedule(jobInfo);
    }

}

