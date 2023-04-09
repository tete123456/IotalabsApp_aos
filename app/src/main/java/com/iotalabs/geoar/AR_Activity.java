package com.iotalabs.geoar;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import com.example.lotalabsappui.R;
import com.google.android.material.floatingactionbutton.ExtendedFloatingActionButton;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

public class AR_Activity extends AppCompatActivity implements View.OnClickListener {

    private FloatingActionButton fb_cancle;
    private FloatingActionButton fb_capturer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ar);

        fb_capturer = findViewById(R.id.fb_capturer);
        fb_cancle = findViewById(R.id.fb_cancle);


        fb_capturer = (FloatingActionButton) findViewById(R.id.fb_capturer);
        fb_cancle = (FloatingActionButton) findViewById(R.id.fb_cancle);

        fb_capturer.setOnClickListener(this);
        fb_cancle.setOnClickListener(this);

        fb_capturer.bringToFront() ;
        fb_cancle.bringToFront() ;

    }

    public void onClick(View v) {
        int id = v.getId();
        switch (id) {
            case R.id.fb_capturer://메인버튼
                // 켑 이벤트!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!
                Toast.makeText(getApplicationContext(),"캡쳐",Toast.LENGTH_SHORT).show();
                System.out.println("캡쳐이벤트캡쳐이벤트캡쳐이벤트캡쳐이벤트캡쳐이벤트캡쳐이벤트캡쳐이벤트캡쳐이벤트캡쳐이벤트캡쳐이벤트");
                break;
            case R.id.fb_cancle://qr생성
                // 취소버튼 이벤트!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!
                Toast.makeText(getApplicationContext(),"끄기",Toast.LENGTH_SHORT).show();
                System.out.println("취소취소취소취소취소취소취소취소취소취소취소취소취소취소취소취소취소취소취소취소취소취소취소취소취소취소취소");
                break;
        }
    }
}