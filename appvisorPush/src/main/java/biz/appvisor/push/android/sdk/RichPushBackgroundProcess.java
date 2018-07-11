package biz.appvisor.push.android.sdk;

import android.app.NotificationManager;
import android.app.Service;
import android.content.Context;
import android.content.ContextWrapper;

public class RichPushBackgroundProcess extends RichPushProcess {


    RichPushBackgroundProcess (Context applicationContext, RichPush richPush, Service service) {
        super(applicationContext, richPush, service);
    }
}
