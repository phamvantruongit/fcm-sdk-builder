package biz.appvisor.push.android.sdk;

import android.app.IntentService;
import android.app.KeyguardManager;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.os.PowerManager;
import android.support.v4.app.NotificationCompat;

import biz.appvisor.push.android.sdk.AppVisorPushUtil.RichPushImage;
import biz.appvisor.push.android.sdk.CommonAsyncTack.AsyncTaskCallback;

public class RichPushIntentService extends IntentService implements AsyncTaskCallback
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

	private void mainProcess()
	{
		if (this.richPush.isImagePush())
		{
			this.imagePushProcess();
			return;
		}
		
		this.webPushProcess();
	}
	
	private void imagePushProcess()
	{
		this.loadImage();
	}
	
	private void loadImage()
	{
		CommonAsyncTack task = new CommonAsyncTack(this.applicationContext, richPush, richPush.getContentURL(), RichPushIntentService.this);
		task.execute();
	}
	
	private void loadImageCallback(Bitmap image)
	{
		if (null == image) {
			this.fireNotification();
		} else {
			this.fireImageNotification(image);
		}
		
		if (this.isLockedScreen() || this.isScreenOff())
		{
			this.openImageDialog(image);
		}
		
		this.stopSelf();
	}
	
	private void webPushProcess()
	{
		this.fireWebNotification();
		
		if (this.isLockedScreen() || this.isScreenOff())
		{
			this.openWebDialog();
		}
		
		this.stopSelf();
	}
	
	private boolean isLockedScreen()
	{
		KeyguardManager keyguard = (KeyguardManager)getSystemService(Context.KEYGUARD_SERVICE);
		return keyguard.inKeyguardRestrictedInputMode();
	}
	
	private boolean isScreenOff()
	{
		PowerManager powerManager = (PowerManager)getSystemService(Context.POWER_SERVICE);
		return (false == powerManager.isScreenOn());
	}
	
	private void fireNotification()
	{
		NotificationCompat.Builder builder = this.commonNotificationBuilder();
		builder.setContentIntent(this.contentIntent());
		
		this.notificationManager.notify(this.richPush.pushId(), this.buildNotification(builder));
	}
	
	private void fireImageNotification(Bitmap image)
	{
		NotificationCompat.Builder builder = this.commonNotificationBuilder();
		builder.setContentIntent(this.imageContentIntent());
		builder.setStyle(this.notificationStyle(image));
		
		this.notificationManager.notify(this.richPush.pushId(), this.buildNotification(builder));
	}
	
	private void fireWebNotification()
	{
		NotificationCompat.Builder builder = this.commonNotificationBuilder();
		builder.setContentIntent(this.webContentIntent());
		
		this.notificationManager.notify(this.richPush.pushId(), this.buildNotification(builder));
	}
	
	private NotificationCompat.Builder commonNotificationBuilder()
	{
		NotificationCompat.Builder builder = new NotificationCompat.Builder(getApplicationContext());
		builder.setSmallIcon(AppVisorPushUtil.getPushIconID(this.applicationContext));
		builder.setContentTitle(this.notificationTitle());
		builder.setContentText(this.notificationMessage());
		builder.setSmallIcon(this.statusbarIconResourceId());
		builder.setLargeIcon(this.largeIconImage());
		builder.setStyle(this.notificationStyle());

		return builder;
	}
	
	private Notification buildNotification(NotificationCompat.Builder builder)
	{
		Notification notification = builder.build();
		notification.defaults |= Notification.DEFAULT_SOUND;
		notification.flags = Notification.FLAG_AUTO_CANCEL;
		
		if (this.richPush.isVibrationOnOff() && AppVisorPushUtil.hasVibratePermission(this.applicationContext))
		{
			notification.defaults |= Notification.DEFAULT_VIBRATE;
		}
		
		return notification;
	}
	
	private String notificationTitle()
	{
		String title = this.richPush.getTitle();
		if (null == title || 0 == title.length())
		{
			title = AppVisorPushUtil.getPushAppName(this.applicationContext);
		}
		
		return title;
	}
	
	private String notificationMessage()
	{
		return this.richPush.getMessage();
	}
	
	private int statusbarIconResourceId()
	{
		return AppVisorPushUtil.getStatusBarIconID(this.applicationContext);
	}
	
	private Bitmap largeIconImage()
	{
		return BitmapFactory.decodeResource(this.applicationContext.getResources(), AppVisorPushUtil.getPushIconID(this.applicationContext));
	}
	
	private Intent appIntent()
	{
		Class<?> callbackClass = null;
		try {
			callbackClass = Class.forName(AppVisorPushUtil.getPushCallbackClassName(this.applicationContext));
		}
		catch (ClassNotFoundException exception)
		{
			AppVisorPushUtil.appVisorPushLog("ClassNotFoundException: " + exception.getMessage());
		}
		
		Intent intent = (null == callbackClass) ? new Intent()
		                                        : new Intent(this.applicationContext, callbackClass);
		
		intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
		intent.putExtra(AppVisorPushSetting.KEY_APPVISOR_PUSH_INTENT, true);
		intent.putExtra(AppVisorPushSetting.KEY_PUSH_TITLE, this.notificationTitle());
		intent.putExtra(AppVisorPushSetting.KEY_PUSH_MESSAGE, this.notificationMessage());
		intent.putExtra(AppVisorPushSetting.KEY_PUSH_TRACKING_ID, this.richPush.getPushIDStr());
		intent.putExtra(AppVisorPushSetting.KEY_PUSH_X, this.richPush.getHashMap().get(AppVisorPushSetting.KEY_PUSH_X));
		intent.putExtra(AppVisorPushSetting.KEY_PUSH_Y, this.richPush.getHashMap().get(AppVisorPushSetting.KEY_PUSH_Y));
		intent.putExtra(AppVisorPushSetting.KEY_PUSH_Z, this.richPush.getHashMap().get(AppVisorPushSetting.KEY_PUSH_Z));
		intent.putExtra(AppVisorPushSetting.KEY_PUSH_W, this.richPush.getHashMap().get(AppVisorPushSetting.KEY_PUSH_W));
		
		return intent;
	}
	
	private Intent urlIntent()
	{
		Intent intent = new Intent(this.applicationContext, NotificationStartService.class);
		intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
		intent.putExtra(AppVisorPushSetting.KEY_PUSH_TITLE, this.notificationTitle());
		intent.putExtra("url", this.buildUrl());
		
		return intent;
	}
	
	private Intent dialogIntent()
	{
		Intent intent = new Intent(getApplicationContext(), RichPushDialogActivity.class);
		intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
		intent.addFlags(Intent.FLAG_ACTIVITY_MULTIPLE_TASK);
		intent.addFlags(Intent.FLAG_ACTIVITY_EXCLUDE_FROM_RECENTS);
		intent.addFlags(Intent.FLAG_ACTIVITY_NO_HISTORY);
		intent.putExtras(this.bundleData());
		
		return intent;
	}
	
	private PendingIntent contentIntent()
	{
		return PendingIntent.getActivity(this.applicationContext, (int)System.currentTimeMillis(), this.appIntent(), PendingIntent.FLAG_UPDATE_CURRENT);
	}
	
	private PendingIntent imageContentIntent()
	{
		if (this.richPush.hasURL())
		{
			return this.urlContentIntent();
		}
		
		return this.contentIntent();
	}
	
	private PendingIntent webContentIntent()
	{
		return this.dialogContentIntent();
	}
	
	private PendingIntent urlContentIntent()
	{
		return PendingIntent.getService(this.applicationContext, (int)System.currentTimeMillis(), this.urlIntent(), PendingIntent.FLAG_UPDATE_CURRENT);
	}
	
	private PendingIntent dialogContentIntent()
	{
		return PendingIntent.getActivity(this.applicationContext, (int)System.currentTimeMillis(), this.dialogIntent(), PendingIntent.FLAG_UPDATE_CURRENT);
	}
	
	private NotificationCompat.Style notificationStyle()
	{
		return this.notificationStyle(null);
	}
	
	private NotificationCompat.Style notificationStyle(Bitmap image)
	{
		if (null != image)
		{
			Bitmap bigPictureImage = AppVisorPushUtil.BitmapResizeHelper.resizeToBigPictureSize(image, this.applicationContext);
			
			return new NotificationCompat.BigPictureStyle()
			                             .bigPicture(bigPictureImage)
			                             .setBigContentTitle(this.notificationTitle())
			                             .setSummaryText(this.notificationMessage());
		}
		
		return new NotificationCompat.BigTextStyle().bigText(this.notificationMessage());
	}
	
	private void openImageDialog(Bitmap image)
	{
    	RichPushImage.imageBitmap = image;
    	
    	startActivity(this.dialogIntent());
	}
	
	private void openWebDialog()
	{
    	startActivity(this.dialogIntent());
	}
	
	private Bundle bundleData()
	{
		Bundle bundle = new Bundle();
		bundle.putSerializable("richPush", this.richPush);
		
    	return bundle;
	}
	
	private String buildUrl()
	{
		String appTrackingID = AppVisorPushUtil.getAppTrackingKey(this.applicationContext);
		return String.format("%s?%s=user&%s=callback&%s=%s&%s=%s&%s=%s&%s=%d",
				AppVisorPushSetting.PUSH_ARRIVED_URL,
				AppVisorPushSetting.PARAM_C, AppVisorPushSetting.PARAM_A,
				AppVisorPushSetting.PARAM_APP_TRACKING_KEY, appTrackingID,
				AppVisorPushSetting.PARAM_DEVICE_UUID, AppVisorPushUtil.getDeviceUUID(this.applicationContext, appTrackingID),
				AppVisorPushSetting.PARAM_PUSH_TRACKING_ID, this.richPush.getPushIDStr(),
				AppVisorPushSetting.PARAM_ARRIVED_TIME, System.currentTimeMillis());
	}
	
	@Override
	public void onDestroy() {
		super.onDestroy();
	}

	/**
	 * 以下画像非同期取得コールバック
	 */
	@Override
	public void preExecute() {}

	@Override
	public void postExecute(Bitmap result) {
		this.loadImageCallback(result);
	}

	@Override
	public void progressUpdate(int progress) {}

	@Override
	public void cancel() {}
}
