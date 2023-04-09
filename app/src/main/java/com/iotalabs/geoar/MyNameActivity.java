package com.iotalabs.geoar;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.example.lotalabsappui.R;

public class MyNameActivity extends AppCompatActivity {
    private EditText nameEdit;
    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_name);

        nameEdit = (EditText)findViewById(R.id.myName);
    }
    public void mOnClick(View v){
        String name = nameEdit.getText().toString();
        if(name.trim().equals("")){
            Toast.makeText(this, "이름을 입력하세요.", Toast.LENGTH_SHORT).show();
        }
        else{
            SharedPreferences prefs = getSharedPreferences("person_name",0);
            SharedPreferences.Editor editor =prefs.edit();
            editor.putString("name",name);
            editor.apply();
            Intent intent = new Intent(MyNameActivity.this, MainActivity.class);
            startActivity(intent);
            finish();
        }
    }
}