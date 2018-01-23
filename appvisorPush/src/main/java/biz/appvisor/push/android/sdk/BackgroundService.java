package biz.appvisor.push.android.sdk;

import android.content.Context;
import android.util.Log;

import java.util.HashMap;
import java.util.Iterator;

/**
 * Created by hirayamatakaaki on 2018/01/23.
 */

public class BackgroundService implements IAppvisorPushBackgroundService {

    public void execute(Context context, HashMap<String, String> params) {
        AsyncNetworkTask task = new AsyncNetworkTask(context);
        Iterator<String> i = params.keySet().iterator();
        while (i.hasNext()) {
            String key = i.next();
            Log.d("BackgroundService", key + ": " + params.get(key));
        }

        task.execute("http://dev-p.app-visor.com/");
    }
}
