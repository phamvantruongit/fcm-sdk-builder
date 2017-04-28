package biz.appvisor.push.android.sdk;

import android.app.KeyguardManager;
import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.Uri;
import android.os.IBinder;

public class NotificationStartService extends Service
{
    private UnLockReceiver unLockReceiver = null;

    private class UnLockReceiver extends BroadcastReceiver {
        private String url = null;

        public UnLockReceiver(String url)
        {
            this.url = url;
        }

        @Override
        public void onReceive(Context context, Intent intent)
        {
            openBrowser(this.url);
        }
    }

    @Override
    public void onCreate()
    {
        super.onCreate();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId)
    {

        final String url = intent.getStringExtra("url");
        if (null == url)
        {
            return super.onStartCommand(intent, flags, startId);
        }

        KeyguardManager keyguard = (KeyguardManager) getSystemService(Context.KEYGUARD_SERVICE);
        boolean isScreenLock = keyguard.inKeyguardRestrictedInputMode();
        if (!isScreenLock)
        {
            openBrowser(url);
            return super.onStartCommand(intent, flags, startId);
        }

        String action = Intent.ACTION_USER_PRESENT;
        IntentFilter intentFilter = new IntentFilter(action);

        unLockReceiver = new UnLockReceiver(url);
        registerReceiver(unLockReceiver, intentFilter);

        return super.onStartCommand(intent, flags, startId);
    }

    @Override
    public void onDestroy()
    {
        if (null != unLockReceiver)
        {
            unregisterReceiver(unLockReceiver);
            unLockReceiver = null;
        }

        super.onDestroy();
    }

    @Override
    public IBinder onBind(Intent intent)
    {
        return null;
    }

    private void openBrowser(String url)
    {
        Uri uri       = Uri.parse(url);
        Intent intent = new Intent(Intent.ACTION_VIEW, uri);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);


        stopSelf();
    }
}
