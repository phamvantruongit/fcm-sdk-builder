package jp.co.bravesoft.my_gcm_example2;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.util.Log;

import com.google.firebase.FirebaseApp;

import biz.appvisor.push.android.sdk.AppVisorPush;

public class SimpleService extends Service {
    private final String TAG = "SimpleService";

    @Override
    public void onCreate() {
        super.onCreate();
        Log.i(TAG, "onCreate");
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Log.i(TAG, "onStartCommand");
        SharedObject.value = "hello";

        initAppvisor();

        return START_STICKY;
    }

    private void initAppvisor()
    {
        //TAG = "SimpleService";
        Log.d(TAG, "initAppvisor");
        FirebaseApp.initializeApp(getApplicationContext());

        AppVisorPush appVisorPush = AppVisorPush.sharedInstance();
        //String appID = "UK3vtZa06c";
        String appID = "0GbyFCPEhb";

        appVisorPush.setAppInfor(getApplicationContext(), appID);
//通知関連の内容を設定します。(送信者ID,通知アイコン,ステータスバーアイコン,通知で起動するClass名、デフォルトの通知タイトル)
        //this.appVisorPush.startPush("407066157166", R.mipmap.ic_launcher, R.mipmap.ic_launcher, MainActivity.class, getString(R.string.app_name));
        appVisorPush.startPush("890273406421", R.mipmap.ic_launcher, R.mipmap.ic_launcher, MainActivity.class, getString(R.string.app_name));
//Push反応率チェック(必須)
//        appVisorPush.trackPushWithActivity(this);

//        appVisorPush.setService(BackgroundService.class.getName());
    }

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        Log.i(TAG, "onDestroy");
    }
}
