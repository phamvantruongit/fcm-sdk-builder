package jp.co.bravesoft.my_gcm_example2;

import android.app.IntentService;
import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.util.Log;

import com.google.firebase.FirebaseApp;
import com.google.firebase.iid.FirebaseInstanceId;

import java.io.IOException;

import biz.appvisor.push.android.sdk.AppVisorPush;

public class TokenDeletionService extends IntentService {
    private final String TAG = "TokenDeletionService";

    public TokenDeletionService(String name) {
        super(name);
        Log.i(TAG, "constructor");
    }

    public TokenDeletionService() {
        super("SimpleService");
        Log.i(TAG, "constructor");
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        Log.i(TAG, "onDestroy");
    }

    @Override
    protected void onHandleIntent(Intent arg0) {
        Log.i(TAG, "onHandleIntent");


        try
        {
            /*
            // Check for current token
            String originalToken = getTokenFromPrefs();
            Log.d(TAG, "Token before deletion: " + originalToken);
            */

            // Resets Instance ID and revokes all tokens.
            FirebaseInstanceId.getInstance().deleteInstanceId();

            /*
            // Clear current saved token
            saveTokenToPrefs("");

            // Check for success of empty token
            String tokenCheck = getTokenFromPrefs();
            Log.d(TAG, "Token deleted. Proof: " + tokenCheck);

            // Now manually call onTokenRefresh()
            Log.d(TAG, "Getting new token");
            FirebaseInstanceId.getInstance().getToken();
            */
        }
        catch (IOException e)
        {
            e.printStackTrace();
        }


    }

}
