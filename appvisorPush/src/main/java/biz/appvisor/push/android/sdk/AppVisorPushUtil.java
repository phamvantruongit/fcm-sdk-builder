package biz.appvisor.push.android.sdk;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.Point;
import android.net.ConnectivityManager;
import android.net.NetworkInfo.State;
import android.os.Build;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Display;
import android.view.WindowManager;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Locale;
import java.util.UUID;

public class AppVisorPushUtil 
{	
	//sdk verison 
    static int getApiLevel()
    {
        try
        {
            final Class<?> buildClass = Build.VERSION.class;
            final String sdkString = (String) buildClass.getField("SDK").get(null);
            return Integer.parseInt(sdkString);
        }
        catch (final Exception e)
        {
            try
            {
                final Class<?> buildClass = Build.VERSION.class;
                return buildClass.getField("SDK_INT").getInt(null);
            }
            catch (final Exception ignore) 
            {
            	AppVisorPushUtil.appVisorPushWaring( "get ApiLevel Exception" , e );
            }
        }
 
        return 3;
    }
    
    //normal log output
    static void appVisorPushLog(String logStr)
    {
    	if (AppVisorPushSetting.allowLogOutput)
        {
    		Log.d(AppVisorPushSetting.APPVISOR_PUSH_LOG_TAG, logStr);
        }
    }
    
    //warning log output
    static void appVisorPushWaring(String msg, Throwable tr)
    {
    	Log.w(AppVisorPushSetting.APPVISOR_PUSH_LOG_TAG, msg, tr);        
    }
    
    //model
    static String getModel()
    {
        String a = "";
        try
        {
            a = Build.MODEL;
        }
        catch (final Exception e)
        {
        	AppVisorPushUtil.appVisorPushWaring( "get Model Exception" , e );
        }
        return a;
    }
    
    //memory
    static String getMemmory()
    {
        double a = 0;
        String z = "";
        Runtime b = Runtime.getRuntime();
        try
        {
            a  = (int)( (b.totalMemory() - b.freeMemory())/1024);
            if((a /1024)>1)
            {
                z = String.valueOf((a / 1024)) + "MB";
            }
            else
            {
                z = String.valueOf(a) + "KB";
            }
        }
        catch (final Exception e)
        {
        	AppVisorPushUtil.appVisorPushWaring( "get Memmory Exception" , e );
        }
        
        return z;
    }
    
    //total memory
    static String getTotalMemmory()
    {
        double a = 0;
        String z = "";
        Runtime b = Runtime.getRuntime();
        try
        {
            a  = (int)( (b.totalMemory())/1024);
            if((a /1024)>1)
            {
                z = String.valueOf((a / 1024)) + "MB";
            }
            else
            {
                z = String.valueOf(a) + "KB";
            }
        }catch (final Exception e)
        {
        	AppVisorPushUtil.appVisorPushWaring( "get Total Memmory Exception" , e );
        }
        
        return z;
    }
    
    //os Version
    static String getOSVersion()
    {
        String a = "";
        try
        {
            a = Build.VERSION.RELEASE;
        }
        catch (final Exception e)
        {
        	AppVisorPushUtil.appVisorPushWaring( "get OS Version Exception" , e );
        }
        return a;
    }
    
    //country
    static String getCountry()
    {
        String a = "";
        try
        {
            a = Locale.getDefault().getDisplayCountry(Locale.US);
        }
        catch (final Exception e)
        {
        	AppVisorPushUtil.appVisorPushWaring( "get Country Exception" , e );
        }
        return a;
    }
    
