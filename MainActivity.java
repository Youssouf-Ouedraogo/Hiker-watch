package com.example.hickerwatch;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Context;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Build;
import android.os.Bundle;
import android.os.TestLooperManager;
import android.util.Log;
import android.widget.TextView;

import java.io.IOException;
import java.util.List;
import java.util.Locale;

public class MainActivity extends AppCompatActivity {
    LocationManager locationManager;
    LocationListener locationListener;

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        if (grantResults.length >0 && grantResults[0] == PackageManager.PERMISSION_GRANTED){
           startListening();
        }
    }
    public  void  startListening (){
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED){
            locationManager = (LocationManager) this.getSystemService(Context.LOCATION_SERVICE);
        }
    }
    public void  updateLocationInfo(Location location){
        TextView lat = findViewById(R.id.latittude);
        TextView longitude = findViewById(R.id.longitude);
        TextView accuracy = findViewById(R.id.accuracy);
        TextView address = findViewById(R.id.address);
        TextView altitude = findViewById(R.id.altitude);

        lat.setText("Latitude: "+location.getLatitude());
        longitude.setText("Longitude: "+ location.getLongitude());
        altitude.setText("Altitude: "+ location.getAltitude());
        accuracy.setText("Accuracy: "+location.getAccuracy());

        Geocoder geocoder = new Geocoder (getApplicationContext(), Locale.getDefault());
        try {
            String addr ="Could not find an address";
            List<Address> list = geocoder.getFromLocation(location.getLatitude(), location.getLongitude(),1);

            if (list !=null && list.size()>0){
                addr = "";
                if(list.get(0).getSubThoroughfare() !=null){
                    addr += list.get(0).getSubThoroughfare() + " ";
                }
                if(list.get(0).getThoroughfare() !=null){
                    addr += list.get(0).getThoroughfare() + "\n";
                }
                if(list.get(0).getLocality() !=null){
                    addr += list.get(0).getLocality() + "\n";
                }
                if(list.get(0).getPostalCode() !=null){
                    addr += list.get(0).getPostalCode() + "\n";
                }
                if(list.get(0).getCountryName() !=null){
                    addr += list.get(0).getCountryName() + "\n";
                }

            }
            address.setText("Address: "+addr);
        } catch (IOException e) {
            e.printStackTrace();
        }


    }

    @SuppressLint("MissingPermission")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        locationManager = (LocationManager) this.getSystemService(Context.LOCATION_SERVICE);
        locationListener = new LocationListener() {
            @Override
            public void onLocationChanged(Location location) {
                updateLocationInfo(location);
            }

            @Override
            public void onStatusChanged(String s, int i, Bundle bundle) {

            }

            @Override
            public void onProviderEnabled(String s) {

            }

            @Override
            public void onProviderDisabled(String s) {

            }
        };
        if (Build.VERSION.SDK_INT <23){
            startListening();
        }
        else {
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED){
                ActivityCompat.requestPermissions(this,new String[]{Manifest.permission.ACCESS_FINE_LOCATION},1);
            }else{
                locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0,0, locationListener);
                Location location = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
                if (location !=null){
                    updateLocationInfo(location);
                }
            }
        }
    }
}
