package jp.co.bravesoft.my_gcm_example2;

import android.content.Context;
import android.content.Intent;
import android.util.Log;

import biz.appvisor.push.android.sdk.AppVisorPushSetting;
import biz.appvisor.push.android.sdk.IAppvisorPushBackgroundService;

/**
 * Created by hirayamatakaaki on 2018/01/23.
 */

public class BackgroundService implements IAppvisorPushBackgroundService {

    public void execute(Context context, Intent intent) {
        AsyncNetworkTask task = new AsyncNetworkTask(context);

        String x = intent.getStringExtra(AppVisorPushSetting.KEY_PUSH_X);
        String z = intent.getStringExtra(AppVisorPushSetting.KEY_PUSH_Z);
        String bg = intent.getStringExtra(AppVisorPushSetting.KEY_BACKGROUND_NOTIFICATION);
        Log.d("BackgroundService", "x: " + x + ", z: " + z + ", bg: " + bg);

        /*
        Iterator<String> i = params.keySet().iterator();
        while (i.hasNext()) {
            String key = i.next();
            Log.d("BackgroundService", key + ": " + params.get(key));
        }
        */

        task.execute("http://dev-p.app-visor.com/");
    }
}