    //language
    static String getLanguage()
    {
        String a = "";
        try
        {
            a = Locale.getDefault().getLanguage();
        }
        catch (final Exception e)
        {
        	AppVisorPushUtil.appVisorPushWaring( "get Language Exception" , e );
        }
        return a;
    }
    
    
    //connection info
    static String getConnection(Context co)
    {
        String a = "";
        ConnectivityManager b = (ConnectivityManager) co.getSystemService(Context.CONNECTIVITY_SERVICE);
        PackageManager pm = co.getPackageManager();
        boolean p = false;
        try
        {
            //check permission
            try 
            {
                PackageInfo e = pm.getPackageInfo(co.getPackageName(), PackageManager.GET_PERMISSIONS);
                String[] f = e.requestedPermissions;
                if (f != null) 
                {
                    for (int i = 0; i < f.length; i++) 
                    {
                        if(f[i].toString().equals("android.permission.ACCESS_NETWORK_STATE"))
                        {
                            p = true;
                            
                            appVisorPushLog("connection true");
                            
                            break;
                        }
                    }
                }
            } 
            catch (NameNotFoundException e) 
            {
            	AppVisorPushUtil.appVisorPushWaring( "NameNotFoundException" , e );
                p = false;
            }
            
            if(p)
            {
            	State c = State.DISCONNECTED;
            	if( b.getNetworkInfo(ConnectivityManager.TYPE_MOBILE) != null )
            	{
            		c = b.getNetworkInfo(ConnectivityManager.TYPE_MOBILE).getState();
            	}
                
            	State d = State.DISCONNECTED;
            	if( b.getNetworkInfo(ConnectivityManager.TYPE_WIFI) != null )
            	{
            		d = b.getNetworkInfo(ConnectivityManager.TYPE_WIFI).getState();
            	}
            	
                if ((d != State.CONNECTED) && (c != State.CONNECTED)) 
                {
                    a = "-";
                }
                else if((c == State.CONNECTED))
                {
                    a = "3g";
                }
                else
                {
                    a = "wifi";
                }
            }
            else
            {
                a = "wifi";
            }
        }
        catch (final Exception e)
        {
        	AppVisorPushUtil.appVisorPushWaring( "get Connection type Exception" , e );
        }   
        return a;
    }
    
    //app version
    static String getAppVersion(Context a)
    {
        PackageManager pm = a.getPackageManager();
        String b = "";
        try 
        {
               PackageInfo packageInfo = pm.getPackageInfo(a.getPackageName(), PackageManager.GET_ACTIVITIES);
               b = packageInfo.versionName;
        }
        catch (final Exception e)
        {
        	AppVisorPushUtil.appVisorPushWaring( "get AppVersion Exception" , e );
               b = "";
        }
        
        return b;
    }
    
    //get device's display size
    static String getDisplaySize(Context c)
    {
    	String a = ""; 
    	try
    	{
        	WindowManager w = (WindowManager) c.getSystemService(Context.WINDOW_SERVICE);
        	Display d = w.getDefaultDisplay();
        	a =  String.valueOf(d.getWidth());
        	a += "*";
        	a += String.valueOf(d.getHeight());
        	
        	appVisorPushLog("size : "+a);
    	}
    	catch(final Exception e)
    	{
    		AppVisorPushUtil.appVisorPushWaring( "get DisplaySize" , e );
    		a = "";
    	}
    	return a;
    }
    
    //get device uuid for this app on appvisor
    static String getDeviceUUID(Context context , String apiKey)
    {
    	String uuidStr = "";
    	SharedPreferences prefer = context.getSharedPreferences(AppVisorPushSetting.SHARED_PREFERENCES_KEY ,Context.MODE_PRIVATE); 
    	uuidStr = prefer.getString(AppVisorPushSetting.SHARED_PREFERENCES_PARA_DEVICE_ID, "");
    	if(uuidStr.equals(""))
    	{
    		//check if uuid for old version exsist
    		String oldVersionUUID = getOldVersionUUID( context , apiKey );
    		if( null == oldVersionUUID || oldVersionUUID.length() == 0 )
    		{
    			uuidStr = UUID.randomUUID().toString();
    		}else{
    			uuidStr = oldVersionUUID;
    		}
    		uuidStr = md5(uuidStr);
    		saveDeviceUUID(context, uuidStr);
    	}
    	
    	return uuidStr;
    }

    //save uuid
    static void saveDeviceUUID(Context context, String uuidStr)
    {
    	SharedPreferences prefer = context.getSharedPreferences(AppVisorPushSetting.SHARED_PREFERENCES_KEY ,Context.MODE_PRIVATE);
    	SharedPreferences.Editor editor = prefer.edit();
    	editor.putString( AppVisorPushSetting.SHARED_PREFERENCES_PARA_DEVICE_ID , uuidStr);
    	editor.commit();
    }
    
