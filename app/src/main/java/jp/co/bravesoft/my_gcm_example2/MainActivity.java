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
        //String appID = "kJ6Tvz9dGc";
        //String appID = "McPEpLDQUb";
        String appID = "emUrt7qtxc";
        //String appID = "0GbyFCPEhb";

        //String appID = "UK3vtZa06c";
        //String appID = "uIxoJSuq6b";
        //String appID = "M2RF4vBbmb";

        //String appID = "McPEpLDQUb";


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
        //String appID = "UK3vtZa06c";
        //String appID = "McPEpLDQUb";
        //String appID = "uIxoJSuq6b";
        //String appID = "M2RF4vBbmb";
        //String appID = "WRw2QSwLTb";
        //String appID = "ck0k2X2mrb";
        //String appID = "uIxoJSuq6b";

        //String appID = "U26L9Adwbd";
        //String appID = "emUrt7qtxc";
        //String appID = "2AbBjrsZSc";
        //String appID = "IySdXJepnb";

        //String appID = "0GbyFCPEhb";
        String appID = "6i8rRaSqMc";
        //String appID = "0GbyFCPEhb";
        //String appID = "gSUHf3ugnb";
        //String appID = "Ea84v0OQbc";

        //String appID = "McPEpLDQUb";
        appVisorPush.setAppInfor(getApplicationContext(), appID,true);
        appVisorPush.setNotificationChannel("channelName", "channelDesc");
        //appVisorPush.setno
        //appVisorPush.setNotificationChannel("hoge", "hello world");

//通知関連の内容を設定します。(送信者ID,通知アイコン,ステータスバーアイコン,通知で起動するClass名、デフォルトの通知タイトル)
        //this.appVisorPush.startPush("407066157166", R.mipmap.ic_launcher, R.mipmap.ic_launcher, MainActivity.class, getString(R.string.app_name));
        //appVisorPush.startPush("890273406421", R.mipmap.ic_launcher, R.mipmap.ic_launcher, MainActivity.class, getString(R.string.app_name));

        appVisorPush.startPush("890273406421", R.mipmap.ic_launcher, R.mipmap.ic_launcher, R.drawable.errorstop , MainActivity.class, "マイタイトル");
        //appVisorPush.startPush("71332990448", R.mipmap.ic_launcher, R.mipmap.ic_launcher, MainActivity.class, "マイタイトル");
        //appVisorPush.startPush("750834641974", R.mipmap.ic_launcher, R.mipmap.ic_launcher, MainActivity.class, "マイタイトル");
//Push反応率チェック(必須)
        appVisorPush.trackPushWithActivity(this);

        //ActivityがAppVisorPushで起動かどうかの判断(Option)
        Log.d(TAG, "before checkIfStartByAppVisorPush");
        if (appVisorPush.checkIfStartByAppVisorPush(this))
        {
            Log.d(TAG, "after checkIfStartByAppVisorPush");
//例：Push内のメーセージ内容をAlertで表示させる。
            Bundle bundle = appVisorPush.getBundleFromAppVisorPush(this);
            String message = bundle.getString("message");
            String xString = bundle.getString("x");
            String yString = bundle.getString("y");
            String zString = bundle.getString("z");
            String wString = bundle.getString("w");
            Log.d(TAG, "get from bundle -> message: " + message + " x: " + xString +
                " y: " + yString +
                " z: " + zString +
                " w: " + wString
            );
        }
        /*
        if (AppVisorPush.requiresJobService()) {

            appVisorPush.setJobService(AppvisorPushJobService.class.getName());
//            appVisorPush.setJobService("AppvisorPushJobService");
        }
        else {
            appVisorPush.setService(AppvisorPushBackgroundService.class.getName());
//            appVisorPush.setService("AppvisorPushBackgroundService");
        }
        */

        EditText editItems1[] = {
                (EditText)(findViewById(R.id.editText1)),
                (EditText)(findViewById(R.id.editText2)),
                (EditText)(findViewById(R.id.editText3))
        };
        setupSpecialProperties(appVisorPush, editItems1, ((Button)findViewById(R.id.button1)), AppVisorPush.SpecialUserPropertyGroup1);

        EditText editItems2[] = {
                (EditText)(findViewById(R.id.editText4)),
                (EditText)(findViewById(R.id.editText5)),
                (EditText)(findViewById(R.id.editText6))
        };
        setupSpecialProperties(appVisorPush, editItems2, ((Button)findViewById(R.id.button2)), AppVisorPush.SpecialUserPropertyGroup2);

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
                        //appvisorPush.synchronizeUserProperties();
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
