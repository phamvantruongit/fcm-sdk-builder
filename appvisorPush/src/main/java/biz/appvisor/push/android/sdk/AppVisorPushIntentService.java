package biz.appvisor.push.android.sdk;

import android.app.NotificationManager;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;

import com.google.android.gcm.GCMBaseIntentService;

import java.util.HashMap;

public class AppVisorPushIntentService extends GCMBaseIntentService{
	
	public AppVisorPushIntentService() {
		super();
	}

	@Override
	protected void onRegistered(Context context, String registrationId) {
		AppVisorPushUtil.appVisorPushLog("Device registered: regId = "
				+ registrationId);

		AppVisorPushUtil.savePushToken(context, registrationId);
		AppVisorPush.startSendDeviceInfor(context);
	}

	@Override
	protected void onUnregistered(Context context, String registrationId) {
		AppVisorPushUtil.appVisorPushLog("Device unregistered");
	}

	@Override
	protected void onMessage(Context context, Intent intent) {
		AppVisorPushUtil.appVisorPushLog("Received message data.");

		String appvisorPushFlag = intent
				.getStringExtra(AppVisorPushSetting.KEY_APPVISOR_PUSH_INTENT);
		String appvisor_sn = intent
				.getStringExtra(AppVisorPushSetting.KEY_SILENCE_NOTIFICATION);
		String appvisor_bn = intent
				.getStringExtra(AppVisorPushSetting.KEY_BACKGROUND_NOTIFICATION);
		if (null == appvisorPushFlag || !appvisorPushFlag.equals("1")) {
			AppVisorPushUtil
					.appVisorPushLog("ignore message which is not came from appvisor.");
			return;
		}
		boolean is_silent;
		if ("1".equals(appvisor_sn)) {
			is_silent = true;
		} else {
			is_silent = false;
		}
		boolean is_background;
		if ("1".equals(appvisor_bn)) {
			is_background = true;
		} else {
			is_background = false;
		}

		String messageStr = intent
				.getStringExtra(AppVisorPushSetting.KEY_PUSH_MESSAGE);
		String titleStr = intent
				.getStringExtra(AppVisorPushSetting.KEY_PUSH_TITLE);

		String pushIDStr = intent
				.getStringExtra(AppVisorPushSetting.KEY_PUSH_TRACKING_ID);
		boolean vibrationOnOff = intent.getBooleanExtra(
				AppVisorPushSetting.KEY_PUSH_VIBRATION, false);

		String xStr = intent.getStringExtra(AppVisorPushSetting.KEY_PUSH_X);
		String yStr = intent.getStringExtra(AppVisorPushSetting.KEY_PUSH_Y);
		String zStr = intent.getStringExtra(AppVisorPushSetting.KEY_PUSH_Z);
		String wStr = intent.getStringExtra(AppVisorPushSetting.KEY_PUSH_W);

		String urlFlag = intent
				.getStringExtra(AppVisorPushSetting.KEY_PUSH_URL);

		AppVisorPushUtil.appVisorPushLog("X:" + xStr + ",Y:" + yStr + ",Z:"
				+ zStr + ",W:" + wStr + ",V:" + vibrationOnOff);

		HashMap<String, String> hashMap = new HashMap<String, String>();
		hashMap.put(AppVisorPushSetting.KEY_PUSH_X, xStr);
		hashMap.put(AppVisorPushSetting.KEY_PUSH_Y, yStr);
		hashMap.put(AppVisorPushSetting.KEY_PUSH_Z, zStr);
		hashMap.put(AppVisorPushSetting.KEY_PUSH_W, wStr);

		// rich push
		String content_flg = intent.getStringExtra(AppVisorPushSetting.KEY_PUSH_CONTENT_FLAG);
		String content_url = intent.getStringExtra(AppVisorPushSetting.KEY_PUSH_CONTENT_URL);
		
		AppVisorPushUtil.appVisorPushLog("Received message:" + pushIDStr + ","
				+ titleStr + "," + messageStr);

		if (is_background) {
			String serviceName = AppVisorPushUtil
					.getPushCallbackServiceName(context);

			if (serviceName != null && !"".equals(serviceName)) {
				Class<?> callBackService = null;
				try {
					callBackService = Class.forName(serviceName);
				} catch (ClassNotFoundException e) {
//					e.printStackTrace();
				}
				Intent mIntent = intent;
				intent.removeExtra(AppVisorPushSetting.KEY_APPVISOR_PUSH_INTENT);
				intent.putExtra(AppVisorPushSetting.KEY_APPVISOR_PUSH_INTENT,
						true);
				intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
				mIntent.setClass(context, callBackService);
				startService(mIntent);
			}

		}

		if (!is_silent) {
			if ((messageStr == null || messageStr.length() == 0)
					&& (titleStr == null || titleStr.length() == 0)) {
				AppVisorPushUtil
						.appVisorPushLog("ignore message which don't have body and title.");
			} else {

				// record arrived time
				long arrivedTimeStamp = System.currentTimeMillis();
				int pushTaskID = Integer.parseInt(pushIDStr);
				AppVisorPushUtil.addArrivedTimeRecord(context,
						arrivedTimeStamp, pushTaskID);

				String clsName = AppVisorPushUtil
						.getPushCallbackClassName(context);
				Class<?> callBackClass = null;
				try {
					callBackClass = Class.forName(clsName);
				} catch (ClassNotFoundException e) {
//					e.printStackTrace();
				}

				if (AppVisorPushSetting.thisApiLevel >= 16 && content_flg != null) {
					// OS Version after Android 4.1 && Rich Push
					this.showRichNotification(titleStr, messageStr, context,
							clsName, pushIDStr, hashMap, vibrationOnOff, content_flg, content_url, urlFlag);
					
					return;
				}

				NotificationManager notiManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
				if (urlFlag != null) {
					// URL Mode
					AppvisorPushNotification.showUrlNotification(titleStr, messageStr, context,
							pushIDStr, vibrationOnOff, notiManager );
				} else {
					// Normal Mode
					AppvisorPushNotification.showNotification(titleStr, messageStr, context,
							callBackClass, pushIDStr, hashMap, vibrationOnOff, notiManager );
				}

			}

		}
		AppVisorPushUtil.appVisorPushLog("Received message:Finished");
		this.stopSelf();
	}


	protected void showRichNotification(String title, String message,
			final Context context, String className, String pushIDStr,
			HashMap<String, String> hashMap, boolean vibrationOnOff,
			String contentFlg, String contentURL, String urlFlag) {
		
		AppVisorPushUtil.appVisorPushLog("show Rich Notification start");

		final RichPush richPush = new RichPush(title, message, className, pushIDStr,
				hashMap, vibrationOnOff, contentFlg, contentURL, urlFlag);

		Intent intent = new Intent(context, RichPushIntentService.class);
		intent.putExtra("richPush", richPush);
		startService(intent);
	}
	
	@Override
	public void onError(Context context, String errorId) {
		AppVisorPushUtil.appVisorPushLog("Received error: " + errorId);
	}

	@Override
	protected boolean onRecoverableError(Context context, String errorId) {
		AppVisorPushUtil.appVisorPushLog("Received recoverable error: "
				+ errorId);
		return super.onRecoverableError(context, errorId);
	}

	@Override
	protected String[] getSenderIds(Context context) {
		String[] senderIDs = new String[1];

		senderIDs[0] = AppVisorPushUtil.getPushSenderID(context);

		return senderIDs;
	}

	protected static boolean checkIsVibrateEnable(Context context) {
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
}
