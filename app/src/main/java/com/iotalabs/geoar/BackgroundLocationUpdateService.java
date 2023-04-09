package com.iotalabs.geoar;
import android.annotation.SuppressLint;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.IntentSender;
import android.content.SharedPreferences;
import android.location.Location;
import android.location.LocationListener;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Looper;
import android.util.Log;
import android.widget.RemoteViews;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.NotificationCompat;

import com.example.lotalabsappui.R;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GoogleApiAvailability;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.ResolvableApiException;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.LocationSettingsRequest;
import com.google.android.gms.location.LocationSettingsResponse;
import com.google.android.gms.location.LocationSettingsStatusCodes;
import com.google.android.gms.location.SettingsClient;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.tasks.OnCanceledListener;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.messaging.FirebaseMessaging;
import com.google.maps.android.PolyUtil;

import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

/**
 * Created by Ketan Ramani on 05/11/18.
 */

public class BackgroundLocationUpdateService extends Service implements GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener, LocationListener {

    /* Declare in manifest
    <service android:name=".BackgroundLocationUpdateService"/>
    */

    private final String TAG = "BackgroundService";
    private final String TAG_LOCATION = "TAG_LOCATION";
    private Context context;
    private boolean stopService = false;

    /* For Google Fused API */
    protected GoogleApiClient mGoogleApiClient;
    protected LocationSettingsRequest mLocationSettingsRequest;
    private String UUID;
    private String str_latitude = "0.0", str_longitude = "0.0";
    private String all_g = "0.0", all_w = "0.0";

    private FusedLocationProviderClient mFusedLocationClient;
    private SettingsClient mSettingsClient;
    private LocationCallback mLocationCallback;
    private LocationRequest mLocationRequest;
    private Location mCurrentLocation;
    /* For Google Fused API */

    private static String IP_ADDRESS;
    private InsertData task;
    private GetData task2;
    private GetFriendData task3;
    private PushNoti task4;
    private InsertToken task5;
    private List<LatLng> p;
    private NotificationCompat.Builder builder;
    private boolean geo_check = false;//default를 false로 해야 어플 처음 시작할 때 밖에 있으면 알림이 안뜸



