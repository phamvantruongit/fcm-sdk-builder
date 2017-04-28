package biz.appvisor.push.android.sdk;

import android.content.Context;

import com.google.android.gcm.GCMBroadcastReceiver;

public class AppVisorPushBroadcastReceiver extends GCMBroadcastReceiver 
{
	
	  protected String getGCMIntentServiceClassName(Context context)
	  {
	    String className = "biz.appvisor.push.android.sdk.AppVisorPushIntentService";
	    return className;
	  }
}
