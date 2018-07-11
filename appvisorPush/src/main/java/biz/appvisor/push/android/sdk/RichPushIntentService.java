package biz.appvisor.push.android.sdk;

import android.app.IntentService;
import android.app.NotificationManager;
import android.content.Context;
import android.content.Intent;

public class RichPushIntentService extends IntentService
{
	private Context             applicationContext  = null;
	private NotificationManager notificationManager = null;
	private RichPush            richPush            = null;
	
	public RichPushIntentService() {
		super("RichPushIntentService");
	}
	
	public RichPushIntentService(String name) {
		super(name);
	}

	@Override
	protected void onHandleIntent(Intent intent) {
		this.applicationContext  = this.getApplicationContext();
		this.notificationManager = (NotificationManager)getSystemService(Context.NOTIFICATION_SERVICE);
		this.richPush            = (RichPush)intent.getSerializableExtra("richPush");

		this.mainProcess();
	}

}
