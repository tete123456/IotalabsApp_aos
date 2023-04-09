package com.iotalabs.geoar;

import android.annotation.SuppressLint;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.Html;

import androidx.appcompat.app.AlertDialog;
import androidx.preference.Preference;
import androidx.preference.PreferenceFragmentCompat;
import androidx.preference.PreferenceManager;
import androidx.preference.SwitchPreference;

import com.example.lotalabsappui.R;

public class SettingFragment extends PreferenceFragmentCompat implements SharedPreferences.OnSharedPreferenceChangeListener{
    SharedPreferences prefs;

    SwitchPreference my_place;
    SwitchPreference fr_place;
    SwitchPreference hittmap;
    Preference exit;
    @Override
    public void onCreatePreferences(Bundle savedInstanceState, String rootKey) {
        setPreferencesFromResource(R.xml.root_preferences, rootKey);

        my_place = (SwitchPreference) findPreference("key_me");
        fr_place = (SwitchPreference) findPreference("key_friend");
        hittmap = (SwitchPreference) findPreference("key_add_hitt");
        prefs = PreferenceManager.getDefaultSharedPreferences(getActivity());

        exit =(Preference)findPreference("exit");
        exit.setOnPreferenceClickListener(preference -> {
            AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
            builder.setTitle("어플을 종료하시겠습니까?");
            builder.setMessage(Html.fromHtml("어플이 재실행되기 전까지 백그라운드 서비스를 이용할 수 없습니다."));
            builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    Intent delFromInpo = new Intent();
                    ((MainActivity)getActivity()).service_stop();
                    getActivity().finish();
                }
            });
            builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                }
            });
            builder.create().show();
            return true;
        });

    }
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public void onResume() {
        final SharedPreferences sh = getPreferenceManager().getSharedPreferences() ;
        super.onResume();
        sh.registerOnSharedPreferenceChangeListener(this);

    }

    @Override
    public void onPause() {
        final SharedPreferences sh = getPreferenceManager().getSharedPreferences() ;
        super.onPause();
        sh.unregisterOnSharedPreferenceChangeListener(this);
    }
    @SuppressLint("ResourceType")
    @Override
    public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String Key) {
        if(Key.equals("key_me")){
            boolean b = prefs.getBoolean("key_me", true);

        }
        else if(Key.equals("key_friend")){
            boolean b = prefs.getBoolean("key_friend", true);

        }
        else if(Key.equals("key_add_hitt")){
            boolean b = prefs.getBoolean("key_add_hitt", true);

        }

    }
}