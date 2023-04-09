package com.iotalabs.geoar;


import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.Fragment;
import androidx.preference.PreferenceManager;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.example.lotalabsappui.R;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.Polygon;
import com.google.android.gms.maps.model.PolygonOptions;
import com.google.android.gms.maps.model.TileOverlay;
import com.google.android.gms.maps.model.TileOverlayOptions;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.maps.android.heatmaps.Gradient;
import com.google.maps.android.heatmaps.HeatmapTileProvider;

import java.util.ArrayList;
import java.util.List;


public class MapFragment extends Fragment  implements OnMapReadyCallback  {
    private static final String TAG = "MapsFragment";
    private GoogleMap mMap;
    private MapView mapView = null;
    private DbOpenHelper mDbOpenHelper;
    private Cursor mCursor;
    private Cursor friendCursor;
    private FloatingActionButton fb_reroad;
    private FloatingActionButton fb_ar;

    List<LatLng> latLngs;
    private int[] colors = {
            Color.rgb(102, 225, 0), // green
            Color.rgb(255, 0, 0)    // red
    };
    SharedPreferences prefs;

    private float[] startPoints = {0.2f, 1f};
    private Gradient gradient;
    private HeatmapTileProvider provider;
    private TileOverlay overlay;

    public MapFragment() {
        // required
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        prefs = PreferenceManager.getDefaultSharedPreferences(getActivity());
    }

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View layout = inflater.inflate(R.layout.fragment_map, container, false);
        fb_reroad = layout.findViewById(R.id.fb_reroad);
        fb_reroad.setOnClickListener(new View.OnClickListener() {  //플로팅 버튼 이벤트
            @Override
            public void onClick(View v) {
                int id = v.getId();
                switch (id) {
                    case R.id.fb_reroad:
                        reloadMap(); // 새로고침 버튼 이벤트 !
                        break;
            }
        }});

        ///////////////////////////////////////////////////////////////////////////////////////
        fb_ar = layout.findViewById(R.id.fb_AR);
        fb_ar.setOnClickListener(new View.OnClickListener() {  //플로팅 버튼 이벤트
            @Override
            public void onClick(View v) {
                int id = v.getId();
                switch (id) {
                    case R.id.fb_AR:
                        // AR뷰 이벤트!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!
                        Intent intent = new Intent(getActivity(), AR_Activity.class);
                        startActivity(intent);
                        break;
                }
            }});
        ////////////////////////////////////////////////////////////////////////////////////////


        mapView = (MapView) layout.findViewById(R.id.map);
        mapView.getMapAsync(this);
        if (mapView != null) {
            mapView.onCreate(savedInstanceState);
        }

