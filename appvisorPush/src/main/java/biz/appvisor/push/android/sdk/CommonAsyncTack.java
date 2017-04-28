package biz.appvisor.push.android.sdk;

import android.content.Context;
import android.graphics.Bitmap;
import android.os.AsyncTask;

import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;

import biz.appvisor.push.android.sdk.AppVisorPushUtil.BitmapResizeHelper;

public class CommonAsyncTack extends AsyncTask<String, Integer, Bitmap> {
	
	Context context;
	RichPush richPush;
	String url;
	
    public interface AsyncTaskCallback {
        void preExecute();
        void postExecute(Bitmap result);
        void progressUpdate(int progress);
        void cancel();
    }

    private AsyncTaskCallback callback = null;;

    public CommonAsyncTack(final Context context, final RichPush richPush, String url, AsyncTaskCallback _callback) {
    	this.context = context;
    	this.richPush = richPush;
    	this.url = url;
        this.callback = _callback;
    }

    @Override
    protected void onPreExecute() {
        super.onPreExecute();
        callback.preExecute();
    }

    @Override
    protected void onCancelled() {
        super.onCancelled();
        callback.cancel();
    }

    @Override
    protected void onPostExecute(Bitmap result) {
        super.onPostExecute(result);
        callback.postExecute(result);
    }

    @Override
    protected void onProgressUpdate(Integer... values) {
        super.onProgressUpdate(values);
        callback.progressUpdate(values[0]);
    }

    @Override
    protected Bitmap doInBackground(String... params) {
        try {
            URL imageUrl = new URL(url);
            InputStream imageIs;
            imageIs = imageUrl.openStream();
            
            return BitmapResizeHelper.decodeStreamByDisplayScale(imageIs, context);
        } catch (MalformedURLException e) {
        	e.printStackTrace();
            return null;
        } catch (IOException e) {
        	e.printStackTrace();
            return null;
        }
    }
}