    //load push token (google registrationId)
    static String getPushToken(Context context)
    {
    	String tokenStr = "";
    	SharedPreferences prefer = context.getSharedPreferences(AppVisorPushSetting.SHARED_PREFERENCES_KEY ,Context.MODE_PRIVATE); 
    	tokenStr = prefer.getString(AppVisorPushSetting.SHARED_PREFERENCES_PARA_PUSH_TOKEN, "");
    	
    	return tokenStr;
    }
    
    //save push token (google registrationId)
    static void savePushToken(Context context, String tokenStr)
    {
    	SharedPreferences prefer = context.getSharedPreferences(AppVisorPushSetting.SHARED_PREFERENCES_KEY ,Context.MODE_PRIVATE);
    	SharedPreferences.Editor editor = prefer.edit();
    	editor.putString( AppVisorPushSetting.SHARED_PREFERENCES_PARA_PUSH_TOKEN , tokenStr);
    	editor.commit();
    }
    
    //load appvisor status(kl,ok,ng)
    static int getAppStatus(Context context)
    {
    	int appStatus = AppVisorPushSetting.APP_STATUS_KEY_OK;
    	SharedPreferences prefer = context.getSharedPreferences(AppVisorPushSetting.SHARED_PREFERENCES_KEY ,Context.MODE_PRIVATE); 
    	appStatus = prefer.getInt(AppVisorPushSetting.SHARED_PREFERENCES_PARA_APP_STATUS, AppVisorPushSetting.APP_STATUS_KEY_OK);
    	
    	return appStatus;
    }
    
    //save appvisor status(kl,ok,ng)
    static void saveAppStatus(Context context, int appStatus)
    {
    	SharedPreferences prefer = context.getSharedPreferences(AppVisorPushSetting.SHARED_PREFERENCES_KEY ,Context.MODE_PRIVATE);
    	SharedPreferences.Editor editor = prefer.edit();
    	editor.putInt( AppVisorPushSetting.SHARED_PREFERENCES_PARA_APP_STATUS , appStatus);
    	editor.commit();
    }
    
    //load PushSenderID for this app on google api console
    static String getPushSenderID(Context context)
    {
    	String senderID = "";
    	SharedPreferences prefer = context.getSharedPreferences(AppVisorPushSetting.SHARED_PREFERENCES_KEY ,Context.MODE_PRIVATE); 
    	senderID = prefer.getString(AppVisorPushSetting.SHARED_PREFERENCES_PARA_SENDER_ID, "");
    	
    	return senderID;
    }
    
    //save PushSenderID for this app on google api console
    static void savePushSenderID(Context context, String sender_id)
    {
    	SharedPreferences prefer = context.getSharedPreferences(AppVisorPushSetting.SHARED_PREFERENCES_KEY ,Context.MODE_PRIVATE);
    	SharedPreferences.Editor editor = prefer.edit();
    	editor.putString( AppVisorPushSetting.SHARED_PREFERENCES_PARA_SENDER_ID , sender_id);
    	editor.commit();
    }
    
    //load Push Callback Class Name
    static String getPushCallbackClassName(Context context)
    {
    	String className = "";
    	SharedPreferences prefer = context.getSharedPreferences(AppVisorPushSetting.SHARED_PREFERENCES_KEY ,Context.MODE_PRIVATE); 
    	className = prefer.getString(AppVisorPushSetting.SHARED_PREFERENCES_PARA_CLASS_NAME, "");
    	
    	return className;
    }
    
    //save Push Callback Class Name
    static void savePushCallbackClassName(Context context, String className)
    {
    	SharedPreferences prefer = context.getSharedPreferences(AppVisorPushSetting.SHARED_PREFERENCES_KEY ,Context.MODE_PRIVATE);
    	SharedPreferences.Editor editor = prefer.edit();
    	editor.putString( AppVisorPushSetting.SHARED_PREFERENCES_PARA_CLASS_NAME , className);
    	editor.commit();
    }

    //load Push Callback Service Name
    static String getPushCallbackJobServiceName(Context context)
    {
        String serviceName = "";
        SharedPreferences prefer = context.getSharedPreferences(AppVisorPushSetting.SHARED_PREFERENCES_KEY ,Context.MODE_PRIVATE);
        serviceName = prefer.getString(AppVisorPushSetting.SHARED_PREFERENCES_PARA_JOB_SERVICE_NAME, "");

        return serviceName;
    }

