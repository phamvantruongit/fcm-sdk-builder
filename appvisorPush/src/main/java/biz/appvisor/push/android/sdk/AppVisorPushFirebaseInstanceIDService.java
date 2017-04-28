package biz.appvisor.push.android.sdk;

import android.content.Context;
import android.util.Log;

import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.iid.FirebaseInstanceIdService;

import biz.appvisor.push.android.sdk.AppVisorPush;
import biz.appvisor.push.android.sdk.AppVisorPushUtil;

/**
 * Created by hirayamatakaaki on 2017/03/29.
 */

class AppVisorPushFirebaseInstanceIDService extends FirebaseInstanceIdService
{
    private static final String TAG = "AppVisorFCMIIDService";

    /**
     * Called if InstanceID token is updated. This may occur if the security of
     * the previous token had been compromised. Note that this is called when the InstanceID token
     * is initially generated so this is where you would retrieve the token.
     */
    // [START refresh_token]
    @Override
    public void onTokenRefresh() {
        // Get updated InstanceID token.
//        String refreshedToken = FirebaseInstanceId.getInstance().getToken();
//        Log.d(TAG, "Refreshed token: " + refreshedToken);

        AppVisorPush appVisorPush = AppVisorPush.sharedInstance();
        if (!appVisorPush.launchWithTokenIfYet(true)) {
            appVisorPush.refreshTokenIfUpdated();
        }

        // If you want to send messages to this application instance or
        // manage this apps subscriptions on the server side, send the
        // Instance ID token to your app server.
        //sendRegistrationToServer(refreshedToken);
    }
    // [END refresh_token]

}

