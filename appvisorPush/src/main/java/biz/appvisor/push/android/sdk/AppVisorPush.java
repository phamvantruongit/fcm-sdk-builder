package biz.appvisor.push.android.sdk;

import android.annotation.TargetApi;
import android.app.Activity;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.Looper;
import android.os.Message;
import android.text.TextUtils;

import com.google.firebase.iid.FirebaseInstanceId;

import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.StatusLine;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class AppVisorPush 
{
    public final static int	UserPropertyGroup1        = 1;
    public final static int	UserPropertyGroup2        = 2;
    public final static int UserPropertyGroup3        = 3;
    public final static int UserPropertyGroup4        = 4;
    public final static int UserPropertyGroup5        = 5;
    public final static int SpecialUserPropertyGroup1 = 101;
	public final static int SpecialUserPropertyGroup2 = 102;

	static AppVisorPush 	_appvisorPushInstance;
	
    private String 			appTrackingKey;
    private Context 		appContext;
   
    private static final HandlerThread pushSenderThread = loadThread(AppVisorPushSender.class.getSimpleName());
    
    private static Handler pushSenderHandler = null;
    
    public ChangePushStatusListener changeStatusListener;

	private MessagingInterface messagingInterface = null;
	private boolean launched = false;

	public static AppVisorPush sharedInstance( )
	{
		if(_appvisorPushInstance == null)
		{
			_appvisorPushInstance = new AppVisorPush();
		}
		
		return _appvisorPushInstance;
	}
	
	public void setService(Context context ,String serviceName)
	{
		AppVisorPushUtil.savePushCallbackServiceName(context, serviceName);
	}

	public void setService(String serviceName)
	{
		setService(this.appContext, serviceName);
	}

	public void setJobService(Context context ,String serviceNmae)
	{
		AppVisorPushUtil.savePushCallbackJobServiceName(context, serviceNmae);
	}

	public void setJobService(String serviceNmae)
	{
		setJobService(this.appContext, serviceNmae);
	}

	public static boolean requiresJobService()
	{
		return higherOrEqualsToOreo();
	}

	private static boolean requiresNotificationChannel()
	{
		return higherOrEqualsToOreo();
	}

	private static boolean higherOrEqualsToOreo()
	{
		return AppVisorPushSetting.thisApiLevel >= 26;
	}

	public void setAppInfor( Context context , String trackingKey )
	{
		setAppInfor( context , trackingKey , false );
	}

	public void setAppInfor( Context context, String trackingKey, boolean debuggable)
	{
		if (context == null)
        {
            throw new IllegalArgumentException("The context of application is required!");
        }
		
        if ( TextUtils.isEmpty( trackingKey ) )
        {
            throw new IllegalArgumentException("A valid app Tracking Key is required!");
        }
        
        this.appContext 	= !(context.getClass().getName().equals("android.test.RenamingDelegatingContext")) && AppVisorPushSetting.thisApiLevel >= 8 ? context.getApplicationContext() : context;
//		this.messagingInterface = new GoogleMessagingInterface(this.appContext);
		this.messagingInterface = new FirebaseMessagingInterface(this.appContext);

        this.appTrackingKey = trackingKey;
        
        AppVisorPushUtil.saveAppTrackingKey( this.appContext , this.appTrackingKey );
        
        AppVisorPushSetting.allowLogOutput = debuggable;
	}

	@TargetApi(26)
	public void setNotificationChannel(String name, String description)
	{
		if (!requiresNotificationChannel())
		{
			return;
		}

		NotificationManager mNotificationManager =
				(NotificationManager) this.appContext.getSystemService(Context.NOTIFICATION_SERVICE);

		NotificationChannel mChannel = new NotificationChannel(
				AppVisorPushSetting.DEFAULT_NOTIFICATION_CHANNEL_ID,
				name,
				AppVisorPushSetting.DEFAULT_NOTIFICATION_CHANNEL_IMPORTANCE
		);
		mChannel.setDescription(description);
		mChannel.enableVibration(true);
		mChannel.setVibrationPattern(AppVisorPushSetting.DEFAULT_NOTIFICATION_CHANNEL_VIBRATION_PATTERN);

		mNotificationManager.createNotificationChannel(mChannel);
	}

	public String getDeviceID()
	{
		if (this.appContext == null)
        {
			 throw new IllegalArgumentException("The context of application is required!");
        }
		
		String deviceIDStr = AppVisorPushUtil.getDeviceUUID(this.appContext , this.appTrackingKey);
		return deviceIDStr;
	}
	
	public boolean getPushRecieveStatus()
    {
		if (this.appContext == null)
        {
			 throw new IllegalArgumentException("The context of application is required!");
        }
		
		boolean onOff ;
    	int pushStatus = AppVisorPushUtil.getPushStatus(this.appContext);
    	if( pushStatus != 0 )
    	{
    		onOff = true;
    	}
    	else
    	{
    		onOff = false;	 
    	}
    	return onOff; 
    }
	
    public void startPushInService(String senderID,int pushIconID,Class<?> classToCallBack,String title)
    {	
    	startPushInService(senderID,pushIconID,pushIconID,classToCallBack,title);
    }
	
    public void startPushInService(String senderID, int pushIconID, int statusbarIconID, Class<?> classToCallBack, String title)
	{
    	startPush(senderID, pushIconID, statusbarIconID, classToCallBack, title, true);
	}
    
    public void startPush(String senderID, int pushIconID, int statusbarIconID, Class<?> classToCallBack, String title)
	{
    	startPush(senderID, pushIconID, statusbarIconID, classToCallBack, title, false);
	}
    
    public void startPush(String senderID,int pushIconID,Class<?> classToCallBack,String title)
    {	
    	startPush(senderID,pushIconID,pushIconID,classToCallBack,title);
    }
    
	public void startPush(String senderID, int pushIconID, int statusbarIconID, Class<?> classToCallBack, String title , boolean isInService)
	{
		if (TextUtils.isEmpty(senderID))
        {
            throw new IllegalArgumentException("A valid senderID is required!");
        }
    	
        if (TextUtils.isEmpty(title))
        {
            throw new IllegalArgumentException("A valid title is required!");
        }
    	
    	AppVisorPushUtil.savePushSenderID( this.appContext, senderID );
    	AppVisorPushUtil.savePushIconID( this.appContext, pushIconID );
    	AppVisorPushUtil.saveStatusBarIconID( this.appContext, statusbarIconID );
    	AppVisorPushUtil.savePushAppName( this.appContext, title );
    	AppVisorPushUtil.savePushCallbackClassName( this.appContext, classToCallBack.getName() );

    	// mmbi only enable code. get UIM_ID *_uimid.jar
    	/*
    	TelephonyManager tm = (TelephonyManager)this.appContext.getSystemService(Context.TELEPHONY_SERVICE); 
    	String uimid = tm.getSimSerialNumber();
    	if(uimid == null)
    	{
    		AppVisorPushUtil.appVisorPushLog("UIM_ID is null");
    	}else{
    		AppVisorPushUtil.appVisorPushLog("UIM_ID:"+uimid);
    	}
    	
    	this.setUserPropertyWithGroup(uimid , UserPropertyGroup1);
    	*/

		this.messagingInterface.checkDevice();

		if ( this.messagingInterface.requiresRegistration() )
		{
			this.messagingInterface.register( senderID );
		}
		else
		{
			this.launchWithToken(isInService);
   	 	}
	}

	private void launchWithToken(boolean isInService)
	{
		this.launched = true;
		final String deviceToken = this.messagingInterface.getDeviceToken();
		AppVisorPushUtil.appVisorPushLog( "already had device token:" + deviceToken );
		AppVisorPushUtil.savePushToken(this.appContext, deviceToken);
		//send user info only when token EXISTS
        AppVisorPush.startSendDeviceInfor(this.appContext, isInService);
	}

    static void startSendDeviceInfor(Context context)
    {
    	AppVisorPush.startSendDeviceInfor(context, false);
    }
    
    static void startSendDeviceInfor(Context context, boolean isInService)
    {
    	if ( null == context)
        {
            throw new IllegalArgumentException("context is null can't send token.");
        }
    	
    	String appTrackingKey = AppVisorPushUtil.getAppTrackingKey(context);
    	
    	if(appTrackingKey == null || appTrackingKey.equals("") )
    	{
    		throw new IllegalArgumentException("appTrackingKey is empty.");
    	}
    	
    	pushSenderHandler = new AppVisorPushSender( context , appTrackingKey , pushSenderThread.getLooper() );
    	
    	if ( isInService )
    	{	
    		pushSenderHandler.sendMessage( pushSenderHandler.obtainMessage( AppVisorPushSetting.msgDoSendPushTokenInService ) );
    	}
    	else
    	{
    		pushSenderHandler.sendMessage( pushSenderHandler.obtainMessage( AppVisorPushSetting.msgDoSendPushToken ) );
    	}
    }
    
    public boolean setUserPropertyWithGroup(String propertyValue,int propertyGroup)
    {
    	return setUserPropertyWithGroup(this.appContext,propertyValue,propertyGroup);
    }
    
    public boolean setUserPropertyWithGroup(List<String> propertyValue,int propertyGroup)
    {
    	return setUserPropertyWithGroup(this.appContext,propertyValue,propertyGroup);
    }
    
    public boolean setUserPropertyWithGroup(Context context,List<String> propertyValue,int propertyGroup)
    {
    	boolean result = false;
    	
    	if ( propertyGroup >= AppVisorPush.SpecialUserPropertyGroup1 && null != propertyValue)
    	{
    		String valueInString = TextUtils.join(AppVisorPushSetting.PROPERTY_DELIMITER, propertyValue);
    		
    		if ( valueInString.equals("") == true )
    		{
    			valueInString = AppVisorPushSetting.PROPERTY_DEFAULT_VALUE;
    		}
    		
    		result = this.setUserPropertyWithGroup(context, valueInString, propertyGroup);
    	}
    	
    	return result;
    }
    
    public Object getUserPropertyWithGroup(Context context,int propertyGroup)
    {	
    	String valueString = AppVisorPushUtil.getUserProperties(context, propertyGroup) ;
    	
    	if ( propertyGroup >= AppVisorPush.SpecialUserPropertyGroup1 )
    	{
    		List<String> valueList = new ArrayList<String>();
    		if( valueString.equals(AppVisorPushSetting.PROPERTY_DEFAULT_VALUE) == false)
    		{
    			valueList = Arrays.asList( TextUtils.split(valueString, AppVisorPushSetting.PROPERTY_DELIMITER_GREP) );
    		}
    		
    		return valueList;
    	}
    	else
    	{
    		if( valueString.equals(AppVisorPushSetting.PROPERTY_DEFAULT_VALUE) )
    		{
    			valueString = "";
    		}
    		return valueString;
    	}
    }
    
    public Object getUserPropertyWithGroup(int propertyGroup)
    {
    	return this.getUserPropertyWithGroup(this.appContext, propertyGroup);
    }

    public boolean setUserPropertyWithGroup(Context context,String propertyValue,int propertyGroup)
    {	
    	boolean result = false;
    	
    	if ( null == context)
        {
            throw new IllegalArgumentException("context is null can't send token.");
        }
    	
    	String appTrackingKey = AppVisorPushUtil.getAppTrackingKey(context);
    	
    	if(appTrackingKey == null || appTrackingKey.equals("") )
    	{
    		throw new IllegalArgumentException("appTrackingKey is empty.");
    	}

		final Integer[] props = {
            AppVisorPush.UserPropertyGroup1,
            AppVisorPush.UserPropertyGroup2,
            AppVisorPush.UserPropertyGroup3,
            AppVisorPush.UserPropertyGroup4,
            AppVisorPush.UserPropertyGroup5,
            AppVisorPush.SpecialUserPropertyGroup1,
            AppVisorPush.SpecialUserPropertyGroup2};

		if (!Arrays.asList(props).contains(propertyGroup))
        {
    		AppVisorPushUtil.appVisorPushLog("avoid UserPropertyGroup :" + propertyGroup + " .");
    		return result;
        }
    	
    	String existsValue = AppVisorPushUtil.getUserProperties(context, propertyGroup);
    	
    	if (propertyValue == null)
    	{
            if (existsValue != null && (!(existsValue.equals(AppVisorPushSetting.PROPERTY_DEFAULT_VALUE))) )
            {
            	if ( AppVisorPushUtil.saveIfUserPropertiesChanged(context, 1) )
            	{
            		result = AppVisorPushUtil.saveUserProperties(context, propertyGroup, AppVisorPushSetting.PROPERTY_DEFAULT_VALUE);
            	}
            }
    	}else{
    		if(existsValue == null || (!(propertyValue.equals(existsValue))))
    		{
    			if (AppVisorPushUtil.saveIfUserPropertiesChanged(context, 1))
    			{
    				result = AppVisorPushUtil.saveUserProperties(context, propertyGroup, propertyValue);
    			}
    		}
    		else
    		{
    			result = true;
    		}
    	}
    	
    	return result;
    }
    
    public void synchronizeUserProperties()
    {
    	synchronizeUserProperties(this.appContext);
    }
    
    public void synchronizeUserProperties(Context context)
    {
    	if ( null == context)
        {
            throw new IllegalArgumentException("context is null, can't send token.");
        }
    	
    	String appTrackingKey = AppVisorPushUtil.getAppTrackingKey(context);
    	
    	if(appTrackingKey == null || appTrackingKey.equals("") )
    	{
    		throw new IllegalArgumentException("appTrackingKey is empty.");
    	}
    	
    	String token = AppVisorPushUtil.getPushToken(context);
    	if (null == token || token.equals(""))
    	{
    		AppVisorPushUtil.appVisorPushLog("avoid registrationId,synchronize User Properties canceled.");
    		return;
    	}
    	  	
    	if(AppVisorPushUtil.getIfUserPropertiesChanged(context) == 0)
    	{
    		AppVisorPushUtil.appVisorPushLog("User Properties are unchanged,synchronize User Properties canceled.");
    		return;
    	}

		if (null == pushSenderHandler)
		{
			pushSenderHandler = new AppVisorPushSender(this.appContext, appTrackingKey, pushSenderThread.getLooper());
		}
    	pushSenderHandler.sendMessage( pushSenderHandler.obtainMessage(AppVisorPushSetting.msgDoSynchronizeUserProperties) );
    }
    
    public void changePushReceiveStatus(boolean onOff)
    {
		if (null == this.appContext)
		{
			 throw new IllegalArgumentException("The context of application is required!");
		}

		if ( this.messagingInterface.isRegistered() )
		{ 
			int statusByInt = onOff ? 1 : 0;
			
			int nowStatus 	= AppVisorPushUtil.getPushStatus( this.appContext );
			
			if (statusByInt != nowStatus)
			{
				if (null == pushSenderHandler)
				{
					pushSenderHandler = new AppVisorPushSender(this.appContext, appTrackingKey, pushSenderThread.getLooper());
				}
				pushSenderHandler.sendMessage( pushSenderHandler.obtainMessage(AppVisorPushSetting.msgDoSetPushOnOff ,  Integer.valueOf(statusByInt)  ) );
			}
		}
		else
		{
			int statusByInt = onOff ? 1 : 0;
			
			int nowStatus 	= AppVisorPushUtil.getPushStatus( this.appContext );
			
			if (statusByInt != nowStatus)
			{
				AppVisorPush.mainHandler.obtainMessage( AppVisorPushSetting.msgSetPushStatusFailed, nowStatus ).sendToTarget();
			}
		}
    }
    
    public void trackPushWithActivity(Activity activity)
    {
    	if (null == activity)
		{
			 throw new IllegalArgumentException("activity is null.");
		}
    	if(activity.getIntent().getBooleanExtra( AppVisorPushSetting.KEY_APPVISOR_PUSH_INTENT , false ) )
    	{
    		Intent activityIntent = activity.getIntent();
    		String pushIDStr = activityIntent.getStringExtra( AppVisorPushSetting.KEY_PUSH_TRACKING_ID );
    		if(pushIDStr != null && pushIDStr.length() > 0)
    		{
    			int lastPushID = AppVisorPushUtil.getLastPushTrackingID( activity.getApplicationContext() );
    			int newPushID = 0;
    			
    			try
    			{
    				newPushID = Integer.parseInt(pushIDStr);
    			}
    			catch(NumberFormatException e)
    			{
    				AppVisorPushUtil.appVisorPushWaring( "NumberFormatException" , e );
    				newPushID = 0;
    			}
    			
    			if( lastPushID !=  newPushID )
    			{
    				pushSenderHandler.sendMessage(pushSenderHandler.obtainMessage(AppVisorPushSetting.msgDoFeedBack , pushIDStr));
    			}
    		}
    		AppVisorPushUtil.appVisorPushLog("Activity Start by AppVisor Push:" + pushIDStr);    		
    	}else{
    		AppVisorPushUtil.appVisorPushLog("Activity isn't Start by AppVisor Push");
    	}
    }
    
    public boolean checkIfStartByAppVisorPush(Activity activity)
    {
    	
    	boolean returnValume = false;
    	if (null == activity)
		{
			 throw new IllegalArgumentException("activity is null.");
		}
    	
    	if(activity.getIntent().getBooleanExtra( AppVisorPushSetting.KEY_APPVISOR_PUSH_INTENT, false))
    	{
    		returnValume =  true;
    	}else{
    		returnValume = false;
    	}
    	
    	return returnValume;
    }
    
    public Bundle getBundleFromAppVisorPush(Activity activity)
    {
    	if (null == activity)
		{
			 throw new IllegalArgumentException("activity is null.");
		}
    	
    	Bundle infoBundle = new Bundle();
    	Intent activityIntent = activity.getIntent();
    	infoBundle.putString(AppVisorPushSetting.KEY_PUSH_MESSAGE, activityIntent.getStringExtra(AppVisorPushSetting.KEY_PUSH_MESSAGE));
    	infoBundle.putString(AppVisorPushSetting.KEY_PUSH_TITLE, activityIntent.getStringExtra(AppVisorPushSetting.KEY_PUSH_TITLE));
    	
    	infoBundle.putString(AppVisorPushSetting.KEY_PUSH_X, activityIntent.getStringExtra(AppVisorPushSetting.KEY_PUSH_X));
    	infoBundle.putString(AppVisorPushSetting.KEY_PUSH_Y, activityIntent.getStringExtra(AppVisorPushSetting.KEY_PUSH_Y));
    	infoBundle.putString(AppVisorPushSetting.KEY_PUSH_Z, activityIntent.getStringExtra(AppVisorPushSetting.KEY_PUSH_Z));
    	infoBundle.putString(AppVisorPushSetting.KEY_PUSH_W, activityIntent.getStringExtra(AppVisorPushSetting.KEY_PUSH_W));
    	
    	return infoBundle;
    }
    
    public void addChangePushStatusListener(ChangePushStatusListener listener)
    {
    	if( null == listener)
    	{
    		throw new IllegalArgumentException("listener is null.");
    	}
    	else
    	{
    		this.changeStatusListener = listener;
    	}
    }
	
	private static HandlerThread loadThread(final String name)
    {
        final HandlerThread thread = new HandlerThread(name, android.os.Process.THREAD_PRIORITY_BACKGROUND);
        thread.start();
        return thread;
    }

	private final static Handler mainHandler  = new Handler() 
    {
    	@Override
        public void handleMessage(Message msg) 
        {    
    		try
            {
    			switch (msg.what)
    			{
    				case AppVisorPushSetting.msgSetPushStatusFailed:
    				{	
    					onChangePushStatusFailed( ( (Integer)msg.obj ) );
    					break;
    				}
    				case AppVisorPushSetting.msgSetPushStatusSucceeded:
    				{	
    					onChangePushStatusSucceeded( ( (Integer)msg.obj ) );
    					break;
    				}
    				default :
    				{
    					break;
    				}
    			}
            }
    		catch (final Exception e)
            {
    			AppVisorPushUtil.appVisorPushWaring( "Exception" , e );
            }
        }
    }; 
	
    private static void onChangePushStatusFailed(Integer resultNumber)
    {
    	if( _appvisorPushInstance.changeStatusListener != null )
    	{
    		boolean result = (resultNumber.intValue() != 0) ? true : false;
    		_appvisorPushInstance.changeStatusListener.changeStatusFailed( result );
    	}
    }
    
    private static void onChangePushStatusSucceeded(Integer resultNumber)
    {
    	if( _appvisorPushInstance.changeStatusListener != null )
    	{
    		boolean result = (resultNumber.intValue() != 0) ? true : false;
    		_appvisorPushInstance.changeStatusListener.changeStatusSucceeded( result );
    	}
    }

	static class AppVisorPushSender extends Handler
    {
        private final String appTrackingKey;
        private final Context appContext;
        
        public AppVisorPushSender(final  Context context, final String trackingKey, final Looper looper)
        {
        	super(looper);
        	
    		if (context == null)
            {
                throw new IllegalArgumentException("The context of application is required in AppVisorPushSender.");
            }
    		
            if (TextUtils.isEmpty(trackingKey))
            {
                throw new IllegalArgumentException("A valid app Tracking Key is required in AppVisorPushSender.");
            }
        	
        	this.appTrackingKey = trackingKey;
        	this.appContext = context;
        }
        
        @Override
        public void handleMessage(final Message msg)
        {
            try
            {
                super.handleMessage(msg);

                switch (msg.what)
                {
                	case AppVisorPushSetting.msgDoSendPushToken:
                	{
                		try
                        {
                        	if( AppVisorPushUtil.getAppStatus( this.appContext ) != AppVisorPushSetting.APP_STATUS_KEY_KL)
                        	{
                        		sendToServer();
                        	}
                        	else
                        	{
                        		AppVisorPushUtil.appVisorPushLog("AppStatus is KL, can't send appInfor to server.");
                        	}
                        }
                		finally
                        {
                        	
                        }
                		break;
                	}
                	case AppVisorPushSetting.msgDoSendPushTokenInService:
                	{
                		try
                        {
                        	if( AppVisorPushUtil.getAppStatus( this.appContext ) != AppVisorPushSetting.APP_STATUS_KEY_KL)
                        	{
                        		sendToServer(true);
                        	}
                        	else
                        	{
                        		AppVisorPushUtil.appVisorPushLog("AppStatus is KL, can't send appInfor to server.");
                        	}
                        }
                		finally
                        {
                        	
                        }
                		break;
                	}
                	case AppVisorPushSetting.msgDoSetPushOnOff:
                	{
                		try
                        {
	                		if( AppVisorPushUtil.getAppStatus( this.appContext ) != AppVisorPushSetting.APP_STATUS_KEY_KL)
	                    	{
	                			int statusByInt = ( (Integer)msg.obj ).intValue();
	                			boolean status  = true;
	                			if (statusByInt == 0)
	                			{
	                				status = false;
	                			}
	                			
	                    		if ( changeStatus( status ) )
	                    		{
	                    			AppVisorPushUtil.savePushStatus( this.appContext , statusByInt );
	                    			
	                    			AppVisorPush.mainHandler.obtainMessage( AppVisorPushSetting.msgSetPushStatusSucceeded, Integer.valueOf(statusByInt) ).sendToTarget();
	                    			
	                    		}else{
	                    			int nowStatus = AppVisorPushUtil.getPushStatus(this.appContext);
	                    			Integer statusInteger = Integer.valueOf( nowStatus );
	                    			
	                    			AppVisorPush.mainHandler.obtainMessage( AppVisorPushSetting.msgSetPushStatusFailed, statusInteger ).sendToTarget();
	                    		}
	                    	}
	                    	else
	                    	{
	                    		AppVisorPushUtil.appVisorPushLog("AppStatus is KL, can't send feedback push_task_id to server.");
	                    		
	                    		int nowStatus = AppVisorPushUtil.getPushStatus(this.appContext);
	                			Integer statusInteger = Integer.valueOf( nowStatus );
	                			
	                			AppVisorPush.mainHandler.obtainMessage( AppVisorPushSetting.msgSetPushStatusFailed, statusInteger ).sendToTarget();
	                    	}
                        }
                		finally
                        {
                        	
                        }
                		break;
                	}
                	case AppVisorPushSetting.msgDoFeedBack:
                	{
                		String pushIdStr = (String)msg.obj;
                		try
                        {
                        	if( AppVisorPushUtil.getAppStatus( this.appContext ) != AppVisorPushSetting.APP_STATUS_KEY_KL)
                        	{
                        		if ( feedbackByPushID( pushIdStr ) )
                        		{
                        			AppVisorPushUtil.appVisorPushLog("send push feedback successed is, refresh last push task id.");
                        			
                        			int newPushID = 0; 
                        			try
                        			{
                        				newPushID = Integer.parseInt(pushIdStr);
                        			}
                        			catch(NumberFormatException e)
                        			{
                        				AppVisorPushUtil.appVisorPushWaring( "NumberFormatException" , e );
                        				newPushID = 0;
                        			}
                        			
                        			AppVisorPushUtil.saveLastPushTrackingID( this.appContext , newPushID );
                        		}
                        	}
                        	else
                        	{
                        		AppVisorPushUtil.appVisorPushLog("AppStatus is KL, can't send feedback push_task_id to server.");
                        	}
                        }
                		finally
                        {
                        	
                        }
                		break;
                	}
                	case AppVisorPushSetting.msgDoSynchronizeUserProperties:
                	{
                		try
                		{
                			if( AppVisorPushUtil.getAppStatus( this.appContext ) != AppVisorPushSetting.APP_STATUS_KEY_KL)
                			{
                				if(synchronizeUserProperties() == true)
                				{
                					AppVisorPushUtil.saveIfUserPropertiesChanged(this.appContext, 0);
                				}
                			}else
                    		{
                    			AppVisorPushUtil.appVisorPushLog("AppStatus is KL, can't synchronize user properties.");
                    		}
                		}
                		finally
                		{
                		}
                		break;
                	}
                	default:
                	{
                		throw new RuntimeException("Unknow Message Exception.");
                	}
                }
            }
            catch (final Exception e)
            {
            	throw new RuntimeException(e);
            }
        }
        
        
        public boolean sendToServer()
        {
        	return sendToServer(false);
        }
        
        public boolean sendToServer(boolean isInService)
        {
            return AppVisorPushRegisterer.sendToServer(this.appContext, this.appTrackingKey, isInService);
        }

        public boolean feedbackByPushID( String pushIdStr )
        {
        	final DefaultHttpClient client = new DefaultHttpClient();
            final HttpPost method = new HttpPost(AppVisorPushSetting.PUSH_FEEDBACK_URL);
            
            try
            {
            	String deviceUUID		= AppVisorPushUtil.getDeviceUUID( this.appContext , this.appTrackingKey );
            	int pushId = Integer.parseInt(pushIdStr);
            	long  arrivedTime =  AppVisorPushUtil.getArrivedTimestampByPushTaskId(this.appContext , pushId);
            	
            	List<NameValuePair> postParams = new ArrayList<NameValuePair>();
            	
            	postParams.add(new BasicNameValuePair( AppVisorPushSetting.PARAM_C , "user" ) );
        		postParams.add(new BasicNameValuePair( AppVisorPushSetting.PARAM_A , "callback" ) );
            	
            	postParams.add(new BasicNameValuePair( AppVisorPushSetting.PARAM_APP_TRACKING_KEY , this.appTrackingKey ) );
        		postParams.add(new BasicNameValuePair( AppVisorPushSetting.PARAM_DEVICE_UUID , deviceUUID ) );
        		postParams.add(new BasicNameValuePair( AppVisorPushSetting.PARAM_PUSH_TRACKING_ID , pushIdStr ) );
        		postParams.add(new BasicNameValuePair( AppVisorPushSetting.PARAM_ARRIVED_TIME , String.valueOf(arrivedTime) ) );
        		
        		
        		method.setEntity(new UrlEncodedFormEntity(postParams, "UTF-8"));
                final HttpResponse response = client.execute(method);
                
                final StatusLine statusLine = response.getStatusLine();
                final int statusCode = statusLine.getStatusCode();
                
                AppVisorPushUtil.appVisorPushLog( String.format("communication completely with status code: %d", Integer.valueOf(statusCode)) );
                
                if (statusCode >= 500 && statusCode <= 599)
                {	
                	AppVisorPushUtil.appVisorPushLog( "push feed back failed. Error code:" + statusCode );
                    return false;
                }
                else
                {
            		AppVisorPushUtil.appVisorPushLog( "push feed back successed.");
                	return true;
                }
            	
            }
            catch (final UnsupportedEncodingException e)
            {
            	AppVisorPushUtil.appVisorPushWaring( "UnsupportedEncodingException" , e );
            	return false;
            }
            catch (final ClientProtocolException e)
            {
            	AppVisorPushUtil.appVisorPushWaring( "ClientProtocolException" , e );
            	return false;
            }
            catch (final IOException e)
            {
            	AppVisorPushUtil.appVisorPushWaring( "IOException" , e );
            	return false;
            }
            finally
            {
                
            }
        }
    
        public boolean changeStatus( boolean onOff )
        {
        	final DefaultHttpClient client = new DefaultHttpClient();
            final HttpPost method = new HttpPost(AppVisorPushSetting.PUSH_ONOFF_URL);
            
            try
            {
            	String deviceUUID		= AppVisorPushUtil.getDeviceUUID( this.appContext , this.appTrackingKey );
            	String onOffStr			= onOff ? "1" : "0" ; 
        	
            	List<NameValuePair> postParams = new ArrayList<NameValuePair>();
            	
            	postParams.add(new BasicNameValuePair( AppVisorPushSetting.PARAM_C , "user" ) );
        		postParams.add(new BasicNameValuePair( AppVisorPushSetting.PARAM_A , "setOn" ) );
            	
            	postParams.add(new BasicNameValuePair( AppVisorPushSetting.PARAM_APP_TRACKING_KEY , this.appTrackingKey ) );
				postParams.add(new BasicNameValuePair( AppVisorPushSetting.PARAM_DEVICE_UUID , deviceUUID ) );
    			postParams.add(new BasicNameValuePair( AppVisorPushSetting.PARAM_PUSH_ON_OFF , onOffStr ) );
    		
	    		method.setEntity(new UrlEncodedFormEntity(postParams, "UTF-8"));
	            final HttpResponse response = client.execute(method);
	            
	            final StatusLine statusLine = response.getStatusLine();
	            final int statusCode = statusLine.getStatusCode();
	            
	            AppVisorPushUtil.appVisorPushLog( String.format("communication completely with status code: %d", Integer.valueOf(statusCode)) );
	            
	            if (statusCode >= 500 && statusCode <= 599)
	            {	
	            	AppVisorPushUtil.appVisorPushLog( "change status failed. Error code:" + statusCode );
	                return false;
	            }
	            else
	            {
	            	AppVisorPushUtil.appVisorPushLog( "change status succeed.");
	            	return true;
	            }
            }
            catch (final UnsupportedEncodingException e)
            {
            	AppVisorPushUtil.appVisorPushWaring( "UnsupportedEncodingException" , e );
            	return false;
            }
            catch (final ClientProtocolException e)
            {
            	AppVisorPushUtil.appVisorPushWaring( "ClientProtocolException" , e );
            	return false;
            }
            catch (final IOException e)
            {
            	AppVisorPushUtil.appVisorPushWaring( "IOException" , e );
            	return false;
            }
            finally
            {
                
            }
        }
    
        public boolean synchronizeUserProperties()
        {
        	final DefaultHttpClient client = new DefaultHttpClient();
            final HttpPost method = new HttpPost(AppVisorPushSetting.PUSH_PROPERTY_URL);
            try
            {
            	String deviceUUID = AppVisorPushUtil.getDeviceUUID( this.appContext , this.appTrackingKey );
            	
            	List<NameValuePair> postParams = new ArrayList<NameValuePair>();
            	
            	for (int i = 1; i < 6; i++)
        	    {
        			String propertyValue = AppVisorPushUtil.getUserProperties( this.appContext ,i);
        			if ( propertyValue.equals(AppVisorPushSetting.PROPERTY_DEFAULT_VALUE) == false )
        			{
        				String propertyKey = AppVisorPushSetting.PARAM_PROPERTY + i;
            			postParams.add(new BasicNameValuePair( propertyKey , propertyValue ));
        			}
        	    }

        	    int specialProperties[] = {AppVisorPush.SpecialUserPropertyGroup1, AppVisorPush.SpecialUserPropertyGroup2};

				for (int i = 0; i < specialProperties.length; i++)
				{
					int specialProperty = specialProperties[i];
					List<String> specialPropertyList = (List<String>) AppVisorPush.sharedInstance().getUserPropertyWithGroup(this.appContext, specialProperty);
					int sizeOfList = specialPropertyList.size();

					if ( sizeOfList > 0 )
					{
						for (int j = 0; j < sizeOfList; j++)
						{
							String propertyValue = specialPropertyList.get(j);
							String propertyKey = AppVisorPushSetting.PARAM_PROPERTY + specialProperty + "[]";
							postParams.add(new BasicNameValuePair( propertyKey , propertyValue ));
						}
					}
					else
					{
						String propertyValue = "";
						String propertyKey = AppVisorPushSetting.PARAM_PROPERTY + specialProperty + "[]";
						postParams.add(new BasicNameValuePair( propertyKey , propertyValue ));
					}
				}

            	
            	postParams.add(new BasicNameValuePair( AppVisorPushSetting.PARAM_C , "user" ) );
        		postParams.add(new BasicNameValuePair( AppVisorPushSetting.PARAM_A , "property" ) );
        		postParams.add(new BasicNameValuePair( AppVisorPushSetting.PARAM_APP_TRACKING_KEY , this.appTrackingKey ) );
        		postParams.add(new BasicNameValuePair( AppVisorPushSetting.PARAM_DEVICE_UUID , deviceUUID ) );
        		
        		method.setEntity(new UrlEncodedFormEntity(postParams, "UTF-8"));
                final HttpResponse response = client.execute(method);
                
                final StatusLine statusLine = response.getStatusLine();
                final int statusCode = statusLine.getStatusCode();
                
                AppVisorPushUtil.appVisorPushLog( String.format("communication completely with status code: %d", Integer.valueOf(statusCode)) );
                
                if (statusCode >= 500 && statusCode <= 599)
                {	
                	AppVisorPushUtil.appVisorPushLog( "synchronize user properties failed. Error code:" + statusCode );
                    return false;
                }
                else
                {
                	AppVisorPushUtil.appVisorPushLog( "synchronize user properties succeed.");
                	return true;
                }
            }
            catch (final UnsupportedEncodingException e)
            {
            	AppVisorPushUtil.appVisorPushWaring( "UnsupportedEncodingException" , e );
            	return false;
            }
            catch (final ClientProtocolException e)
            {
            	AppVisorPushUtil.appVisorPushWaring( "ClientProtocolException" , e );
            	return false;
            }
            catch (final IOException e)
            {
            	AppVisorPushUtil.appVisorPushWaring( "IOException" , e );
            	return false;
            }
            finally
            {
                
            }
        }

    }

	interface MessagingInterface
	{
		boolean isRegistered();
		void checkDevice();
		String getDeviceToken();
		boolean requiresRegistration();
		void register(String senderID);
	}
/*
	static class GoogleMessagingInterface implements MessagingInterface
	{
		Context context;

		GoogleMessagingInterface (Context context)
		{
			this.context = context;
		}

		public boolean isRegistered()
		{
            return GCMRegistrar.isRegistered( this.context );
		}

		public void checkDevice()
		{
			try
			{
				GCMRegistrar.checkDevice( this.context );
			}
			catch(Exception e)
			{
				AppVisorPushUtil.appVisorPushWaring("UnsupportedOperationException", e);
				return;
			}
		}

		public String getDeviceToken()
		{
			return GCMRegistrar.getRegistrationId( this.context );
		}

		public boolean requiresRegistration()
		{
			String deviceToken = getDeviceToken();
			return null == deviceToken || deviceToken.equals("");
		}

		public void register( String senderID )
		{
			AppVisorPushUtil.appVisorPushLog("start resgister device token");
			GCMRegistrar.register( this.context, senderID );
		}
	}
	*/

	static class FirebaseMessagingInterface implements MessagingInterface
	{
		Context context;

		FirebaseMessagingInterface (Context context)
		{
			this.context = context;
		}

		public boolean isRegistered()
		{
			return FirebaseInstanceId.getInstance().getToken() != null;
		}

		public void checkDevice()
		{
			return;
		}

		public String getDeviceToken()
		{
			return FirebaseInstanceId.getInstance().getToken();
		}

		public boolean requiresRegistration()
		{
			return getDeviceToken() == null || getDeviceToken().equals("");
		}

		public void register( String senderID )
		{
			AppVisorPushUtil.saveRegistrationState(this.context, AppVisorPushSetting.REGISTRATION_STATE_ACTIVE);
		}

	}
}
