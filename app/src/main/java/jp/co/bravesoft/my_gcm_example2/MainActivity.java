package jp.co.bravesoft.my_gcm_example2;

//import android.support.v7.app.AppCompatActivity;
import android.content.Intent;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;

import biz.appvisor.push.android.sdk.AppVisorPush;
import biz.appvisor.push.android.sdk.AppVisorPushSetting;
import biz.appvisor.push.android.sdk.BackgroundPushNotificationReceiveService;
import biz.appvisor.push.android.sdk.MyJobService;

public class MainActivity extends ActionBarActivity
{
    private AppVisorPush appVisorPush;
    private static final String TAG = "MainActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);

        //Log.d(TAG, "Build.VERSION.SDK_INT: " + Build.VERSION.SDK_INT);
        //Log.d(TAG, "AppVisorPushUtil.getApiLevel(): " + AppVisorPushUtil.getApiLevel());

        setContentView(R.layout.activity_main);

        //sdk初期化(必須)
        this.appVisorPush = AppVisorPush.sharedInstance();
//AppVisorPush用のAPPIDを設定します。
        //String appID = "uIxoJSuq6b";
        String appID = "kJ6Tvz9dGc";
        this.appVisorPush.setAppInfor(getApplicationContext(), appID);
//通知関連の内容を設定します。(送信者ID,通知アイコン,ステータスバーアイコン,通知で起動するClass名、デフォルトの通知タイトル)
        //this.appVisorPush.startPush("407066157166", R.mipmap.ic_launcher, R.mipmap.ic_launcher, MainActivity.class, getString(R.string.app_name));
        this.appVisorPush.startPush("890273406421", R.mipmap.ic_launcher, R.mipmap.ic_launcher, MainActivity.class, getString(R.string.app_name));
//Push反応率チェック(必須)
        this.appVisorPush.trackPushWithActivity(this);

        //if (1) { // APIレベルを判定
        /*
        if (AppVisorPushSetting.thisApiLevel >= 26) {
            this.appVisorPush.setJobService(MyJobService.class.getName());
        }

        this.appVisorPush.setService(BackgroundPushNotificationReceiveService.class.getName());
        */

        this.appVisorPush.setService(BackgroundService.class.getName());
        //this.appVisorPush.setService(BackgroundService.class.getName());
    }

    //必須
    protected void onNewIntent (Intent intent)
    {
        super.onNewIntent(intent);
//画面表示時に再度起動された際にgetIntent()を更新する。
        setIntent(intent);
    }

}
