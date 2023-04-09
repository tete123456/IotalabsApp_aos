package com.iotalabs.geoar;

import android.app.Application;
import android.location.Location;

public class Location_All extends Application {
    static Location Value;
// 타 class에서 Location_All class를 통해 해당 variable 값을 참조

    public Location getGlobalgValue(){
        return Value;
    }

// 타 class에서 변경한 valuable을 Location_All 에 저장

    public void setGlobalgValue(Location mValue){
        this.Value = mValue;
    }


}
