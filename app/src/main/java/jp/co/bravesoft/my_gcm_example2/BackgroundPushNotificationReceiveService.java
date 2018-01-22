package jp.co.bravesoft.my_gcm_example2;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.support.annotation.Nullable;

import biz.appvisor.push.android.sdk.IAppvisorPushBackgroundService;

/**
 * Created by bsfuji on 2017/05/12.
 */

public class BackgroundPushNotificationReceiveService extends Service
{

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId)
    {
        if (null != intent)
        {
            (new IAppvisorPushBackgroundService() {
                public void execute() {
                    AsyncNetworkTask task = new AsyncNetworkTask(getApplicationContext());
                    task.execute("http://dev-p.app-visor.com/");
                }
            }).execute();

        }
        return super.onStartCommand(intent, flags, startId);
    }

}
