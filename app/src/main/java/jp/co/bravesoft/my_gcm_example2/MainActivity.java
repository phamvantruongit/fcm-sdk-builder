package jp.co.bravesoft.my_gcm_example2;

import android.support.v7.app.AppCompatActivity;
import android.content.Intent;
import android.os.Looper;
//import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import java.util.ArrayList;
import java.util.List;

import biz.appvisor.push.android.sdk.AppVisorPush;
import biz.appvisor.push.android.sdk.AppVisorPushSetting;
import biz.appvisor.push.android.sdk.AppVisorPushUtil;

//public class MainActivity extends ActionBarActivity
public class MainActivity extends AppCompatActivity
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
        //String appID = "0GbyFCPEhb";
        String appID = "UK3vtZa06c";


        //if (1) { // APIレベルを判定
        /*
        if (AppVisorPushSetting.thisApiLevel >= 26) {
            this.appVisorPush.setJobService(MyJobService.class.getName());
        }

        this.appVisorPush.setService(AppvisorPushBackgroundService.class.getName());
        */

        setContentView(R.layout.activity_main);
        //this.appVisorPush.setService(BackgroundService.class.getName());
        initAppvisor();
//        Intent i = new Intent(this, SimpleService.class);
//        startService(i);
    }

    private void initAppvisor()
    {
        AppVisorPush appVisorPush = AppVisorPush.sharedInstance();
        String appID = "UK3vtZa06c";
        //String appID = "0GbyFCPEhb";
        appVisorPush.setAppInfor(getApplicationContext(), appID,true);
        appVisorPush.setNotificationChannel("channelName", "channelDesc");
//通知関連の内容を設定します。(送信者ID,通知アイコン,ステータスバーアイコン,通知で起動するClass名、デフォルトの通知タイトル)
        //this.appVisorPush.startPush("407066157166", R.mipmap.ic_launcher, R.mipmap.ic_launcher, MainActivity.class, getString(R.string.app_name));
        appVisorPush.startPush("890273406421", R.mipmap.ic_launcher, R.mipmap.ic_launcher, MainActivity.class, getString(R.string.app_name));
//Push反応率チェック(必須)
        appVisorPush.trackPushWithActivity(this);

        if (AppVisorPush.requiresJobService()) {

            appVisorPush.setJobService(AppvisorPushJobService.class.getName());
        }
        else {
            appVisorPush.setService(AppvisorPushBackgroundService.class.getName());
//            appVisorPush.setService("AppvisorPushBackgroundService");
        }

        EditText editItems[] = {
                (EditText)(findViewById(R.id.editText1)),
                (EditText)(findViewById(R.id.editText2)),
                (EditText)(findViewById(R.id.editText3))
        };
        Button button = ((Button)findViewById(R.id.button1));
        setupSpecialProperties(appVisorPush, editItems, button, AppVisorPush.SpecialUserPropertyGroup1);
    }


    private void setupSpecialProperties(final AppVisorPush appvisorPush, final EditText editItems[], Button button, final int specialPropertyGroup) {
       //appvisorPush.
        List<String> subscribed = (List<String>)appvisorPush.getUserPropertyWithGroup(specialPropertyGroup);

        for (int i = 0; i < subscribed.size(); i++) {
            editItems[i].setText(subscribed.get(i));
        }

        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                List<String> params = new ArrayList<String>();

                for (int i = 0; i < editItems.length; i++) {
                    String param = editItems[i].getText().toString();
                    if (!param.equals("")) {
                        params.add(param);
                    }
                }

                if (params.size() >= 1) {

                    boolean saved = appvisorPush.setUserPropertyWithGroup(params, specialPropertyGroup);
                    if (saved == true) {
                        Log.d("MainActivity", "succeeded to save params");
                    }
                }
            }
        });
    }

    //必須
    protected void onNewIntent (Intent intent)
    {
        super.onNewIntent(intent);
//画面表示時に再度起動された際にgetIntent()を更新する。
        setIntent(intent);
    }

    public void onStartClick(View view) {
//        Intent i = new Intent(this, SimpleService.class);
 //       startService(i);
//        initAppvisor();
    }

    // onDeleteClick
    public void onDeleteClick (View view) {
        Intent i = new Intent(this, TokenDeletionService.class);
        startService(i);
    }

    public void onPrintClick(View view) {
        Log.d("MainActivity", SharedObject.value == "" ? "empty" : SharedObject.value);
    }


}