        return layout;
    }

    @Override
    public void onStart() {
        super.onStart();
        mapView.onStart();
    }

    @Override
    public void onStop() {
        super.onStop();
        mapView.onStop();
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        mapView.onSaveInstanceState(outState);
    }

    @Override
    public void onResume() {
        super.onResume();
        mapView.onResume();
    }

    @Override
    public void onPause() {
        super.onPause();
        mapView.onPause();
    }

    @Override
    public void onLowMemory() {
        super.onLowMemory();
        mapView.onLowMemory();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        mapView.onLowMemory();
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        mMap.clear();
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(34.928825,127.498833), 14));//순천국가정원 이동

        createMyLocation();//내위치만들기
        createFriendMarker();//친구마커 만들기
        createHitMap();//히트맵만들기
        createPloy();
    }

    public void createMyLocation(){//내위치만들기
        if (ActivityCompat.checkSelfPermission(getContext(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED
                && ActivityCompat.checkSelfPermission(getContext(), Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            Toast.makeText(getActivity(), "위치권한을 허용해주세요.", Toast.LENGTH_LONG).show();
            mMap.setMyLocationEnabled(false);
        } else {
            if(prefs.getBoolean("key_me", true)){//세팅에서 온상태면(내위치)
                mMap.setMyLocationEnabled(true);//내위치 만듬
            }
        }
    }
    @SuppressLint("Range")
    public void createFriendMarker(){//친구마커 만들기
        if(prefs.getBoolean("key_friend", true)) {//세팅에서 온상태면(친구위치)
            try {
                mDbOpenHelper = new DbOpenHelper(getActivity());
                mDbOpenHelper.open();
                friendCursor = null;
                friendCursor = mDbOpenHelper.getAllColumns3();

                //마커 크기 및 아이콘 생성
                int height = 110;
                int width = 110;
                BitmapDrawable bitmapdraw1=(BitmapDrawable)getResources().getDrawable(R.drawable.mapmarker);
                Bitmap b=bitmapdraw1.getBitmap();
                Bitmap friend_lMarker = Bitmap.createScaledBitmap(b, width, height, false);
                ////

                while (friendCursor.moveToNext()) {
                    MarkerOptions makerOptions = new MarkerOptions();
                    makerOptions
                            .position(new LatLng(
                                    Double.parseDouble(friendCursor.getString(friendCursor.getColumnIndex("str_latitude"))),
                                    Double.parseDouble(friendCursor.getString(friendCursor.getColumnIndex("str_longitude")))
                                    )
                            )
                            .title(friendCursor.getString(friendCursor.getColumnIndex("name")))// 타이틀.
                            .icon(BitmapDescriptorFactory.fromBitmap(friend_lMarker));
                    // 2. 마커 생성 (마커를 나타냄)
                    mMap.addMarker(makerOptions);
                }
                friendCursor.close();
                mDbOpenHelper.close();
            } catch (Exception e) {
            }
        }
    }
    @SuppressLint("Range")
    public void createHitMap(){//히트맵만들기
        if(prefs.getBoolean("key_add_hitt", true)) {//세팅에서 온상태면(히트맵)
            try {
                latLngs = new ArrayList<>();
                mDbOpenHelper = new DbOpenHelper(getActivity());
                mDbOpenHelper.open();
                latLngs.clear();
                mCursor = null;
                mCursor = mDbOpenHelper.getAllColumns2();
                while (mCursor.moveToNext()) {
                    if (!(mCursor.getString(mCursor.getColumnIndex("UUID")).equals(CreateQR.GetDeviceUUID(getContext())))) {
                        latLngs.add(
                                new LatLng(Double.parseDouble(mCursor.getString(mCursor.getColumnIndex("str_latitude"))),
                                        Double.parseDouble(mCursor.getString(mCursor.getColumnIndex("str_longitude")))));
                    }
                }
                mCursor.close();
                mDbOpenHelper.close();

                gradient = new Gradient(colors, startPoints);
                provider = new HeatmapTileProvider.Builder().data(latLngs).gradient(gradient).build();
                overlay = mMap.addTileOverlay(new TileOverlayOptions().tileProvider(provider));//히트맵 만듬
            } catch (Exception e) {

            }
        }
    }
    public void createPloy(){
        Polygon polygon = mMap.addPolygon(new PolygonOptions()
                .clickable(true)
                .strokeColor(Color.RED)
                .strokeWidth(5)
                .add(
                        new LatLng(37.2104, 126.9528),
                        new LatLng(37.2107, 126.9534),
                        new LatLng(37.2116, 126.9534),
                        new LatLng(37.2126, 126.9542),
                        new LatLng(37.2140, 126.9543),
                        new LatLng(37.2151, 126.9526),
                        new LatLng(37.2149, 126.9517),
                        new LatLng(37.2143, 126.9517),
                        new LatLng(37.2132, 126.9503),
                        new LatLng(37.2122, 126.9495),
                        new LatLng(37.2111, 126.9504)
                     ));
    }
    public void reloadMap(){
        mMap.clear();
        createMyLocation();
        createFriendMarker();
        createHitMap();
        createPloy();
    }
}