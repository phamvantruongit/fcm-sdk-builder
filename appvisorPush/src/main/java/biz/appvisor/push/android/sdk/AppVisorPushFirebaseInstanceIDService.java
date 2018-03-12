package biz.appvisor.push.android.sdk;

import android.content.Context;
import android.util.Log;

import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.iid.FirebaseInstanceIdService;

import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.StatusLine;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.List;

import biz.appvisor.push.android.sdk.AppVisorPush;
import biz.appvisor.push.android.sdk.AppVisorPushUtil;

/**
 * Created by hirayamatakaaki on 2017/03/29.
 */

public class AppVisorPushFirebaseInstanceIDService extends FirebaseInstanceIdService
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

        /*
        AppVisorPush appVisorPush = AppVisorPush.sharedInstance();
        if (!appVisorPush.launchWithTokenIfYet(true)) {
            appVisorPush.refreshTokenIfUpdated();
        }
        */
        Context context = getApplicationContext();

        final String appTrackingKey = AppVisorPushUtil.getAppTrackingKey(context);
        if (appTrackingKey == null) {
            return;
        }

        final String deviceToken = FirebaseInstanceId.getInstance().getToken();
        if( AppVisorPushUtil.getAppStatus( context ) == AppVisorPushSetting.APP_STATUS_KEY_KL)
        {
            AppVisorPushUtil.appVisorPushLog("AppStatus is KL, can't send appInfor to server.");
        }

//        sendToServer(true);
        AppVisorPushUtil.appVisorPushLog( "already had device token:" + deviceToken );
        AppVisorPushUtil.savePushToken(context, deviceToken);
        //send user info only when token EXISTS
        //AppVisorPush.startSendDeviceInfor(context, isInService);
        refreshPushToken(context, appTrackingKey);

        // If you want to send messages to this application instance or
        // manage this apps subscriptions on the server side, send the
        // Instance ID token to your app server.
        //sendRegistrationToServer(refreshedToken);
    }
    // [END refresh_token]

    private static boolean refreshPushToken(Context context, String appTrackingKey) {
        final DefaultHttpClient client = new DefaultHttpClient();
        final HttpPost method = new HttpPost(AppVisorPushSetting.PUSH_PROPERTY_URL);
        try
        {
            String deviceUUID = AppVisorPushUtil.getDeviceUUID( context , appTrackingKey );
            List<NameValuePair> postParams = new ArrayList<NameValuePair>();

            postParams.add(new BasicNameValuePair( AppVisorPushSetting.PARAM_C , "user" ) );
            postParams.add(new BasicNameValuePair( AppVisorPushSetting.PARAM_A , "refreshToken" ) );
            postParams.add(new BasicNameValuePair( AppVisorPushSetting.PARAM_APP_TRACKING_KEY , appTrackingKey ) );
            postParams.add(new BasicNameValuePair( AppVisorPushSetting.PARAM_DEVICE_UUID, deviceUUID ) );
            postParams.add(new BasicNameValuePair( AppVisorPushSetting.PARAM_DEVICE_TOKEN, AppVisorPushUtil.getPushToken( context ) ) );

            method.setEntity(new UrlEncodedFormEntity(postParams, "UTF-8"));
            final HttpResponse response = client.execute(method);

            final StatusLine statusLine = response.getStatusLine();
            final int statusCode = statusLine.getStatusCode();

            AppVisorPushUtil.appVisorPushLog( String.format("communication completely with status code: %d", Integer.valueOf(statusCode)) );

            if (statusCode >= 500 && statusCode <= 599)
            {
                AppVisorPushUtil.appVisorPushLog( "synchronize user properties failed. Error code:" + statusCode );
                return false;
            }
            else
            {
                AppVisorPushUtil.appVisorPushLog( "synchronize user properties succeed.");
                return true;
            }
        }
        catch (final UnsupportedEncodingException e)
        {
            AppVisorPushUtil.appVisorPushWaring( "UnsupportedEncodingException" , e );
            return false;
        }
        catch (final ClientProtocolException e)
        {
            AppVisorPushUtil.appVisorPushWaring( "ClientProtocolException" , e );
            return false;
        }
        catch (final IOException e)
        {
            AppVisorPushUtil.appVisorPushWaring( "IOException" , e );
            return false;
        }
        finally
        {
        }
    }
}

