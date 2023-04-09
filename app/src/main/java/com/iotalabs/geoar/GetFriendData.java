package com.iotalabs.geoar;

import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;

public class GetFriendData extends AsyncTask<String, Void, String> {
    private Context context;
    String errorString = null;
    private static String TAG = "getFriend";
    private String mJsonString;

    private DbOpenHelper mDbOpenHelper;

    public GetFriendData(Context context){
        this.context=context;
    }
    @Override
    protected void onPreExecute() {
        super.onPreExecute();
    }

    @Override
    protected void onPostExecute(String result) {
        super.onPostExecute(result);
        if (result == null){

        }
        else {
            mJsonString = result;
            showResult();
        }
    }
    private void showResult(){

        String TAG_JSON="friend";
        String TAG_name = "name";
        String TAG_STR_LATITUDE = "str_latitude";
        String TAG_STR_LONGITUDE ="str_longitude";

        mDbOpenHelper = new DbOpenHelper(context);

        try {
            mDbOpenHelper.open();
            if(mDbOpenHelper.existtable()){
                mDbOpenHelper.deleteAll3();
            }
            JSONObject jsonObject = new JSONObject(mJsonString);
            JSONArray jsonArray = jsonObject.getJSONArray(TAG_JSON);

            for(int i=0;i<jsonArray.length();i++){

                JSONObject item = jsonArray.getJSONObject(i);

                String name = item.getString(TAG_name);
                String str_latitude = item.getString(TAG_STR_LATITUDE);
                String str_longitude = item.getString(TAG_STR_LONGITUDE);
                mDbOpenHelper.insertColumn3(name,str_latitude,str_longitude);

            }
            mDbOpenHelper.close();
        } catch (JSONException e) {

            Log.d(TAG, "showResult : ", e);
        }

    }

    @Override
    protected String doInBackground(String... params) {

        String TO_FIREND = (String)params[1];

        String serverURL = params[0];
        String postParameters = "TO_FRIEND=" + TO_FIREND;


        try {

            URL url = new URL(serverURL);
            HttpURLConnection httpURLConnection = (HttpURLConnection) url.openConnection();


            httpURLConnection.setReadTimeout(5000);
            httpURLConnection.setConnectTimeout(5000);
            httpURLConnection.setRequestMethod("POST");
            httpURLConnection.setDoInput(true);
            httpURLConnection.connect();


            OutputStream outputStream = httpURLConnection.getOutputStream();
            outputStream.write(postParameters.getBytes("UTF-8"));
            outputStream.flush();
            outputStream.close();


            int responseStatusCode = httpURLConnection.getResponseCode();
            Log.d(TAG, "response code - " + responseStatusCode);

            InputStream inputStream;
            if(responseStatusCode == HttpURLConnection.HTTP_OK) {
                inputStream = httpURLConnection.getInputStream();
            }
            else{
                inputStream = httpURLConnection.getErrorStream();
            }


            InputStreamReader inputStreamReader = new InputStreamReader(inputStream, "UTF-8");
            BufferedReader bufferedReader = new BufferedReader(inputStreamReader);

            StringBuilder sb = new StringBuilder();
            String line;

            while((line = bufferedReader.readLine()) != null){
                sb.append(line);
            }

            bufferedReader.close();

            return sb.toString().trim();


        } catch (Exception e) {

            Log.d(TAG, "GetData : Error ", e);
            errorString = e.toString();

            return null;
        }

    }
}