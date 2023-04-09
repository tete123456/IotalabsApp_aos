package com.iotalabs.geoar;

import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.widget.Toast;

import com.example.lotalabsappui.R;
import com.google.zxing.integration.android.IntentIntegrator;
import com.google.zxing.integration.android.IntentResult;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class ReadQRActivity extends AppCompatActivity {
    private IntentIntegrator qrScan;
    private DbOpenHelper mDbOpenHelper;
    private Cursor mCursor;
    private static String IP_ADDRESS;
    private GetFriendData getTask;
    private InsertFriendData task3;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_read_qractivity);
        IP_ADDRESS=Constants.IP_ADDRESS.toString();
        qrScan = new IntentIntegrator(this);
        qrScan.setOrientationLocked(false); // default가 세로모드인데 휴대폰 방향에 따라 가로, 세로로 자동 변경됩니다.
        qrScan.initiateScan();
    }
    @SuppressLint("Range")
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {//QR스캐너에서 돌아왔을 때
        IntentResult result = IntentIntegrator.parseActivityResult(requestCode, resultCode, data);
        String checkUUID="[a-zA-Z0-9]{8}-[a-zA-Z0-9]{4}-[a-zA-Z0-9]{4}-[a-zA-Z0-9]{4}-[a-zA-Z0-9]{12}";
        String uuidFriend;
        String nameFriend;
        if(result != null) {
            if(result.getContents() == null) {
                finish();
                //취소
            } else {//성공
                Pattern patten = Pattern.compile(checkUUID);
                Matcher matcher = patten.matcher(result.getContents());
                boolean regex= matcher.find();
                if(regex){//UUID형식인지 체크
                    uuidFriend = result.getContents().split("문자열나누기")[0];
                    nameFriend = result.getContents().split("문자열나누기")[1];
                    mDbOpenHelper = new DbOpenHelper(this);
                    mDbOpenHelper.open();
                    mCursor = null;
                    mCursor = mDbOpenHelper.getAllColumns();
                    boolean readyFriend = false;
                    while (mCursor.moveToNext()) {//데이터베이스에 등록된 UUID인지 체크
                        if(uuidFriend.equals(mCursor.getString(mCursor.getColumnIndex("UUID")))){
                            readyFriend =true;
                            break;
                        }
                    }
                    mDbOpenHelper.close();
                    if(readyFriend){
                        Toast.makeText(this, "이미 등록된 친구입니다.", Toast.LENGTH_LONG).show();
                        finish();
                    }
                    else{
                        task3 = new InsertFriendData(ReadQRActivity.this);
                        task3.execute("http://" + IP_ADDRESS + "/insertFriend.php", CreateQR.GetDeviceUUID(getApplicationContext()),
                                uuidFriend,null,null,nameFriend);
                        getTask= new GetFriendData(getApplicationContext());//친구 위치정보 받기
                        getTask.execute( "http://" + IP_ADDRESS + "/getMyFriend.php",CreateQR.GetDeviceUUID(getApplicationContext()));
                    }
                }
                else{
                    Toast.makeText(this, "친구추가 QR코드가 아닙니다." , Toast.LENGTH_LONG).show();
                    finish();
                }
            }
        } else {
            super.onActivityResult(requestCode, resultCode, data);
            finish();
        }

    }

}