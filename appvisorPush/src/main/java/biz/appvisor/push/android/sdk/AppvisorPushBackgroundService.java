package biz.appvisor.push.android.sdk;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.IBinder;
import android.support.annotation.Nullable;

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
        Context context = getApplicationContext();
        IAppvisorPushBackgroundService service = AppVisorPushFirebaseMessagingService.getCallbackService(context);
        service.execute(context, intent);

        return super.onStartCommand(intent, flags, startId);
    }

}
