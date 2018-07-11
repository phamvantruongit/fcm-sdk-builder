package biz.appvisor.push.android.sdk;

import android.app.NotificationManager;
import android.app.Service;
import android.content.Context;
import android.content.ContextWrapper;

public class RichPushBackgroundProcess extends RichPushProcess {

    private Service service;

    RichPushBackgroundProcess(Context applicationContext, RichPush richPush, Service service)
    {
        this.service = service;
        setup(applicationContext, richPush);

        this.notificationManager = (NotificationManager)getContextWrapper().getSystemService(Context.NOTIFICATION_SERVICE);
    }

    protected ContextWrapper getContextWrapper() {
        return this.service;
    }

    protected void stopSelf() {
        this.service.stopSelf();
    }
}
