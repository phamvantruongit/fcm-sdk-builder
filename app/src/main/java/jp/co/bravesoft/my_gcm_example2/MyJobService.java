package jp.co.bravesoft.my_gcm_example2;

import android.app.job.JobParameters;
import android.app.job.JobService;

import biz.appvisor.push.android.sdk.IAppvisorPushBackgroundService;

public class MyJobService extends JobService {

    @Override
    public boolean onStartJob(JobParameters params) {
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

