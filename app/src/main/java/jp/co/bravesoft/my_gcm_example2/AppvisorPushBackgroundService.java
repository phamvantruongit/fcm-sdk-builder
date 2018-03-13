package jp.co.bravesoft.my_gcm_example2;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.IBinder;
import android.support.annotation.Nullable;

import biz.appvisor.push.android.sdk.AppVisorPushFirebaseMessagingService;
import biz.appvisor.push.android.sdk.AppVisorPushUtil;

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
        IAppvisorPushBackgroundService service = getCallbackService(context);
        service.execute(context, intent);

        return super.onStartCommand(intent, flags, startId);
    }

    public static IAppvisorPushBackgroundService getCallbackService(Context context) {
        //String serviceName = AppVisorPushUtil
         //       .getPushCallbackServiceName(context);
        //String serviceName = AppVisorPushUtil.getPushCallbackServiceName(context);
        String serviceName = BackgroundService.class.getName();

        if (serviceName == null || "".equals(serviceName)) {
            return null;
        }

        Class<?> callBackService = null;
        try {
            callBackService = Class.forName(serviceName);
        } catch (ClassNotFoundException e) {
//					e.printStackTrace();
        }

        IAppvisorPushBackgroundService instance = null;
        try {
            instance = (IAppvisorPushBackgroundService)(callBackService.newInstance());
        }
        catch (IllegalAccessException e) {

        }
        catch (InstantiationException e) {

        }
        return instance;
    }


}
