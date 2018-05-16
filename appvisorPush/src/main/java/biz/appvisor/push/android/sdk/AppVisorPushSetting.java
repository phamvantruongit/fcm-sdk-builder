package biz.appvisor.push.android.sdk;

import android.app.NotificationManager;

public class AppVisorPushSetting
{
	public static final int thisApiLevel = AppVisorPushUtil.getApiLevel();
	public static boolean allowLogOutput = false; 
	public final static String APPVISOR_PUSH_LOG_TAG		       = "Appvisor_Push_SDK"; 
	public final static String APPVISOR_PUSH_SDK_VERSION           = "3.0.0";
	//asahi "a.1.0.0";
	public final static String APPVISOR_PUSH_SDK_CUSTOMIZE_VERSION = "";
	public final static String OS_TYPE						       = "Android";
	
	//API URL
	public final static String LIVE_BASE_URL	 = "http://p.app-visor.com/";	
	public final static String DEV_BASE_URL		 = "http://dp.app-visor.com/";
	public final static String REAL_URL          = DEV_BASE_URL;
    //public final static String REAL_URL          = LIVE_BASE_URL;
    public final static String PUSH_INFOR_URL 	 = REAL_URL;
    public final static String PUSH_FEEDBACK_URL = REAL_URL;
    public final static String PUSH_ONOFF_URL 	 = REAL_URL;
    public final static String PUSH_PROPERTY_URL = REAL_URL;
    public final static String PUSH_ARRIVED_URL	 = REAL_URL;
	
	//SHARED_PREFERENCES
	public final static String SHARED_PREFERENCES_KEY 					 = "appvisor_push";
	public final static String SHARED_PREFERENCES_PARA_DEVICE_ID		 = "device_id";
	public final static String SHARED_PREFERENCES_PARA_PUSH_TOKEN		 = "push_token";
	public final static String SHARED_PREFERENCES_PARA_APP_STATUS		 = "app_status";
	public final static String SHARED_PREFERENCES_PARA_SENDER_ID		 = "push_sender_id";
	public final static String SHARED_PREFERENCES_PARA_CLASS_NAME		 = "callback_class";
	public final static String SHARED_PREFERENCES_PARA_SERVICE_NAME		 = "callback_service";
    public final static String SHARED_PREFERENCES_PARA_JOB_SERVICE_NAME		 = "callback_job_service";
	public final static String SHARED_PREFERENCES_PARA_APP_NAME			 = "app_name";
	public final static String SHARED_PREFERENCES_PARA_ICON_ID			 = "push_icon_id";
	public final static String SHARED_PREFERENCES_PARA_TRACKING_KEY		 = "tracking_key";
	public final static String SHARED_PREFERENCES_PARA_PUSH_TRACKING_ID	 = "push_tracking_id";
	public final static String SHARED_PREFERENCES_PARA_PUSH_STATUS		 = "push_status";
	public final static String SHARED_PREFERENCES_PARA_PROPERTY			 = "appvisorproperty";
	public final static String SHARED_PREFERENCES_PARA_STATUSBAR_ICON_ID = "statusbar_icon_id";
	public final static String SHARED_PREFERENCES_PARA_ARRIVED_TIMES	 = "arrived_times";
    public final static String SHARED_PREFERENCES_PARA_REGISTRATION_STATE	 = "registration_state";

	public final static String PROPERTY_DELIMITER      = "{[s]}";
	public final static String PROPERTY_DELIMITER_GREP = "\\{\\[s\\]\\}";
	public final static String PROPERTY_DEFAULT_VALUE  = "appvisorpush_default_value";
	
	//Multi threaded Message define
    public static final int msgDoSendPushToken  		   = 1;
    public static final int msgDoSetPushOnOff		       = 2;    
    public static final int msgDoFeedBack 				   = 3;
    public static final int msgSetPushStatusSucceeded 	   = 4;
    public static final int msgSetPushStatusFailed 		   = 5;
    public static final int msgDoSynchronizeUserProperties = 6;
    public static final int msgDoSendPushTokenInService    = 7;

    
    //Application status key
    public static final int APP_STATUS_KEY_OK			= 1;
    public static final int APP_STATUS_KEY_NG			= 2;
    public static final int APP_STATUS_KEY_KL			= 3;
    
    //Push Message key
    public final static String KEY_APPVISOR_PUSH_INTENT    = "appvisor_push";
    public final static String KEY_SILENCE_NOTIFICATION    = "appvisor_sn";
    public final static String KEY_BACKGROUND_NOTIFICATION = "appvisor_bn";
    public final static String KEY_PUSH_MESSAGE    		   = "message";
    public final static String KEY_PUSH_TITLE    		   = "title";
    public final static String KEY_PUSH_VIBRATION		   = "vibration";
    public final static String KEY_PUSH_TRACKING_ID		   = "c";
    public final static String KEY_PUSH_X				   = "x";
    public final static String KEY_PUSH_Y				   = "y";
    public final static String KEY_PUSH_Z				   = "z";
    public final static String KEY_PUSH_W				   = "w";
    public final static String KEY_PUSH_URL    			   = "u";
    public final static String KEY_PUSH_CONTENT_FLAG	   = "content_flg";
    public final static String KEY_PUSH_CONTENT_URL	       = "content_url";
    
    //POST PARAMETER KEY
    public final static String PARAM_C   				= "c";
    public final static String PARAM_A   				= "a";
    public final static String PARAM_APP_TRACKING_KEY   = "ak";
    public final static String PARAM_DEVICE_UUID        = "du";
    public final static String PARAM_DEVICE_TOKEN       = "tk";
    public final static String PARAM_APP_VERSION        = "av";
    public final static String PARAM_LOCALE_COUNTRY     = "lc";
    public final static String PARAM_LOCALE_LANGUAGE    = "ll";
    public final static String PARAM_SDK_VERSION    	= "tv";
    public final static String PARAM_OS_TYPE        	= "os";
    public final static String PARAM_DEVICE_TYPE        = "dt";
    public final static String PARAM_DEVICE_MODEL       = "dm";
    public final static String PARAM_OS_VERSION         = "ov";
    public final static String PARAM_CONNECTION_TYPE    = "ct";
    public final static String PARAM_ARRIVED_TIME    	= "at";
    
    public final static String PARAM_PUSH_TRACKING_ID   = "pt";
    public final static String PARAM_PUSH_ON_OFF    	= "on";
    
    public final static String PARAM_APP_STATUS    		= "st";
    public final static String PARAM_PUSH_STATUS		= "ps";
    public final static String PARAM_PROPERTY			= "s";    
    public final static String PARAM_PROPERTIES			= "pp"; 
    
    public final static String PARAM_IN_SERVICE   		= "wk";
    
    // Rich Push Const
    public final static String RICH_PUSH_IMAGE			= "1";
    public final static String RICH_PUSH_WEB			= "2";

    // Default notification channel const
    public final static String DEFAULT_NOTIFICATION_CHANNEL_ID = "appvisor_default_notification_channel";
    public final static int DEFAULT_NOTIFICATION_CHANNEL_IMPORTANCE = NotificationManager.IMPORTANCE_HIGH;
    public final static long[] DEFAULT_NOTIFICATION_CHANNEL_VIBRATION_PATTERN = new long[]{100, 200, 300, 400, 500, 400, 300, 200, 500};

    public final static int REGISTRATION_STATE_ACTIVE	 = 1;
    public final static int REGISTRATION_STATE_INACTIVE	 = 0;
}
