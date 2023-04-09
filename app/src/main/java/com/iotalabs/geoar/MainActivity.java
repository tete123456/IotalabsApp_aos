package com.iotalabs.geoar;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.FrameLayout;
import android.widget.Toast;

import com.example.lotalabsappui.R;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.floatingactionbutton.ExtendedFloatingActionButton;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    private BottomNavigationView bottomNavigationView; // 바텀 네비게이션 뷰
    private FragmentManager fm;
    private FragmentTransaction ft;
    private MapFragment frag1;
    private ListFragment frag2;
    private SettingFragment frag3;
    private Animation fab_open, fab_close;
    private Boolean isFabOpen = false;
    private FrameLayout fab_layout;
    private ExtendedFloatingActionButton fab1, fab2;
    private FloatingActionButton fab;
    private long backKeyPressedTime = 0; //뒤로가기 버튼 눌렀던 시간 저장
    private Toast toast;//첫번째 뒤로가기 버튼을 누를때 표시하는 변수
    private final String TAG = "MainActivity";


    public Context context;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        context = this.getApplicationContext();

        startService(new Intent(this, BackgroundLocationUpdateService.class));


        //프래그먼트 설정
        fab_layout = findViewById(R.id.fab_layout);
        fab_layout.bringToFront();
        fab_open = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.fab_open);
        fab_close = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.fab_close);

        fab = (FloatingActionButton) findViewById(R.id.fab);//메인
        fab1 = (ExtendedFloatingActionButton) findViewById(R.id.fab1);//qr생성
        fab2 = (ExtendedFloatingActionButton) findViewById(R.id.fab2);//qr스캔

        fab.setOnClickListener(this);
        fab1.setOnClickListener(this);
        fab2.setOnClickListener(this);
        fab.bringToFront() ;
        bottomNavigationView = findViewById(R.id.bottomNavi);
        bottomNavigationView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener()
        {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem menuItem)
            {
                switch (menuItem.getItemId())
                {
                    case R.id.action_map:
                        setFrag(0);
                        break;
                    case R.id.action_list:
                        setFrag(1);
                        break;
                    case R.id.action_set:
                        setFrag(2);
                        break;
                }
                return true;
            }
        });
        frag1=new MapFragment();
        frag2=new ListFragment();
        frag3=new SettingFragment();
        setFrag(0); // 첫 프래그먼트 화면 지정

    }


    public void onClick(View v) {
        int id = v.getId();
        switch (id) {
            case R.id.fab://메인버튼
                anim();
                break;
            case R.id.fab1://qr생성
                anim();
                Intent intent = new Intent(MainActivity.this, CreateQR.class);
                startActivity(intent);
                break;
            case R.id.fab2://qr스캔
                anim();
                Intent intent2 = new Intent(MainActivity.this, ReadQRActivity.class);
                startActivity(intent2);
                break;
        }
    }
    public void service_start(){
        startService(new Intent(this, BackgroundLocationUpdateService.class));
    }
    //백그라운드 서비스 종료
    public void service_stop() {
        stopService(new Intent(this, BackgroundLocationUpdateService.class));
    }

    public void anim() {

        if (isFabOpen) {
            fab1.startAnimation(fab_close);
            fab2.startAnimation(fab_close);
            fab1.setClickable(false);
            fab2.setClickable(false);
            isFabOpen = false;
        } else {
            fab1.startAnimation(fab_open);
            fab2.startAnimation(fab_open);
            fab1.setClickable(true);
            fab2.setClickable(true);
            isFabOpen = true;
        }
    }

    // 프레그먼트 교체
    public void setFrag(int n)
    {
        fm = getSupportFragmentManager();
        ft= fm.beginTransaction();
        switch (n)
        {
            case 0:
                ft.replace(R.id.Main_Frame,frag1);
                ft.commit();
                break;

            case 1:
                ft.replace(R.id.Main_Frame,frag2);
                ft.commit();
                break;

            case 2:
                ft.replace(R.id.Main_Frame,frag3);
                ft.setReorderingAllowed(true);
                ft.addToBackStack(null);
                ft.commit();
                break;
        }
    }


    /* 뒤로가기 버튼 메소드*/
    public void onBackPressed(){
        //super.onBackPressed();
        //기존의 뒤로가기 버튼 기능 막기
        if (System.currentTimeMillis() > backKeyPressedTime + 2000) {
            backKeyPressedTime = System.currentTimeMillis();
            toast = Toast.makeText(this, "뒤로 버튼 한번더 누르시면 종료됩니다", Toast.LENGTH_SHORT);
            toast.show();
            return;
        }// 뒤로가기버튼을 한번누르면 현재시간값에 현재버튼누른시간 저장
        if (System.currentTimeMillis() <= backKeyPressedTime + 2000) {
            finish();
            toast.cancel();
        }//위에서 저장한 현재시간값에 2초안에 버튼을 한번 더 누르면 앱을 종료함.
    }

}