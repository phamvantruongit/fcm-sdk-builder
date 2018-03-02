package jp.co.bravesoft.my_gcm_example2;

//import android.support.v7.app.AppCompatActivity;
import android.content.Intent;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;

import biz.appvisor.push.android.sdk.AppVisorPush;

public class MainActivity extends ActionBarActivity
{
    private static final String TAG = "MainActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        //Log.d(TAG, "Build.VERSION.SDK_INT: " + Build.VERSION.SDK_INT);
        //Log.d(TAG, "AppVisorPushUtil.getApiLevel(): " + AppVisorPushUtil.getApiLevel());


        //sdk初期化(必須)
//AppVisorPush用のAPPIDを設定します。
        //String appID = "uIxoJSuq6b";
        //String appID = "kJ6Tvz9dGc";
        //String appID = "McPEpLDQUb";
        //String appID = "emUrt7qtxc";


        //if (1) { // APIレベルを判定
        /*
        if (AppVisorPushSetting.thisApiLevel >= 26) {
            this.appVisorPush.setJobService(MyJobService.class.getName());
        }

        this.appVisorPush.setService(AppvisorPushBackgroundService.class.getName());
        */

        //this.appVisorPush.setService(BackgroundService.class.getName());
        initAppvisor();
    }

    private void initAppvisor()
    {
        AppVisorPush appVisorPush;
        setContentView(R.layout.activity_main);

        appVisorPush = AppVisorPush.sharedInstance();
        String appID = "UK3vtZa06c";
        appVisorPush.setAppInfor(getApplicationContext(), appID);
//通知関連の内容を設定します。(送信者ID,通知アイコン,ステータスバーアイコン,通知で起動するClass名、デフォルトの通知タイトル)
        //this.appVisorPush.startPush("407066157166", R.mipmap.ic_launcher, R.mipmap.ic_launcher, MainActivity.class, getString(R.string.app_name));
        appVisorPush.startPush("890273406421", R.mipmap.ic_launcher, R.mipmap.ic_launcher, MainActivity.class, getString(R.string.app_name));
//Push反応率チェック(必須)
        appVisorPush.trackPushWithActivity(this);

        appVisorPush.setService(BackgroundService.class.getName());
    }

    //必須
    protected void onNewIntent (Intent intent)
    {
        super.onNewIntent(intent);
//画面表示時に再度起動された際にgetIntent()を更新する。
        setIntent(intent);
    }

    public void onStartClick(View view) {
        Intent i = new Intent(this, SimpleService.class);
        startService(i);
    }

    public void onPrintClick(View view) {
        Log.d("MainActivity", SharedObject.value == "" ? "empty" : SharedObject.value);
    }


}