    public void getToken(){
        FirebaseMessaging.getInstance().getToken()
                .addOnCompleteListener(new OnCompleteListener<String>() {
                    @Override
                    public void onComplete(@NonNull Task<String> task) {
                        if (!task.isSuccessful()) {
                            Log.w(TAG, "Fetching FCM registration token failed", task.getException());
                            return;
                        }
                        String token = task.getResult();
                        task5= new InsertToken();//
                        task5.execute( "http://" + IP_ADDRESS + "/insertToken.php",CreateQR.GetDeviceUUID(getApplicationContext()),token);
                    }
                });
    }
    @Override
    public void onCreate() {
        super.onCreate();
        IP_ADDRESS=Constants.IP_ADDRESS.toString();
        context = this;
        UUID= CreateQR.GetDeviceUUID(context);
        p = new ArrayList<>();
        p.add(new LatLng(37.2104, 126.9528));
        p.add( new LatLng(37.2107, 126.9534));
        p.add( new LatLng(37.2116, 126.9534));
        p.add( new LatLng(37.2126, 126.9542));
        p.add( new LatLng(37.2140, 126.9543));
        p.add( new LatLng(37.2151, 126.9526));
        p.add( new LatLng(37.2149, 126.9517));
        p.add( new LatLng(37.2143, 126.9517));
        p.add( new LatLng(37.2132, 126.9503));
        p.add( new LatLng(37.2122, 126.9495));
        p.add( new LatLng(37.2111, 126.9504));
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        StartForeground();
        final Handler handler = new Handler();
        final Runnable runnable = new Runnable() {

            @Override
            public void run() {
                try {
                    if (!stopService) {
                        //Perform your task here
                        task = new InsertData();//내 위치정보 보내기
                        task.execute("http://" + IP_ADDRESS + "/insert.php", UUID,str_latitude,str_longitude);//서버에 전송
                        task2= new GetData(context);//모든 사용자 위치정보 받기
                        task2.execute( "http://" + IP_ADDRESS + "/getjson.php", "");
                        task3= new GetFriendData(context);//친구 위치정보 받기
                        task3.execute( "http://" + IP_ADDRESS + "/getMyFriend.php",UUID);
                        getToken();
                        boolean inside= PolyUtil.containsLocation(new LatLng(Double.parseDouble(str_latitude),
                                Double.parseDouble(str_longitude)),p,true);
                        if(inside){
                            geo_check = true;
                        }else{
                            if(geo_check==true){
                                String title="지정영역 벗어남 알림";
                                String msg="지정영역을 벗어났습니다.";
                                //나한테 노티 띄우기
                                Intent intent = new Intent(context, BackgroundLocationUpdateService.class);
                                String channel_id = "getOutArea";
                                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                                PendingIntent pendingIntent = PendingIntent.getActivity(context, 0, intent, PendingIntent.FLAG_ONE_SHOT);
                                Uri uri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
                                NotificationCompat.Builder builder = new NotificationCompat.Builder(getApplicationContext(), channel_id)
                                        .setSmallIcon(R.mipmap.iotalabs_app_icon)
                                        .setSound(uri)
                                        .setAutoCancel(true)
                                        .setVibrate(new long[] {1000, 1000, 1000, 1000, 1000})
                                        .setOnlyAlertOnce(true)
                                        .setContentIntent(pendingIntent);
                                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN)
                                {
                                    builder = builder.setContent(getCustomDesign(title, msg));
                                }
                                else
                                {
                                    builder = builder.setContentTitle(title)
                                            .setContentText(msg)
                                            .setSmallIcon(R.mipmap.iotalabs_app_icon);
                                }
                                NotificationManager notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
                                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O)
                                {
                                    NotificationChannel notificationChannel = new NotificationChannel(channel_id, "locationGetOut", NotificationManager.IMPORTANCE_HIGH);
                                    notificationChannel.setSound(uri, null);
                                    notificationManager.createNotificationChannel(notificationChannel);
                                }
                                notificationManager.notify(0, builder.build());
                                SharedPreferences prefs = getSharedPreferences("person_name",0);
                                String name = prefs.getString("name","");
                                task4=new PushNoti();//생성
                                task4.execute("http://" + IP_ADDRESS + "/push.php", UUID,name);//친구에게 노티보냄
                                geo_check=false;
                            }
                        }
                    }
                } catch (Exception e) {
                    e.printStackTrace();

                } finally {
                    if (!stopService) {
                        handler.postDelayed(this, TimeUnit.SECONDS.toMillis(10));
                    }
                }
            }
        };

        handler.postDelayed(runnable, 5000);
        buildGoogleApiClient();

        return START_STICKY;
    }
    private RemoteViews getCustomDesign(String title, String message)
    {
        @SuppressLint("RemoteViewLayout") RemoteViews remoteViews = new RemoteViews(getApplicationContext().getPackageName(), R.layout.notification);
        remoteViews.setTextViewText(R.id.noti_title, title);
        remoteViews.setTextViewText(R.id.noti_message, message);
        remoteViews.setImageViewResource(R.id.noti_icon, R.mipmap.iotalabs_app_icon);
        return remoteViews;
    }
    @Override
    public void onDestroy() {
        Log.e(TAG, "Service Stopped");
        stopService = true;
        if (mFusedLocationClient != null) {
            mFusedLocationClient.removeLocationUpdates(mLocationCallback);
            Log.e(TAG_LOCATION, "Location Update Callback Removed");
        }
        super.onDestroy();
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    private void StartForeground() {
        Intent intent = new Intent(context, MainActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0 /* Request code */, intent, PendingIntent.FLAG_ONE_SHOT);

        String CHANNEL_ID = "channel_location";
        String CHANNEL_NAME = "channel_location";

        NotificationCompat.Builder builder = null;
        NotificationManager notificationManager = (NotificationManager) getApplicationContext().getSystemService(Context.NOTIFICATION_SERVICE);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel channel = new NotificationChannel(CHANNEL_ID, CHANNEL_NAME, NotificationManager.IMPORTANCE_LOW);
            channel.setLockscreenVisibility(Notification.VISIBILITY_PRIVATE);
            notificationManager.createNotificationChannel(channel);
            builder = new NotificationCompat.Builder(getApplicationContext(), CHANNEL_ID);
            builder.setChannelId(CHANNEL_ID);
            builder.setBadgeIconType(NotificationCompat.BADGE_ICON_NONE);
        } else {
            builder = new NotificationCompat.Builder(getApplicationContext(), CHANNEL_ID);
        }
        builder.setContentTitle("IotalabsApp");
        builder.setContentText("위치 정보 사용중");
        builder.setAutoCancel(true);
        builder.setSmallIcon(R.mipmap.iotalabs_app_icon);
        builder.setContentIntent(pendingIntent);
        Notification notification = builder.build();
        startForeground(101, notification);
    }

    @Override
    public void onLocationChanged(Location location) {
        Log.e(TAG_LOCATION, "Location Changed Latitude : " + location.getLatitude() + "\tLongitude : " + location.getLongitude());
        ///
        Location_All all = (Location_All) getApplication();
        all.setGlobalgValue(location);
        ///
        str_latitude = String.valueOf(location.getLatitude());
        str_longitude = String.valueOf(location.getLongitude());

        if (str_latitude.equalsIgnoreCase("0.0") && str_longitude.equalsIgnoreCase("0.0")) {
            requestLocationUpdate();
        } else {
            Log.e(TAG_LOCATION, "Latitude : " + location.getLatitude() + "\tLongitude : " + location.getLongitude());
        }
    }

    @Override
    public void onStatusChanged(String provider, int status, Bundle extras) {

    }

    @Override
    public void onProviderEnabled(String provider) {

    }

    @Override
    public void onProviderDisabled(String provider) {

    }

    @Override
    public void onConnected(@Nullable Bundle bundle) {
        mLocationRequest = new LocationRequest();
        mLocationRequest.setInterval(10 * 1000);
        mLocationRequest.setFastestInterval(5 * 1000);
        mLocationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);

        LocationSettingsRequest.Builder builder = new LocationSettingsRequest.Builder();
        builder.addLocationRequest(mLocationRequest);
        builder.setAlwaysShow(true);
        mLocationSettingsRequest = builder.build();

        mSettingsClient
                .checkLocationSettings(mLocationSettingsRequest)
                .addOnSuccessListener(new OnSuccessListener<LocationSettingsResponse>() {
                    @Override
                    public void onSuccess(LocationSettingsResponse locationSettingsResponse) {
                        Log.e(TAG_LOCATION, "GPS Success");
                        requestLocationUpdate();
                    }
                }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                int statusCode = ((ApiException) e).getStatusCode();
                switch (statusCode) {
                    case LocationSettingsStatusCodes.RESOLUTION_REQUIRED:
                        try {
                            int REQUEST_CHECK_SETTINGS = 214;
                            ResolvableApiException rae = (ResolvableApiException) e;
                            rae.startResolutionForResult((AppCompatActivity) context, REQUEST_CHECK_SETTINGS);
                        } catch (IntentSender.SendIntentException sie) {
                            Log.e(TAG_LOCATION, "Unable to execute request.");
                        }
                        break;
                    case LocationSettingsStatusCodes.SETTINGS_CHANGE_UNAVAILABLE:
                        Log.e(TAG_LOCATION, "Location settings are inadequate, and cannot be fixed here. Fix in Settings.");
                }
            }
        }).addOnCanceledListener(new OnCanceledListener() {
            @Override
            public void onCanceled() {
                Log.e(TAG_LOCATION, "checkLocationSettings -> onCanceled");
            }
        });
    }

    @Override
    public void onConnectionSuspended(int i) {
        connectGoogleClient();
    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
        buildGoogleApiClient();
    }

    protected synchronized void buildGoogleApiClient() {
        mFusedLocationClient = LocationServices.getFusedLocationProviderClient(context);
        mSettingsClient = LocationServices.getSettingsClient(context);

        mGoogleApiClient = new GoogleApiClient.Builder(context)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .addApi(LocationServices.API)
                .build();

        connectGoogleClient();

        mLocationCallback = new LocationCallback() {
            @Override
            public void onLocationResult(LocationResult locationResult) {
                super.onLocationResult(locationResult);
                Log.e(TAG_LOCATION, "Location Received");
                mCurrentLocation = locationResult.getLastLocation();
                onLocationChanged(mCurrentLocation);
            }
        };
    }

    private void connectGoogleClient() {
        GoogleApiAvailability googleAPI = GoogleApiAvailability.getInstance();
        int resultCode = googleAPI.isGooglePlayServicesAvailable(context);
        if (resultCode == ConnectionResult.SUCCESS) {
            mGoogleApiClient.connect();
        }
    }

    @SuppressLint("MissingPermission")
    private void requestLocationUpdate() {
        mFusedLocationClient.requestLocationUpdates(mLocationRequest, mLocationCallback, Looper.myLooper());
    }
}