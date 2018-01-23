package biz.appvisor.push.android.sdk;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.IBinder;
import android.support.annotation.Nullable;

import java.util.HashMap;

/**
 * Created by bsfuji on 2017/05/12.
 */

public class AppvisorPushBackgroundService extends Service
{

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId)
    {
        Bundle bundle = intent.getExtras();
        HashMap<String, String> map = new HashMap<String, String>();
        if (bundle != null) {
            for (String key : bundle.keySet()) {
                if (!key.equals(AppVisorPushSetting.KEY_APPVISOR_PUSH_INTENT)) {
                    String value = (String)(bundle.get(key));
                    map.put(key, value);
                }
            }
        }

        Context context = getApplicationContext();
        IAppvisorPushBackgroundService service = AppVisorPushFirebaseMessagingService.getCallbackService(context);
        service.execute(context, map);

        return super.onStartCommand(intent, flags, startId);
    }

}
