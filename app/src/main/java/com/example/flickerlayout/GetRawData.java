//in order to use this class properly you need to implement the interface OnDownloadComplete


package com.example.flickerlayout;

import android.os.AsyncTask;
import android.util.Log;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

enum DownloadStatus {IDLE, PROCESSING, NOT_INITIALISED, FAILED_OR_EMPTY, OK};

public class GetRawData extends AsyncTask<String, Void, String> {
    private static final String TAG = "GetRawData";


    private DownloadStatus mDownloadStatus;
    private final OnDownloadComplete mCallBack;


    interface OnDownloadComplete {
        void onDownloadComplete(String Data, DownloadStatus status);
    }

    public GetRawData(OnDownloadComplete CallBack) {
        this.mDownloadStatus = DownloadStatus.IDLE;
        this.mCallBack = CallBack;

    }

    @Override
    protected String doInBackground(String... strings) {
        HttpURLConnection connection = null;
        BufferedReader reader = null;

        if (strings == null) {
            this.mDownloadStatus = DownloadStatus.NOT_INITIALISED;
            return null;
        }

        try {
            Log.d(TAG, "doInBackground: starting download");
            mDownloadStatus = DownloadStatus.PROCESSING;
            URL url = new URL(strings[0]);
            connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod("GET");
            connection.connect();
            int response = connection.getResponseCode();
            Log.d(TAG, "doInBackground: response code: " + response);

            StringBuilder result = new StringBuilder();
            reader = new BufferedReader(new InputStreamReader(connection.getInputStream()));
            String line;
            while (null != (line = reader.readLine())) {
                result.append(line).append("\n");
            }
            mDownloadStatus = DownloadStatus.OK;
            Log.d(TAG, "doInBackground: ending download");
            return result.toString();

        } catch (MalformedURLException e) {
            Log.e(TAG, "doInBackground: malformed exception: " + e.getMessage());
        } catch (IOException e) {
            Log.e(TAG, "doInBackground: IO exception: " + e.getMessage());
        } catch (SecurityException e) {
            Log.e(TAG, "doInBackground: Security exception: " + e.getMessage());
        } finally {
            if (connection != null) {
                connection.disconnect();
            }
            if (reader != null) {
                try {
                    reader.close();
                } catch (IOException e) {
                    Log.d(TAG, "doInBackground: Error closing stream: " + e.getMessage());
                }

            }
        }

        mDownloadStatus = DownloadStatus.FAILED_OR_EMPTY;
        return null;
    }

    void runInSameThread(String s) {


        Log.d(TAG, "runInSameThread: starts");
        if (mCallBack != null) {
            String str = doInBackground(s);
            mCallBack.onDownloadComplete(str, mDownloadStatus);
        }
        Log.d(TAG, "runInSameThread: ends");

    }

    @Override
    protected void onPostExecute(String s) {
        Log.d(TAG, "onPostExecute: started");
        if (mCallBack != null) {
            mCallBack.onDownloadComplete(s, mDownloadStatus);
        }
        Log.d(TAG, "onPostExecute: Ends");
    }


}
