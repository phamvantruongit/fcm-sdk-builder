package jp.co.bravesoft.my_gcm_example2;

import android.annotation.TargetApi;
import android.app.job.JobParameters;
import android.app.job.JobService;
import android.content.Context;
import android.content.Intent;
import android.os.PersistableBundle;

import java.util.Iterator;
import java.util.Set;

import biz.appvisor.push.android.sdk.AppVisorPushFirebaseMessagingService;

public class AppvisorPushJobService extends JobService {

    @TargetApi(26)
    @Override
    public boolean onStartJob(JobParameters params) {
        PersistableBundle bundle = params.getExtras();
        Set<String> keys = bundle.keySet();
        Iterator<String> ite = keys.iterator();

        Intent intent = new Intent();
        while (ite.hasNext()) {
            String key = ite.next();
            intent.putExtra(key, bundle.getString(key));
        }

        Context context = getApplicationContext();
        IAppvisorPushBackgroundService service = AppvisorPushBackgroundService.getCallbackService(context);
        service.execute(context, intent);
        return true;
    }

    @Override
    public boolean onStopJob(JobParameters params) {
        return false;
    }

}

