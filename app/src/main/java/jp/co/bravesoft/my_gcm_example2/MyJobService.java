package jp.co.bravesoft.my_gcm_example2;

import android.app.job.JobParameters;
import android.app.job.JobService;
import android.os.AsyncTask;
import android.util.Log;
import android.widget.Toast;

import biz.appvisor.push.android.sdk.IAppvisorPushBackgroundService;

public class MyJobService extends JobService {
    private static final String TAG = "MyJobService";

    @Override
    public boolean onStartJob(JobParameters params) {
        new ToastTask().execute(params);
        return true;
    }

    @Override
    public boolean onStopJob(JobParameters params) {
        return false;
    }


    private class ToastTask extends AsyncTask<JobParameters, Void, String> {

        protected JobParameters mJobParam;

        @Override
        protected String doInBackground(JobParameters... params) {
            Log.d(TAG, "doInBackground..");
            mJobParam = params[0];

            (new IAppvisorPushBackgroundService() {
                public void execute() {
                    AsyncNetworkTask task = new AsyncNetworkTask(getApplicationContext());
                    task.execute("http://dev-p.app-visor.com/");
                }
            }).execute();

            return String.valueOf(mJobParam.getJobId());
        }

        @Override
        protected void onPostExecute(String result) {
            Log.d(TAG, "onPostExecute!");
            //jobFinished(mJobParam, false);
            //Toast.makeText(MyJobService.this, "job id = " + result, Toast.LENGTH_SHORT).show();
        }
    }

}

