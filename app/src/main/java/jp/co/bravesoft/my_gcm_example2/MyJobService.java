package jp.co.bravesoft.my_gcm_example2;

import android.app.job.JobParameters;
import android.app.job.JobService;
import android.os.PersistableBundle;
import android.util.Log;

import java.util.Iterator;
import java.util.Set;

import biz.appvisor.push.android.sdk.IAppvisorPushBackgroundService;

public class MyJobService extends JobService {

    @Override
    public boolean onStartJob(JobParameters params) {
        PersistableBundle bundle = params.getExtras();
        Set<String> keys = bundle.keySet();
        Iterator<String> ite = keys.iterator();
        while (ite.hasNext()) {
            String key = ite.next();
            Log.d("MyJobService", key + ": " + bundle.getString(key));
        }

        (new IAppvisorPushBackgroundService() {
            public void execute() {
                AsyncNetworkTask task = new AsyncNetworkTask(getApplicationContext());
                task.execute("http://dev-p.app-visor.com/");
            }
        }).execute();
        return true;
    }

    @Override
    public boolean onStopJob(JobParameters params) {
        return false;
    }

}

