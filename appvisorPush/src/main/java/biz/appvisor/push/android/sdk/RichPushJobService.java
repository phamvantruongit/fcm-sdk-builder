package biz.appvisor.push.android.sdk;

import android.annotation.TargetApi;
import android.app.job.JobParameters;
import android.app.job.JobService;
import android.os.PersistableBundle;
import android.util.Log;

import java.util.Iterator;
import java.util.Set;

@TargetApi(26)
public class RichPushJobService extends JobService {

    private static final String TAG = "RichPushJobService";

    @TargetApi(26)
    @Override
    public boolean onStartJob(JobParameters params) {
        Log.d(TAG, "onStartJob");

        PersistableBundle bundle = params.getExtras();
        Set<String> keys = bundle.keySet();
        Iterator<String> ite = keys.iterator();

        while (ite.hasNext()) {
            String key = ite.next();
            Log.d(TAG, key + " " + bundle.getString(key));
        }
        return true;
    }

    @Override
    public boolean onStopJob(JobParameters params) {
        return false;
    }

}

