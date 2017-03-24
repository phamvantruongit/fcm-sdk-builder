package biz.appvisor.push.android.sdk;

import com.google.android.gcm.GCMBroadcastReceiver;
import android.content.Context;

public class AppVisorPushBroadcastReceiver extends GCMBroadcastReceiver 
{
	
	  protected String getGCMIntentServiceClassName(Context context)
	  {
	    String className = "biz.appvisor.push.android.sdk.AppVisorPushIntentService";
	    return className;
	  }
}
