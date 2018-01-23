package biz.appvisor.push.android.sdk;

import android.content.Context;
import android.content.Intent;

/**
 * Created by hirayamatakaaki on 2018/01/22.
 */

public interface IAppvisorPushBackgroundService {
    void execute(Context context, Intent intent);
}
