package com.iotalabs.geoar;

import android.app.Activity;
import android.app.ProgressDialog;
import android.os.AsyncTask;
import android.os.Handler;
import android.widget.Toast;

import java.io.BufferedReader;

import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;

import java.net.HttpURLConnection;
import java.net.URL;

class InsertFriendData extends AsyncTask<String, Void, String>{

    private Activity activity;
    private ProgressDialog progressDialog;
    private DbOpenHelper mDbOpenHelper;
    private Handler handler = new Handler();
    public InsertFriendData(Activity activity){
        this.activity=activity;
    }

    @Override
    protected void onPreExecute() {
        super.onPreExecute();
        progressDialog = ProgressDialog.show(activity, "Please Wait", null, true, true);
        progressDialog.setCanceledOnTouchOutside(false);//바깥터치X
        progressDialog.setCancelable(false);//뒤로가기X
    }


    @Override
    protected void onPostExecute(String result) {
        super.onPostExecute(result);
        progressDialog.dismiss();
    }


    @Override
    protected String doInBackground(String... params) {
        String result;
        String TO_FRIEND = (String)params[1];
        String FROM_FRIEND = (String)params[2];
        String str_latitude = (String)params[3];
        String str_longitude = (String)params[4];
        String name = (String)params[5];

        String serverURL = (String)params[0];
        String postParameters = "TO_FRIEND=" + TO_FRIEND + "&FROM_FRIEND=" + FROM_FRIEND
                + "&str_latitude=" +str_latitude+ "&str_longitude=" + str_longitude + "&name=" + name;
//두번째부턴 &를 붙여야함

        try {

            URL url = new URL(serverURL);
            HttpURLConnection httpURLConnection = (HttpURLConnection) url.openConnection();


            httpURLConnection.setReadTimeout(5000);
            httpURLConnection.setConnectTimeout(5000);
            httpURLConnection.setRequestMethod("POST");
            httpURLConnection.connect();


            OutputStream outputStream = httpURLConnection.getOutputStream();
            outputStream.write(postParameters.getBytes("UTF-8"));
            outputStream.flush();
            outputStream.close();


            int responseStatusCode = httpURLConnection.getResponseCode();

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
            String line = null;

            while((line = bufferedReader.readLine()) != null){
                sb.append(line);
            }

            bufferedReader.close();
            result = sb.toString();
            if(result.equals("새로운 사용자를 추가했습니다.")){//성공시
                mDbOpenHelper = new DbOpenHelper(activity);
                mDbOpenHelper.open();
                mDbOpenHelper.insertColumn( FROM_FRIEND,name);
                mDbOpenHelper.close();
                toast("친구 등록 완료!");
            }
            else{
                toast("친구등록에 실패했습니다.");
            }
            activity.finish();

            return sb.toString();

        } catch (Exception e) {
            activity.finish();
            toast("친구등록에 실패했습니다.");
            return new String("Error: " + e.getMessage());
        }

    }
    public void toast(String msg){
        handler.post(new Runnable() {
            @Override
            public void run() {
                Toast.makeText(activity,msg , Toast.LENGTH_SHORT).show();
            }
        });
    }
}