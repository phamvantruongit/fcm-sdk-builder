package biz.appvisor.push.android.sdk;

import android.annotation.TargetApi;
import android.app.job.JobParameters;
import android.app.job.JobService;
import android.os.PersistableBundle;

import java.util.HashMap;

@TargetApi(26)
public class RichPushJobService extends JobService {

    private static final String TAG = "RichPushJobService";

    @TargetApi(26)
    @Override
    public boolean onStartJob(JobParameters params) {
        PersistableBundle bundle = params.getExtras();

        String title = bundle.getString("title");
        String message = bundle.getString("message");
        String className = bundle.getString("className");
        String pushIDStr = bundle.getString("pushIDStr");
        Boolean vibrationOnOff = bundle.getBoolean("vibrationOnOff");
        String contentFlg = bundle.getString("contentFlg");
        String contentURL = bundle.getString("contentURL");
        String urlFlag = bundle.getString("urlFlag");

        HashMap<String, String> hashMap = new HashMap<String, String>();
        hashMap.put("w", bundle.getString("params_w"));
        hashMap.put("x", bundle.getString("params_x"));
        hashMap.put("y", bundle.getString("params_y"));
        hashMap.put("z", bundle.getString("params_z"));

        final RichPush richPush = new RichPush(title, message, className, pushIDStr,
                hashMap, vibrationOnOff, contentFlg, contentURL, urlFlag);

        RichPushProcess process = new RichPushProcess(
            this.getApplicationContext(),
            richPush,
            this);
        process.mainProcess();
        return true;
    }

    @Override
    public boolean onStopJob(JobParameters params) {
        return false;
    }

}

