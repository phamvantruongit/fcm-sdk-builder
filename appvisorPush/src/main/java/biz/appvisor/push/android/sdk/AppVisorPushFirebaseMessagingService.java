package biz.appvisor.push.android.sdk;

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import biz.appvisor.push.android.sdk.AppVisorPushIntentService;
import biz.appvisor.push.android.sdk.AppVisorPushSetting;

/**
 * Created by hirayamatakaaki on 2017/03/29.
 */

public class AppVisorPushFirebaseMessagingService extends FirebaseMessagingService
{

    private static final String TAG = "AppVisorFCMService";

    /**
     * Called when message is received.
     *
     * @param remoteMessage Object representing the message received from Firebase Cloud Messaging.
     */
    // [START receive_message]
    @Override
    public void onMessageReceived(RemoteMessage remoteMessage) {
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
        /*
        // Check if message contains a data payload.
        if (m.size() > 0) {
            Log.d(TAG, "Message data payload: " + m);
            // Check if data needs to be processed by long running job
            if ( true) {
                // For long-running tasks (10 seconds or more) use Firebase Job Dispatcher.
//                scheduleJob();
            } else {
                // Handle message within 10 seconds
                handleNow();
            }
        }
        for (Map.Entry<String, String> entry : m.entrySet()) {
            System.out.println("key ->" + entry.getKey() + ", value->" + entry.getValue());
        }
        */

        // Check if message contains a notification payload.
        if (remoteMessage.getNotification() != null) {
            Log.d(TAG, "Message Notification Body: " + remoteMessage.getNotification().getBody());
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

        String appvisor_sn = m.get(AppVisorPushSetting.KEY_SILENCE_NOTIFICATION);

        Context context = getApplicationContext();

        if ("1".equals(m.get(AppVisorPushSetting.KEY_BACKGROUND_NOTIFICATION))) {
            String serviceName = AppVisorPushUtil
                    .getPushCallbackServiceName(context);

            if (serviceName != null && !"".equals(serviceName)) {
                Class<?> callBackService = null;
                try {
                    callBackService = Class.forName(serviceName);
                } catch (ClassNotFoundException e) {
//					e.printStackTrace();
                }
                Intent intent = new Intent();
                Iterator i = m.keySet().iterator();
                while (i.hasNext()) {
                    String key = (String)i.next();
                    intent.putExtra(key, m.get(key));
                }
                intent.removeExtra(AppVisorPushSetting.KEY_APPVISOR_PUSH_INTENT);
                intent.putExtra(AppVisorPushSetting.KEY_APPVISOR_PUSH_INTENT,
                        true);
                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                intent.setClass(context, callBackService);
                startService(intent);
            }
        }


        if (urlFlag != null) {
            AppVisorPushIntentService.showUrlNotification(
                    title,
                    message,
                    context,
                    pushIdStr,
                    false,
                    (NotificationManager)getSystemService(Context.NOTIFICATION_SERVICE));
        } else {
            // Normal Mode
            String clsName = AppVisorPushUtil
                    .getPushCallbackClassName(context);
            Class<?> callBackClass = null;
            try {
                callBackClass = Class.forName(clsName);
            } catch (ClassNotFoundException e) {
//					e.printStackTrace();
            }

            AppVisorPushIntentService.showNotification(
                    title,
                    message,
                    context,
                    callBackClass,
                    pushIdStr,
                    hashMap,
                    false,
                    (NotificationManager)getSystemService(Context.NOTIFICATION_SERVICE));
        }
    }

    // [END receive_message]
    /**
     * Handle time allotted to BroadcastReceivers.
     */
    //private void handleNow() {
     //   Log.d(TAG, "Short lived task is done.");
    //}


}