    //save Push Callback Service Name
    static void savePushCallbackJobServiceName(Context context,String serviceName )
    {
        SharedPreferences prefer = context.getSharedPreferences(AppVisorPushSetting.SHARED_PREFERENCES_KEY ,Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = prefer.edit();
        editor.putString( AppVisorPushSetting.SHARED_PREFERENCES_PARA_JOB_SERVICE_NAME, serviceName);
        editor.commit();
    }

    //load Push Callback Service Name
    static String getPushCallbackServiceName(Context context)
    {
    	String serviceName = "";
    	SharedPreferences prefer = context.getSharedPreferences(AppVisorPushSetting.SHARED_PREFERENCES_KEY ,Context.MODE_PRIVATE); 
    	serviceName = prefer.getString(AppVisorPushSetting.SHARED_PREFERENCES_PARA_SERVICE_NAME, "");
    	
    	return serviceName;
    }
    
    //save Push Callback Service Name
    static void savePushCallbackServiceName(Context context,String serviceName )
    {
    	SharedPreferences prefer = context.getSharedPreferences(AppVisorPushSetting.SHARED_PREFERENCES_KEY ,Context.MODE_PRIVATE);
    	SharedPreferences.Editor editor = prefer.edit();
    	editor.putString( AppVisorPushSetting.SHARED_PREFERENCES_PARA_SERVICE_NAME , serviceName);
    	editor.commit();
    }
    
    //load App Name
    static String getPushAppName(Context context)
    {
    	String appName = "";
    	SharedPreferences prefer = context.getSharedPreferences(AppVisorPushSetting.SHARED_PREFERENCES_KEY ,Context.MODE_PRIVATE); 
    	appName = prefer.getString(AppVisorPushSetting.SHARED_PREFERENCES_PARA_APP_NAME, "");
    	
    	return appName;
    }
    
    //save App Name
    static void savePushAppName(Context context, String appName)
    {
    	SharedPreferences prefer = context.getSharedPreferences(AppVisorPushSetting.SHARED_PREFERENCES_KEY ,Context.MODE_PRIVATE);
    	SharedPreferences.Editor editor = prefer.edit();
    	editor.putString( AppVisorPushSetting.SHARED_PREFERENCES_PARA_APP_NAME , appName);
    	editor.commit();
    }
    
    //load getPushIconID id 
    static int getPushIconID(Context context)
    {
    	int pushIconID = 0;
    	SharedPreferences prefer = context.getSharedPreferences(AppVisorPushSetting.SHARED_PREFERENCES_KEY ,Context.MODE_PRIVATE); 
    	pushIconID = prefer.getInt(AppVisorPushSetting.SHARED_PREFERENCES_PARA_ICON_ID, 0);
    	
    	return pushIconID;
    }
    
    //save Push Icon id
    static void savePushIconID(Context context, int pushIconID)
    {
    	SharedPreferences prefer = context.getSharedPreferences(AppVisorPushSetting.SHARED_PREFERENCES_KEY ,Context.MODE_PRIVATE);
    	SharedPreferences.Editor editor = prefer.edit();
    	editor.putInt( AppVisorPushSetting.SHARED_PREFERENCES_PARA_ICON_ID , pushIconID);
    	editor.commit();
    }
    
    //load StatusBarIcon id
    static int getStatusBarIconID(Context context)
    {
    	int statusbarIconID = 0;
    	SharedPreferences prefer = context.getSharedPreferences(AppVisorPushSetting.SHARED_PREFERENCES_KEY ,Context.MODE_PRIVATE); 
    	statusbarIconID = prefer.getInt(AppVisorPushSetting.SHARED_PREFERENCES_PARA_STATUSBAR_ICON_ID, 0);
    	
    	return statusbarIconID;
    }
    
    //save StatusBarIcon id
    static void saveStatusBarIconID(Context context, int statusbarIconID)
    {
    	SharedPreferences prefer = context.getSharedPreferences(AppVisorPushSetting.SHARED_PREFERENCES_KEY ,Context.MODE_PRIVATE);
    	SharedPreferences.Editor editor = prefer.edit();
    	editor.putInt( AppVisorPushSetting.SHARED_PREFERENCES_PARA_STATUSBAR_ICON_ID , statusbarIconID);
    	editor.commit();
    }
    
    //load last push task tracking id
    static int getLastPushTrackingID(Context context)
    {
    	int pushID = 0;
    	SharedPreferences prefer = context.getSharedPreferences(AppVisorPushSetting.SHARED_PREFERENCES_KEY ,Context.MODE_PRIVATE); 
    	pushID = prefer.getInt(AppVisorPushSetting.SHARED_PREFERENCES_PARA_PUSH_TRACKING_ID, 0);
    	
    	return pushID;
    }
    
    static void clearArrivedTimeRecord(Context context)
    {
    	JSONArray records = new JSONArray();
    	saveArrivedTimeRecords(context,records);
    }
    
    static long getArrivedTimestampByPushTaskId(Context context,int pushTaskID)
    {
    	JSONArray records = getArrivedTimeRecords(context);
    	for(int i = 0; i < records.length(); i++)
    	{
    		JSONObject record;
			try {
				record = records.getJSONObject(i);
			} catch (JSONException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
				return 0;
			}
			
    		int id;
    		
			try {
				id = record.getInt("id");
			} catch (JSONException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
				return 0;
			}
			
    		if(id == pushTaskID)
    		{
    			long timeStamp;
				try {
					timeStamp = record.getLong("time");
				} catch (JSONException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
					return 0;
				}
    			return timeStamp;
    		}
    	}
    	
    	return 0;
    }
    
    //add new Arrived Time Record
    static void addArrivedTimeRecord(Context context, long arrivedTimeStamp, int pushTaskID)
    {
    	JSONArray records = getArrivedTimeRecords(context);
    	JSONObject newRecord = new JSONObject();
    	
    	try {
			newRecord.put("id", pushTaskID);
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return;
		}
    	
    	try {
			newRecord.put("time", arrivedTimeStamp);
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return;
		}
    	
    	boolean oldRecordExisted = false;  
    	
    	//update old record if same push id already existed 
    	for(int i = 0; i < records.length(); i++)
    	{
    		JSONObject record;
			try {
				record = records.getJSONObject(i);
			} catch (JSONException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
				return;
			}
			
    		int id;
    		
			try {
				id = record.getInt("id");
			} catch (JSONException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
				return ;
			}
			
    		if(id == pushTaskID)
    		{
    			try {
					records.put(i, newRecord);
					oldRecordExisted = true;
					AppVisorPushUtil.appVisorPushLog("update old record for same push id is already existed ");
					break;
				} catch (JSONException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
					return;
				}
    			
    		}
    	}
    	
    	//if no old record existed, add new record 
    	if(oldRecordExisted == false)
    	{
    		records.put(newRecord);
    	}
    	saveArrivedTimeRecords(context ,records);
    }
    
    //save ArrivedTimeRecords
    static protected void saveArrivedTimeRecords(Context context, JSONArray records)
    {
    	String recordsByStr = records.toString();
    	
    	SharedPreferences prefer = context.getSharedPreferences(AppVisorPushSetting.SHARED_PREFERENCES_KEY ,Context.MODE_PRIVATE);
    	SharedPreferences.Editor editor = prefer.edit();
    	editor.putString( AppVisorPushSetting.SHARED_PREFERENCES_PARA_ARRIVED_TIMES , recordsByStr);
    	editor.commit();
    }
    
    //load Arrived Time Records
    static JSONArray getArrivedTimeRecords(Context context)
    {
    	JSONArray records = new JSONArray();
    	SharedPreferences prefer =  context.getSharedPreferences(AppVisorPushSetting.SHARED_PREFERENCES_KEY ,Context.MODE_PRIVATE);
    	String jsonStr = prefer.getString(AppVisorPushSetting.SHARED_PREFERENCES_PARA_ARRIVED_TIMES ,"");
    	if(jsonStr.equals("") != true)
    	{
    		try {
				records = new JSONArray(jsonStr);
			} catch (JSONException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
    	}
    	
    	return records;
    }
    
    //save last push task tracking id
    static void saveLastPushTrackingID(Context context, int pushID)
    {
    	SharedPreferences prefer = context.getSharedPreferences(AppVisorPushSetting.SHARED_PREFERENCES_KEY ,Context.MODE_PRIVATE);
    	SharedPreferences.Editor editor = prefer.edit();
    	editor.putInt( AppVisorPushSetting.SHARED_PREFERENCES_PARA_PUSH_TRACKING_ID , pushID);
    	editor.commit();
    }
    
    //load app tracking key on appvisor
    static String getAppTrackingKey(Context context)
    {
    	String trackingKey = "";
    	SharedPreferences prefer = context.getSharedPreferences(AppVisorPushSetting.SHARED_PREFERENCES_KEY ,Context.MODE_PRIVATE); 
    	trackingKey = prefer.getString(AppVisorPushSetting.SHARED_PREFERENCES_PARA_TRACKING_KEY, "");
    	
    	return trackingKey;
    }
    
    //save app tracking key on appvisor
    static void saveAppTrackingKey(Context context, String trackingKey)
    {
    	SharedPreferences prefer = context.getSharedPreferences(AppVisorPushSetting.SHARED_PREFERENCES_KEY ,Context.MODE_PRIVATE);
    	SharedPreferences.Editor editor = prefer.edit();
    	editor.putString( AppVisorPushSetting.SHARED_PREFERENCES_PARA_TRACKING_KEY , trackingKey);
    	editor.commit();
    }
    
    //load push on/off setting on appvisor
    static int getPushStatus(Context context)
    {
    	int onOff = 1;
    	SharedPreferences prefer = context.getSharedPreferences(AppVisorPushSetting.SHARED_PREFERENCES_KEY ,Context.MODE_PRIVATE); 
    	onOff = prefer.getInt(AppVisorPushSetting.SHARED_PREFERENCES_PARA_PUSH_STATUS, 1);
    	
    	return onOff;
    }
    
    //save push on/off setting on appvisor
    static void savePushStatus(Context context, int onOff)
    {
    	SharedPreferences prefer = context.getSharedPreferences(AppVisorPushSetting.SHARED_PREFERENCES_KEY ,Context.MODE_PRIVATE);
    	SharedPreferences.Editor editor = prefer.edit();
    	editor.putInt( AppVisorPushSetting.SHARED_PREFERENCES_PARA_PUSH_STATUS , onOff);
    	editor.commit();
    }
    
    static String getUserProperties(Context context,int propertyGroup)
    {	
    	if (null == context)
    	{
    		return null;
    	}
    	
    	String propertyValue = "";
    	
    	SharedPreferences prefer = context.getSharedPreferences(AppVisorPushSetting.SHARED_PREFERENCES_KEY ,Context.MODE_PRIVATE);
    	String keyStr = AppVisorPushSetting.SHARED_PREFERENCES_PARA_PROPERTY + propertyGroup;
    	propertyValue = prefer.getString(keyStr, AppVisorPushSetting.PROPERTY_DEFAULT_VALUE);
    	
    	return propertyValue;
    }
    
    static boolean saveUserProperties(Context context, int propertyGroup, String propertyValue)
    {
    	SharedPreferences prefer = context.getSharedPreferences(AppVisorPushSetting.SHARED_PREFERENCES_KEY ,Context.MODE_PRIVATE);
    	
    	String keyStr = AppVisorPushSetting.SHARED_PREFERENCES_PARA_PROPERTY + propertyGroup;
    	SharedPreferences.Editor editor = prefer.edit();
    	editor.putString(keyStr, propertyValue);
    	
    	return editor.commit();
    }
    
    static int getIfUserPropertiesChanged(Context context)
    {	
    	int changed = 0;
    	
    	SharedPreferences prefer = context.getSharedPreferences(AppVisorPushSetting.SHARED_PREFERENCES_KEY ,Context.MODE_PRIVATE);
    	String keyStr = AppVisorPushSetting.SHARED_PREFERENCES_PARA_PROPERTY;
    	changed = prefer.getInt(keyStr, 0);
    	
    	return changed;
    }
    
    static boolean saveIfUserPropertiesChanged(Context context, int changed)
    {
    	SharedPreferences prefer = context.getSharedPreferences(AppVisorPushSetting.SHARED_PREFERENCES_KEY ,Context.MODE_PRIVATE);
    	
    	String keyStr = AppVisorPushSetting.SHARED_PREFERENCES_PARA_PROPERTY;
    	SharedPreferences.Editor editor = prefer.edit();
    	editor.putInt(keyStr, changed);
    	
    	return editor.commit();
    }
    
    //md5 encoding 
    static String md5(String s) 
    {
        try 
        {
            // Create MD5 Hash
            MessageDigest digest = MessageDigest.getInstance("MD5");
            digest.update(s.getBytes());
            byte messageDigest[] = digest.digest();

            // Create Hex String
            StringBuffer hexString = new StringBuffer();
            for (int i=0; i<messageDigest.length; i++)
                hexString.append(Integer.toHexString(0xFF & messageDigest[i]));
            return hexString.toString();

        }
        catch (NoSuchAlgorithmException e)
        {
            e.printStackTrace();
        }
        return "";
    }

    //Get UUID from Old Sdk DataBase if exist.
    static String getOldVersionUUID(Context context , String apiKey)
    {
    	final String DBFileName = "biz.apptracker.android.%s.sqlite";

    	SQLiteDatabase trackingDB = null;
    	String returnValue = null;

    	try
        {
    		trackingDB = new OldPersistenceHelper(context, String.format(DBFileName, apiKey)).getWritableDatabase();

    		Cursor result = null;

	        try
	        {
	        	result = trackingDB.rawQuery(String.format("SELECT device_id FROM apptracker WHERE api_key = '%s'", apiKey), null);

	            if (result == null || result.getCount() == 0)
	            {
	            	returnValue = "";
	            }
	            else
	            {
	                if (result.moveToNext())
	                {
	                	returnValue = result.getString(result.getColumnIndex("device_id"));
	                }
	            }
	        }
	        catch (final Exception e)
	        {
	        	appVisorPushLog("old version database is not exist.");
	        	returnValue = "";
	        }
	        finally
	        {
	        	if (result != null)
	        	{
	        		result.close();
	        	}
	        }
        }
    	catch (final Exception e)
    	{
        	returnValue = "";
        	appVisorPushLog("old version database is not exist.");
        }
    	finally
    	{
        	if(trackingDB != null)
        	{
        		trackingDB.close();
        	}
        }

    	return returnValue;
    }

    final static class OldPersistenceHelper extends SQLiteOpenHelper
    {
        public OldPersistenceHelper(final Context context, final String name)
        {
            super(context, name, null, 1);
        }

        @Override
        public void onCreate(final SQLiteDatabase db)
        {
            if (null == db)
            {
                throw new IllegalArgumentException("Can't init will null db.");
            }
        }

        @Override
        public void onOpen(final SQLiteDatabase db)
        {
            super.onOpen(db);

            appVisorPushLog("SQLite has been opened");

            if (!db.isReadOnly())
            {
                db.execSQL("PRAGMA foreign_keys = ON;");
            }
        }

        @Override
        public void onUpgrade(final SQLiteDatabase db, final int oldVersion, final int newVersion)
        {
        }

    }

    final static class ByteStream
    {
    	static public byte[] toByteArray(InputStream inputStream) throws IOException
    	{
    		ByteArrayOutputStream buffer = new ByteArrayOutputStream();

    		int nRead;
    		byte[] data = new byte[1024];
    		while (-1 != (nRead = inputStream.read(data, 0, data.length))) {
    			buffer.write(data, 0, nRead);
    		}
    		buffer.flush();

    		return buffer.toByteArray();
    	}
    }

    final static class Screen
    {
    	@SuppressLint("NewApi") @SuppressWarnings("deprecation")
		static public Point getSize(Context context)
    	{
    		WindowManager windowManager = (WindowManager)context.getSystemService(Context.WINDOW_SERVICE);
    		Display display = windowManager.getDefaultDisplay();

    		Point size = new Point();

    		if (13 > Integer.valueOf(Build.VERSION.SDK_INT))
    		{
    			size.x = display.getWidth();
    			size.y = display.getHeight();
    		}
    		else {
    			display.getSize(size);
    		}
    		
    		return size;
    	}
    }
    
    final static class BitmapResizeHelper
    {
    	static public Bitmap decodeStreamByDisplayScale(InputStream inputStream, Context context)
    	{
    		byte[] bytes;
    		
    		try {
    			bytes = ByteStream.toByteArray(inputStream);
    		}
    		catch (IOException exception) {
    			return null;
    		}
    		
    		ByteArrayInputStream imageSizeGetInputStream = new ByteArrayInputStream(bytes);
    		ByteArrayInputStream imageInputStream        = new ByteArrayInputStream(bytes);

    		BitmapFactory.Options options = new BitmapFactory.Options();
            options.inJustDecodeBounds = true;
    		BitmapFactory.decodeStream(imageSizeGetInputStream, null, options);

    		options.inJustDecodeBounds = false;
    		options.inSampleSize       = calculateInSampleSize(options, Screen.getSize(context));

    		return BitmapFactory.decodeStream(imageInputStream, null, options);
    	}
    	
    	static public int calculateInSampleSize(BitmapFactory.Options options, Point size)
    	{
    		if (options.outWidth <= size.x && options.outHeight <= size.y) {
    			return 1;
    		}
    		
    		return (int) Math.ceil((float)options.outWidth / (float)size.x);
    	}
    	
    	static public Bitmap resizeToBigPictureSize(Bitmap bitmap, Context context)
    	{
    		float bigPictureWidth  = 450.0f;
    		float bigPictureHeight = 192.0f;
    		
    		DisplayMetrics metrics = new DisplayMetrics();
    		WindowManager windowManager = (WindowManager)context.getSystemService(Context.WINDOW_SERVICE);
    		windowManager.getDefaultDisplay().getMetrics(metrics);
    		float scaleDensity = metrics.scaledDensity;
    		
    		int targetWidthPixel  = (int)(bigPictureWidth * scaleDensity);
    		int targetHeightPixel = (int)(bigPictureHeight * scaleDensity);
    		
    		Matrix matrix = getMatrix(bitmap.getWidth(), bitmap.getHeight(), targetWidthPixel, targetHeightPixel, 0, false);
    		
    		Paint bitmapPaint = new Paint();
    		bitmapPaint.setFilterBitmap(true);
    		
    		Bitmap canvasBitmap = Bitmap.createBitmap(targetWidthPixel, targetHeightPixel, Bitmap.Config.ARGB_8888);
    		Canvas canvas = new Canvas(canvasBitmap);
    		canvas.drawBitmap(bitmap, matrix, bitmapPaint);

    		return canvasBitmap;
    	}
    	
    	static private Matrix getMatrix(int sourceWidth, int sourceHeight, int parentWidth, int parentHeight, int padding, boolean enableMagnify) {
    		float scale = getMaxScaleToParent(sourceWidth, sourceHeight, parentWidth, parentHeight, padding);
    		if (!enableMagnify && 1.0f < scale) {
    		    scale = 1;
    		}
    		
    		Matrix matrix = new Matrix();
    		matrix.postScale(scale, scale);
    		matrix.postTranslate((parentWidth - (int)(sourceWidth * scale)) / 2, (parentHeight - (int)(sourceHeight * scale)) / 2);
    		return matrix;
    	}
    	
    	static private float getMaxScaleToParent(int sourceWidth, int sourceHeight, int parentWidth, int parentHeight, int padding) {
    		float widthScale  = (float)(parentWidth  - padding) / (float)sourceWidth;
    		float heightScale = (float)(parentHeight - padding) / (float)sourceHeight;
    		return Math.min(widthScale, heightScale);
    	}
    }
    
    final static boolean hasVibratePermission(Context context) {
		boolean result = false;
		ApplicationInfo appInfo = context.getApplicationInfo();
		String appPackageName = appInfo.packageName;
		String permissonName = "android.permission.VIBRATE";

		AppVisorPushUtil
				.appVisorPushLog("check vibrate permission for packageName: "
						+ appPackageName);

		int permissonValue = context.getPackageManager().checkPermission(
				permissonName, appPackageName);

		if (permissonValue == PackageManager.PERMISSION_GRANTED) {
			result = true;

			AppVisorPushUtil
					.appVisorPushLog("vibrate PERMISSION_GRANTED for packageName: "
							+ appPackageName);

		} else {
			result = false;

			AppVisorPushUtil
					.appVisorPushLog("vibrate PERMISSION_DENIED for packageName: "
							+ appPackageName);
		}

		return result;
	}
    
    final static class RichPushImage
    {
    	static Bitmap imageBitmap = null;
    }
}
