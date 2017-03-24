package biz.appvisor.push.android.sdk;

import java.util.EventListener;


public  interface ChangePushStatusListener extends EventListener 
{
	public void changeStatusSucceeded(boolean nowStatus);
	
	public void changeStatusFailed(boolean nowStatus);
}
