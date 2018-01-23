package biz.appvisor.push.android.sdk;

import android.app.job.JobParameters;
import android.app.job.JobService;
import android.os.PersistableBundle;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Set;

public class MyJobService extends JobService {

    @Override
    public boolean onStartJob(JobParameters params) {
        PersistableBundle bundle = params.getExtras();
        Set<String> keys = bundle.keySet();
        Iterator<String> ite = keys.iterator();
        HashMap<String, String> map = new HashMap<String, String>();
        while (ite.hasNext()) {
            String key = ite.next();
//            Log.d("MyJobService", key + ": " + bundle.getString(key));
            map.put(key, bundle.getString(key));
        }

        new BackgroundService().execute(getApplicationContext(), map);
        return true;
    }

    @Override
    public boolean onStopJob(JobParameters params) {
        return false;
    }

}

