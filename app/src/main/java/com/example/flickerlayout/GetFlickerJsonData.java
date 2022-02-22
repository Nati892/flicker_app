package com.example.flickerlayout;

import android.media.MediaPlayer;
import android.net.Uri;
import android.os.AsyncTask;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class GetFlickerJsonData extends AsyncTask<String,Void,List<Photo>>  implements GetRawData.OnDownloadComplete {
    //private fields
    private static final String TAG = "GetFlickerJsonData";

    private List<Photo> mPhotoList = null;
    private String mBaseURL;
    private String mLanguage;
    private boolean mMatchAll;
    private final OnDataAvailable mcallback;
    private boolean runningOnSameThread=false;


    public GetFlickerJsonData(String baseURL, String language, boolean matchAll, OnDataAvailable mcallback) {
        Log.d(TAG, "GetFlickerJsonData: called");
        mBaseURL = baseURL;
        mLanguage = language;
        mMatchAll = matchAll;
        this.mcallback = mcallback;
    }

    @Override
    protected void onPostExecute(List<Photo> photos) {
        Log.d(TAG, "onPostExecute: starts");

        if (mcallback!=null)
            {mcallback.OnDataAvailable(mPhotoList,DownloadStatus.OK);}
        Log.d(TAG, "onPostExecute: ends");


    }

    @Override
    protected List<Photo> doInBackground(String... strings) {
        Log.d(TAG, "doInBackground: starts");
        String destinationURi=createUri(strings[0],mLanguage,mMatchAll);
        GetRawData getRawData=new GetRawData(this);
        getRawData.runInSameThread(destinationURi);
        Log.d(TAG, "doInBackground: ");
        return mPhotoList;
    }

    interface OnDataAvailable {

        void OnDataAvailable(List<Photo> data, DownloadStatus status);

    }

    void executeOnSameThread(String searchCriteria) {
        Log.d(TAG, "executeOnSameThread: starts");
        runningOnSameThread=true;
        String destinationUri = createUri(searchCriteria, mLanguage, mMatchAll);
        GetRawData getRawData = new GetRawData(this);
        getRawData.execute(destinationUri);
        Log.d(TAG, "executeOnSameThread: ends");
    }

    private String createUri(String searchCriteria, String lang, boolean matchAll) {
        Log.d(TAG, "createUri: Starts");
        return Uri.parse(mBaseURL).buildUpon()
                .appendQueryParameter("tags", searchCriteria)
                .appendQueryParameter("tagmode", (matchAll ? "ALL" : "ANY"))
                .appendQueryParameter("lang", lang)
                .appendQueryParameter("format", "json")
                .appendQueryParameter("nojsoncallback", "1")
                .build().toString();

    }

    @Override
    public void onDownloadComplete(String Data, DownloadStatus status) {
        Log.d(TAG, "onDownloadComplete: status =" + status);
        if (status == DownloadStatus.OK) {
            this.mPhotoList = new ArrayList<>();
            try {
                JSONObject jsonData = new JSONObject(Data);
                JSONArray itemArray = jsonData.getJSONArray("items");

                for (int i = 0; i < itemArray.length(); i++) {
                    JSONObject jsonphoto = itemArray.getJSONObject(i);
                    String title = jsonphoto.getString("title");
                    String author = jsonphoto.getString("author");
                    String authorId = jsonphoto.getString("author_id");
                    String tags = jsonphoto.getString("tags");
                    String photoUrl = jsonphoto.getJSONObject("media").getString("m");
                    String link = photoUrl.replaceFirst("_m.", "_b.");
                    Photo photo = new Photo(title, author, authorId, link, tags, photoUrl);
                    mPhotoList.add(photo);
                    Log.d(TAG, "onDownloadComplete: complete  +  " + photo.toString());
                }
            } catch (JSONException e) {
                e.printStackTrace();
                Log.e(TAG, "onDownloadComplete: Error with json data processing " + e.toString());
                status = DownloadStatus.FAILED_OR_EMPTY;
            }


        }
        if (runningOnSameThread&&mcallback != null) {
            Log.d(TAG, "onDownloadComplete: mcallback is not null!, calling OnData Available");
    mcallback.OnDataAvailable(mPhotoList,status);

        }
    }
}
