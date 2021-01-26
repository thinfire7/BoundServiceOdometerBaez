package com.odometer;

import android.Manifest;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Binder;
import android.os.Bundle;
import android.os.IBinder;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;

import java.util.Random;

public class OdometerService extends Service {

    private final IBinder binder = new OdometerBinder();
    //private final Random random = new Random();
    private LocationListener listener;
    private LocationManager locManager;
    private static double distanceInMeters;
    private static Location lastLocation = null;
    public static final String PERMISSION_STRING = Manifest.permission.ACCESS_FINE_LOCATION;

    //LocationManager locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);

    //String provider = locationManager.getBestProvider(new Criteria(), true);

    public class OdometerBinder extends Binder{
        OdometerService getOdometer(){
            return OdometerService.this;
        }
    }

    @Override
    public void onCreate(){
        super.onCreate();
        listener = new LocationListener() {
            @Override
            public void onLocationChanged(Location location) {
                if(lastLocation == null){
                    lastLocation = location;
                }

                distanceInMeters += location.distanceTo(lastLocation);
                lastLocation = location;
            }

            @Override
            public void onProviderDisabled(String arg0){}

            @Override
            public void onProviderEnabled(String arg0){}

            @Override
            public void onStatusChanged(String arg0, int arg1, Bundle bundle){
            }
        };

        locManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        if(ContextCompat.checkSelfPermission(this, PERMISSION_STRING)
                == PackageManager.PERMISSION_GRANTED){
            String provider = locManager.getBestProvider(new Criteria(), true);
            if(provider != null){
                locManager.requestLocationUpdates(provider, 1000, 1, listener);
            }
        }

    }

    @Override
    public IBinder onBind(Intent intent) {
        return binder;
    }

    @Override
    public void onDestroy(){
        super.onDestroy();
        if(locManager != null && listener != null){
            if(ContextCompat.checkSelfPermission(this, PERMISSION_STRING)
                    == PackageManager.PERMISSION_GRANTED){
                locManager.removeUpdates(listener);
            }
            locManager = null;
            listener = null;
        }
    }

    public double getDistance(String estado) {
        if(estado== "millas"){
            System.out.println("millas");
            return this.distanceInMeters / 1609.344;
        }
        else{
            System.out.println("metros");
            return this.distanceInMeters;
        }
    }

}