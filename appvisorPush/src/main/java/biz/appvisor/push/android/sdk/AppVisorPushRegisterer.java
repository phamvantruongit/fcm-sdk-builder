package biz.appvisor.push.android.sdk;

import android.content.Context;
import android.content.SharedPreferences;

import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.StatusLine;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 * Created by hirayamatakaaki on 2018/03/15.
 */

public class AppVisorPushRegisterer {
    public static boolean sendToServer(Context appContext, String appTrackingKey, boolean isInService)
    {
        final DefaultHttpClient client = new DefaultHttpClient();
        final HttpPost method = new HttpPost(AppVisorPushSetting.PUSH_INFOR_URL);

        try
        {
            List<NameValuePair> postParams = new ArrayList<NameValuePair>();
            String appVersion 		= AppVisorPushUtil.getAppVersion( appContext );
            String localCountry 	= AppVisorPushUtil.getCountry();
            String localLanguage	= AppVisorPushUtil.getLanguage();
            String sdkVersion		= AppVisorPushSetting.APPVISOR_PUSH_SDK_VERSION;
            if(AppVisorPushSetting.APPVISOR_PUSH_SDK_CUSTOMIZE_VERSION.length()>0){
                sdkVersion += "-" + AppVisorPushSetting.APPVISOR_PUSH_SDK_CUSTOMIZE_VERSION;
            }

            String osType			= AppVisorPushSetting.OS_TYPE;
            String deviceModel		= AppVisorPushUtil.getModel();
            String osVersion		= AppVisorPushUtil.getOSVersion();
            String connectType		= AppVisorPushUtil.getConnection( appContext );
            String deviceUUID		= AppVisorPushUtil.getDeviceUUID( appContext, appTrackingKey);
            String pushToken		= AppVisorPushUtil.getPushToken( appContext );

            postParams.add(new BasicNameValuePair( AppVisorPushSetting.PARAM_C , "user" ) );
            postParams.add(new BasicNameValuePair( AppVisorPushSetting.PARAM_A , "regist" ) );

            postParams.add(new BasicNameValuePair( AppVisorPushSetting.PARAM_APP_TRACKING_KEY , appTrackingKey ) );
            postParams.add(new BasicNameValuePair( AppVisorPushSetting.PARAM_DEVICE_UUID , deviceUUID ) );
            postParams.add(new BasicNameValuePair( AppVisorPushSetting.PARAM_DEVICE_TOKEN , pushToken ) );
            postParams.add(new BasicNameValuePair( AppVisorPushSetting.PARAM_APP_VERSION , appVersion ) );
            postParams.add(new BasicNameValuePair( AppVisorPushSetting.PARAM_LOCALE_COUNTRY , localCountry ) );
            postParams.add(new BasicNameValuePair( AppVisorPushSetting.PARAM_LOCALE_LANGUAGE , localLanguage) );
            postParams.add(new BasicNameValuePair( AppVisorPushSetting.PARAM_SDK_VERSION , sdkVersion) );
            postParams.add(new BasicNameValuePair( AppVisorPushSetting.PARAM_DEVICE_TYPE , deviceModel) );
            postParams.add(new BasicNameValuePair( AppVisorPushSetting.PARAM_DEVICE_MODEL , deviceModel) );
            postParams.add(new BasicNameValuePair( AppVisorPushSetting.PARAM_OS_VERSION , osVersion) );
            postParams.add(new BasicNameValuePair( AppVisorPushSetting.PARAM_OS_TYPE , osType) );
            postParams.add(new BasicNameValuePair( AppVisorPushSetting.PARAM_CONNECTION_TYPE , connectType) );

            if (isInService)
            {
                postParams.add(new BasicNameValuePair( AppVisorPushSetting.PARAM_IN_SERVICE , "1") );
            }

            for (int i = 1; i < 6; i++)
            {
                String propertyValue = AppVisorPushUtil.getUserProperties( appContext ,i);
                if ( propertyValue.equals(AppVisorPushSetting.PROPERTY_DEFAULT_VALUE) == false )
                {
                    String propertyKey = AppVisorPushSetting.PARAM_PROPERTY + i;
                    postParams.add(new BasicNameValuePair( propertyKey , propertyValue ));
                }
            }

            List<String> specialPropertyList = (List<String>) AppVisorPush.sharedInstance().getUserPropertyWithGroup( AppVisorPush.SpecialUserPropertyGroup1);;
            int sizeOfList = specialPropertyList.size();

            if ( sizeOfList > 0 )
            {
                for (int i = 0; i < sizeOfList; i++)
                {
                    String propertyValue = specialPropertyList.get(i);
                    String propertyKey = AppVisorPushSetting.PARAM_PROPERTY + AppVisorPush.SpecialUserPropertyGroup1 + "[]";
                    postParams.add(new BasicNameValuePair( propertyKey , propertyValue ));
                }
            }
            else
            {
                String propertyValue = "";
                String propertyKey = AppVisorPushSetting.PARAM_PROPERTY + AppVisorPush.SpecialUserPropertyGroup1 + "[]";
                postParams.add(new BasicNameValuePair( propertyKey , propertyValue ));
            }

            method.setEntity(new UrlEncodedFormEntity(postParams, "UTF-8"));
            final HttpResponse response = client.execute(method);

            final StatusLine statusLine = response.getStatusLine();
            final int statusCode = statusLine.getStatusCode();

            AppVisorPushUtil.appVisorPushLog( String.format("communication completely with status code: %d", Integer.valueOf(statusCode)) );

            if (statusCode >= 500 && statusCode <= 599)
            {
                AppVisorPushUtil.appVisorPushLog( "Sent data failed. Error code:" + statusCode );
                return false;
            }
            else
            {
                ByteArrayOutputStream os = new ByteArrayOutputStream();
                response.getEntity().writeTo(os);

                String appStatusStr = "OK";
                String pushStatusString = "ON";
                try
                {
                    JSONObject jsonObj = new JSONObject(os.toString());
                    appStatusStr = jsonObj.getString(AppVisorPushSetting.PARAM_APP_STATUS);
                    pushStatusString = jsonObj.getString(AppVisorPushSetting.PARAM_PUSH_STATUS);

                    try
                    {
                        JSONObject properties = jsonObj.getJSONObject(AppVisorPushSetting.PARAM_PROPERTIES);
                        if( properties != null )
                        {
                            for(Iterator<String> it = properties.keys(); it.hasNext();)
                            {
                                String propertyKey = it.next();
                                String propertyValue = properties.getString( propertyKey );

                                SharedPreferences.Editor editor = appContext.getSharedPreferences(AppVisorPushSetting.SHARED_PREFERENCES_KEY ,Context.MODE_PRIVATE).edit();
                                editor.putString(propertyKey, propertyValue);

                                editor.commit();
                            }
                        }
                    } catch (JSONException e) {
                        AppVisorPushUtil.appVisorPushLog("PARAM_PROPERTIES is empty.");
                    }

                } catch (JSONException e) {
                    AppVisorPushUtil.appVisorPushWaring("JSONException",e);
                }

                if (null == appStatusStr || appStatusStr.equals("OK"))
                {
                    AppVisorPushUtil.saveAppStatus( appContext , AppVisorPushSetting.APP_STATUS_KEY_OK );
                    AppVisorPushUtil.appVisorPushLog( "Sent data successed.");

                    if (null != appStatusStr && appStatusStr.equals("OK") && null != pushStatusString)
                    {
                        if ( pushStatusString.equals("ON") )
                        {
                            AppVisorPushUtil.appVisorPushLog( "Push status is On in server.");
                            AppVisorPushUtil.savePushStatus( appContext , 1);
                        }else if ( pushStatusString.equals("OFF") ){
                            AppVisorPushUtil.appVisorPushLog( "Push status is off in server.");
                            AppVisorPushUtil.savePushStatus( appContext , 0);
                        }
                    }

                    return true;
                }
                else
                {
                    if (appStatusStr.equals("KL"))
                    {
                        AppVisorPushUtil.saveAppStatus( appContext , AppVisorPushSetting.APP_STATUS_KEY_KL );
                        AppVisorPushUtil.appVisorPushLog( "Sent data failed because server Response KL for this app." );
                    }else{
                        AppVisorPushUtil.appVisorPushLog( "Sent data failed .");
                    }

                    return false;
                }
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
