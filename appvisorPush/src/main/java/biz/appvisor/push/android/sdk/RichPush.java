package biz.appvisor.push.android.sdk;

import java.io.Serializable;
import java.util.HashMap;

public class RichPush implements Serializable {
	
	private static final long serialVersionUID = 1L;
	
	String title;
	String message;
	String className;
	String pushIDStr;
	HashMap<String, String> hashMap;
	boolean vibrationOnOff;
	String contentFlg;
	String contentURL;
	String urlFlag;

	public RichPush(String title, String message,
			String className, String pushIDStr, HashMap<String, String> hashMap,
			boolean vibrationOnOff, String contentFlg, String contentURL, String urlFlag) {
		super();
		this.title = title;
		this.message = message;
		this.className = className;
		this.pushIDStr = pushIDStr;
		this.hashMap = hashMap;
		this.vibrationOnOff = vibrationOnOff;
		this.contentFlg = contentFlg;
		this.contentURL = contentURL;
		this.urlFlag = urlFlag;
	}

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public String getMessage() {
		return message;
	}

	public void setMessage(String message) {
		this.message = message;
	}

	public String getClassName() {
		return className;
	}

	public void setCls(String className) {
		this.className = className;
	}

	public String getPushIDStr() {
		return pushIDStr;
	}

	public void setPushIDStr(String pushIDStr) {
		this.pushIDStr = pushIDStr;
	}

	public HashMap<String, String> getHashMap() {
		return hashMap;
	}

	public void setHashMap(HashMap<String, String> hashMap) {
		this.hashMap = hashMap;
	}

	public boolean isVibrationOnOff() {
		return vibrationOnOff;
	}

	public void setVibrationOnOff(boolean vibrationOnOff) {
		this.vibrationOnOff = vibrationOnOff;
	}

	public String getContentFlg() {
		return contentFlg;
	}

	public void setContentFlg(String contentFlg) {
		this.contentFlg = contentFlg;
	}

	public String getContentURL() {
		return contentURL;
	}

	public void setContentURL(String contentURL) {
		this.contentURL = contentURL;
	}
	
	public boolean hasURL()
	{
		return (this.urlFlag != null);
	}
	
	public boolean hasTitle()
	{
		return (this.title != null && this.title.length() > 0);
	}
	
	public boolean isImagePush()
	{
		return contentFlg.equals(AppVisorPushSetting.RICH_PUSH_IMAGE);
	}
	
	public boolean isWebPush()
	{
		return contentFlg.equals(AppVisorPushSetting.RICH_PUSH_WEB);
	}
	
	public int pushId()
	{
		int pushId = 0;
		
		try {
			pushId = Integer.parseInt(pushIDStr);
		}
		catch (NumberFormatException e) {
			AppVisorPushUtil.appVisorPushWaring("NumberFormatException", e);
		}
		
		return pushId;
	}
}
