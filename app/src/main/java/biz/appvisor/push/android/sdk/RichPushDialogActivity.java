package biz.appvisor.push.android.sdk;

import biz.appvisor.push.android.sdk.AppVisorPushUtil.RichPushImage;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.KeyguardManager;
import android.app.NotificationManager;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.GradientDrawable;
import android.graphics.drawable.LayerDrawable;
import android.graphics.drawable.StateListDrawable;
import android.os.Bundle;
import android.os.PowerManager;
import android.text.TextUtils.TruncateAt;
import android.util.AttributeSet;
import android.util.DisplayMetrics;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup.MarginLayoutParams;
import android.view.WindowManager;
import android.view.View.OnClickListener;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ImageView.ScaleType;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;

public class RichPushDialogActivity extends Activity {
	private Context  applicationContext = null;
	private RichPush richPush           = null;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		this.applicationContext = getApplicationContext();
		this.richPush           = (RichPush)getIntent().getSerializableExtra("richPush");
		
		this.disablePendingTransition();
		this.setView();
	}
	
	@Override
	public void onAttachedToWindow() {
		super.onAttachedToWindow();
		
		this.setShowAtLockScreenFlag();
	}

	private void setShowAtLockScreenFlag()
	{
		getWindow().addFlags(WindowManager.LayoutParams.FLAG_SHOW_WHEN_LOCKED);
		getWindow().addFlags(WindowManager.LayoutParams.FLAG_DISMISS_KEYGUARD);
		getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
		getWindow().addFlags(WindowManager.LayoutParams.FLAG_TURN_SCREEN_ON);
	}
	
	private void disablePendingTransition()
	{
		overridePendingTransition(0, 0);
	}
	
	private void setView()
	{
		LinearLayout rootLayout   = this.rootLayout();
		LinearLayout dialogLayout = this.dialogLayout();
		
		rootLayout.addView(dialogLayout);
		
		setContentView(rootLayout);
	}
	
	private LinearLayout rootLayout()
	{
		int paddingPixel = dpToPixel(20);
		
		LinearLayout linearLayout = new LinearLayout(this);
		linearLayout.setOrientation(LinearLayout.VERTICAL);
		linearLayout.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.MATCH_PARENT));
		linearLayout.setBackgroundColor(this.rootLayoutBackgroundColor());
		linearLayout.setPadding(paddingPixel, paddingPixel, paddingPixel, paddingPixel);
		linearLayout.setGravity(Gravity.CENTER_VERTICAL);
		
		return linearLayout;
	}
	
	private int rootLayoutBackgroundColor()
	{
		if (this.isLockedScreen() || this.isScreenOff())
		{
			return Color.BLACK;
		}
		
		return Color.parseColor("#66000000");
	}
	
	private boolean isLockedScreen()
	{
		KeyguardManager keyguard = (KeyguardManager) getSystemService(Context.KEYGUARD_SERVICE);
		return keyguard.inKeyguardRestrictedInputMode();
	}
	
	private boolean isScreenOff()
	{
		PowerManager powerManager = (PowerManager)getSystemService(Context.POWER_SERVICE);
		return (false == powerManager.isScreenOn());
	}
	
	private LinearLayout dialogLayout()
	{
		LinearLayout linearLayout = new LinearLayout(this);
		linearLayout.setOrientation(LinearLayout.VERTICAL);
		linearLayout.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, this.layoutHeight()));
		linearLayout.setBackground(this.dialogBackground());

		linearLayout.addView(this.headerLayout());
		linearLayout.addView(this.contentLayout());
		linearLayout.addView(this.footerLayout());
		
		return linearLayout;
	}
	
	private LayerDrawable dialogBackground()
	{
		GradientDrawable drawable = new GradientDrawable();
		drawable.setStroke(dpToPixel(1), 0x66ffffff);
		drawable.setColor(Color.BLACK);
		return new LayerDrawable(new Drawable[]{drawable});
	}
	
	private LinearLayout headerLayout()
	{
		LinearLayout linearLayout = new LinearLayout(this);
		linearLayout.setOrientation(LinearLayout.HORIZONTAL);
		linearLayout.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, dpToPixel(56)));
		linearLayout.setPadding(dpToPixel(4), dpToPixel(12), dpToPixel(4), dpToPixel(11));
		linearLayout.setGravity(Gravity.CENTER_VERTICAL);
		
		linearLayout.addView(this.iconImageView());
		linearLayout.addView(this.titleTextView());
		
		return linearLayout;
	}
	
	private ImageView iconImageView()
	{
		ImageView imageView = new ImageView(this);
		imageView.setImageResource(this.applicationIcon());

		return imageView;
	}
	
	private TextView titleTextView()
	{
		TextView textView = new TextView(this);
		textView.setText(this.dialogTitle());
		textView.setTextColor(Color.WHITE);
		textView.setTextSize(20);
		textView.setSingleLine(true);
		textView.setEllipsize(TruncateAt.END);
		
		return textView;
	}
	
	private String dialogTitle()
	{
		String title = this.richPush.getTitle();
		if (null == title || 0 == title.length())
		{
			title = AppVisorPushUtil.getPushAppName(this.applicationContext);
		}
		
		return title;
	}
	
	private LinearLayout contentLayout()
	{
		LinearLayout linearLayout = new LinearLayout(this);
		linearLayout.setOrientation(LinearLayout.HORIZONTAL);
		linearLayout.setBackgroundColor(Color.DKGRAY);
		linearLayout.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, this.layoutHeight(), 1.0f));
		
		int margin = dpToPixel(2);
		MarginLayoutParams marginLayoutParams = (MarginLayoutParams)linearLayout.getLayoutParams();
		marginLayoutParams.setMargins(margin, 0, margin, 0);
		
		linearLayout.addView(this.contentView());
		
		return linearLayout;
	}
	
	private View contentView()
	{
		if (this.richPush.isImagePush())
		{
			ScrollView contentScrollView = this.contentScrollView();
			contentScrollView.addView(this.contentImageView());
			return contentScrollView;
		}
		
		return this.contentWebView();
	}
	
	private ScrollView contentScrollView()
	{
		ScrollView contentScrollView = new ScrollView(this);
		contentScrollView.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT, 1.0f));
		
		return contentScrollView;
	}
	
	private ImageView contentImageView()
	{
		ScalingImageView imageView = new ScalingImageView(this);
		imageView.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT));
		
		Bitmap bitmap = this.contentImageBitmap();
		if (null != bitmap)
		{
			imageView.setImageBitmap(bitmap);
			imageView.setScaleType(ScaleType.FIT_START);
			imageView.setAdjustViewBounds(true);
		}
		
		return imageView;
	}
	
	@SuppressLint("SetJavaScriptEnabled")
	private WebView contentWebView()
	{
		WebView webView = new WebView(this);
		webView.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.MATCH_PARENT));
		webView.setScrollBarStyle(WebView.SCROLLBARS_OUTSIDE_OVERLAY);
		webView.getSettings().setJavaScriptEnabled(true);
		webView.getSettings().setLoadWithOverviewMode(true);
		webView.getSettings().setUseWideViewPort(true);
		webView.setWebViewClient(new WebViewClient() {
			@Override
			public void onPageStarted(WebView view, String url, Bitmap favicon) {
			}

			@Override
			public void onReceivedError(WebView view, int errorCode,
					String description, String failingUrl) {
				super.onReceivedError(view, errorCode, description, failingUrl);
			}
			
			@Override
			public void onPageFinished(WebView view, String url) {
			}
		});

		webView.loadUrl(this.richPush.getContentURL());
		
		return webView;
	}
	
	private Bitmap contentImageBitmap()
	{
		return RichPushImage.imageBitmap;
	}
	
	private LinearLayout footerLayout()
	{
		int paddingPixel = dpToPixel(10);
		
		LinearLayout linearLayout = new LinearLayout(this);
		linearLayout.setOrientation(LinearLayout.HORIZONTAL);
		linearLayout.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT));
		linearLayout.setPadding(paddingPixel, paddingPixel, paddingPixel, paddingPixel);
		
		linearLayout.addView(this.showButton());
		linearLayout.addView(this.spaceView(new LinearLayout.LayoutParams(paddingPixel, dpToPixel(40))));
		linearLayout.addView(this.cancelButton());
		
		return linearLayout;
	}
	
	private Button showButton()
	{
		Button button = new Button(this);
		button.setText("表示");
		button.setBackground(this.buttonDrawable());
		button.setLayoutParams(new LinearLayout.LayoutParams(0, dpToPixel(39), 1.0f));
		button.setPadding(0, dpToPixel(1), 0, 0);
		button.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				RichPushDialogActivity.this.pressedShowButton();
			}
		});
		
		return button;
	}
	
	private Button cancelButton()
	{
		Button button = new Button(this);
		button.setText("取り消し");
		button.setBackground(this.buttonDrawable());
		button.setLayoutParams(new LinearLayout.LayoutParams(0, dpToPixel(39), 1.0f));
		button.setPadding(0, dpToPixel(1), 0, 0);
		button.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				RichPushDialogActivity.this.pressedCancelButton();
			}
		});
		
		return button;
	}
	
	private Drawable buttonDrawable()
	{
		StateListDrawable drawable = new StateListDrawable();
		drawable.addState(new int[] { -android.R.attr.state_pressed }, new ColorDrawable(Color.LTGRAY));
		drawable.addState(new int[] {  android.R.attr.state_pressed }, new ColorDrawable(Color.GRAY));
		
		return drawable;
	}
	
	private View spaceView(LinearLayout.LayoutParams layoutParams)
	{
		View view = new View(this);
		view.setLayoutParams(layoutParams);
		return view;
	}
	
	private void pressedShowButton()
	{
		this.cancelNotification();
		
		if (this.richPush.hasURL()) {
			this.startBrowser();
		} else {
			this.startApp();
		}
		
		finish();
	}
	
	private void pressedCancelButton()
	{
		finish();
	}
	
	private void startBrowser()
	{
		startService(this.urlIntent());
	}
	
	private void startApp()
	{
		startActivity(this.appIntent());
	}
	
	private void cancelNotification()
	{
		NotificationManager notificationManager = (NotificationManager)getSystemService(Context.NOTIFICATION_SERVICE);
		notificationManager.cancel(this.richPush.pushId());
	}
	
	private int layoutHeight()
	{
		if (this.richPush.isImagePush()) {
			return LinearLayout.LayoutParams.WRAP_CONTENT;
		}
		
		return LinearLayout.LayoutParams.MATCH_PARENT;
	}
	
	private int applicationIcon() {
		PackageManager pm = getPackageManager();
		String packageName = getPackageName();
		int icon = 0;
		
		try {
			ApplicationInfo info = pm.getApplicationInfo(packageName, 0);
			icon = info.icon;
		} catch (NameNotFoundException e) {
		 e.printStackTrace();
		}
		
		return icon;
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
		intent.putExtra("url", this.buildUrl());
		intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
		intent.putExtra(AppVisorPushSetting.KEY_PUSH_TITLE, this.notificationTitle());
		
		return intent;
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
	
	private String notificationTitle()
	{
		String title = this.richPush.getTitle();
		if (null != title && 0 < title.length())
		{
			title = AppVisorPushUtil.getPushAppName(this.applicationContext);
		}
		
		return title;
	}
	
	private String notificationMessage()
	{
		return this.richPush.getMessage();
	}
	
	private int dpToPixel(float dp) {
		DisplayMetrics metrics = getResources().getDisplayMetrics();
		int pixels = (int)(metrics.density * dp + 0.5f);
		return pixels;
	}
	
	private class ScalingImageView extends ImageView
	{
		public ScalingImageView(Context context) {
			super(context);
		}

		public ScalingImageView(Context context, AttributeSet attrs) {
			super(context, attrs);
		}

		public ScalingImageView(Context context, AttributeSet attrs, int defStyle) {
			super(context, attrs, defStyle);
		}

		protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
			Drawable mDrawable = getDrawable();
			if (mDrawable != null) {
				int mDrawableWidth = mDrawable.getIntrinsicWidth();
				int mDrawableHeight = mDrawable.getIntrinsicHeight();
				float actualAspect = (float) mDrawableWidth
						/ (float) mDrawableHeight;

				// Assuming the width is ok, so we calculate the height.
				final int actualWidth = MeasureSpec.getSize(widthMeasureSpec);
				final int height = (int) (actualWidth / actualAspect);
				heightMeasureSpec = MeasureSpec.makeMeasureSpec(height,
						MeasureSpec.EXACTLY);
			}
			super.onMeasure(widthMeasureSpec, heightMeasureSpec);
		}
	}
}
