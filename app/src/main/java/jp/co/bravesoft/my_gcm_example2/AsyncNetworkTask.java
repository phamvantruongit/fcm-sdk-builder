package jp.co.bravesoft.my_gcm_example2;

import android.content.Context;
import android.os.AsyncTask;
import android.os.SystemClock;
import android.util.Log;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

public class AsyncNetworkTask extends AsyncTask<String, Integer, String> {
//    private TextView txtResult;
//    private ProgressBar progress;
    private static final String TAG = "AsyncNetworkTask";

    public AsyncNetworkTask(Context context) {
        super();
 //       MainActivity activity = (MainActivity)context;
  //      txtResult = (TextView)activity.findViewById(R.id.txtResult);
   //     progress = (ProgressBar)activity.findViewById(R.id.progress);
    }

    @Override
    protected String doInBackground(String... params) {
        Log.d(TAG, "doInBackground");
        publishProgress(30);
        SystemClock.sleep(3000);
        publishProgress(60);
        StringBuilder builder = new StringBuilder();
        try {
            URL url = new URL(params[0]);
            HttpURLConnection con = (HttpURLConnection)url.openConnection();
            con.setRequestMethod("GET");
            BufferedReader reader = new BufferedReader(new InputStreamReader(con.getInputStream(), "Shift-JIS"));
            String line;
            while ((line = reader.readLine()) != null){
                builder.append(line);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        publishProgress(100);
        return builder.toString();
    }

    /*
    @Override
    protected void onPreExecute() {
        progress.setVisibility(ProgressBar.VISIBLE);
    }

    @Override
    protected void onProgressUpdate(Integer... values) {
        // Log.d("url", values[0].toString());
        progress.setProgress(values[0]);
    }
    */

    @Override
    protected void onPostExecute(String result) {
        //txtResult.setText(result);
        //progress.setVisibility(ProgressBar.GONE);
        Log.d(TAG, "onPostExecute: " + result);
    }
    /*

    @Override
    protected void onCancelled() {
        txtResult.setText("キャンセルされました。");
        progress.setVisibility(ProgressBar.GONE);
    }
    */
}