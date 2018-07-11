package biz.appvisor.push.android.sdk;

import android.app.IntentService;
import android.content.Intent;

public class RichPushIntentService extends IntentService
{
	public RichPushIntentService() {
		super("RichPushIntentService");
	}
	
	public RichPushIntentService(String name) {
		super(name);
	}

	@Override
	protected void onHandleIntent(Intent intent) {
		RichPushProcess process =
            new RichPushProcess(
            	this.getApplicationContext(),
            	(RichPush)intent.getSerializableExtra("richPush"),
            	this);
		process.mainProcess();
	}

	@Override
	public void onDestroy() {
		super.onDestroy();
	}
}
