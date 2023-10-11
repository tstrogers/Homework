package com.example.homework0;


import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import android.Manifest;
import android.content.IntentSender;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Bundle;
import android.os.Looper;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import com.google.android.gms.common.api.ResolvableApiException;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.Priority;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;

import java.text.MessageFormat;


public class MainActivity extends AppCompatActivity {
    private FusedLocationProviderClient locationClient;
    private LocationRequest locationRequest;
    TextView locationTextView;
    private double speedms = 0.0;
    private double speedmph = 0.0;
    //private boolean isOn = false;
    //private boolean askHelp = false;
    //private Button pauseBtn;
    //private Button helpBtn;
    //private LocationResult locationResult;
    private boolean testMode = false;
    private Button testButton;
    private double testLat =  42.3601;
    private double testLong = -71.0589;
    private double fakeSpeedMph = 10.0;

    private LocationCallback locationCallback = new LocationCallback() {
        @Override
        public void onLocationResult(LocationResult locationResult) {
            if (locationResult == null){
                return;
            }
            if (testMode == true) {
                double timeHours = 4.0 / 3600.0; //for update interval of 4 seconds
                double distanceMiles = fakeSpeedMph * timeHours; //how many miles traveled each update
                double changeLong = distanceMiles / 52.3; //52.3 is the approx. number of miles per 1 degree of longitude at latitude 42.3601
                double newLat = testLat;
                double newLong = testLong + changeLong;

                locationTextView.setText(MessageFormat.format("Lat: {0} Long: {1} Accuracy: Test Speed(mph): {2}", newLat, newLong, fakeSpeedMph));
            }
            else {
                for (Location location : locationResult.getLocations()) {
                    speedms = location.getSpeed();
                    speedmph = speedms * 2.23694;
                    locationTextView.setText(MessageFormat.format("Lat: {0} Long: {1} Accuracy: {2} Speed(mph): {3}", location.getLatitude(),
                            location.getLongitude(), location.getAccuracy(), speedmph));
                }
            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
//        pauseBtn = (Button)findViewById(R.id.button_pause);
//        pauseBtn.setOnClickListener(new View.OnClickListener(){
//            public void onClick(View view) {
//                if (isOn == false){
//                    onPause();
//                    locationTextView.setText("App is paused");
//                    isOn = true;
//                }
//                else if (isOn){
//                    startLocationUpdates();
//                    isOn = false;
//                }
//            }
//        });
//        helpBtn = (Button)findViewById(R.id.button_help);
//        helpBtn.setOnClickListener(new View.OnClickListener(){
//            public void onClick(View view) {
//                if (askHelp == false){
//                    onPause();
//                    locationTextView.setText("This app displays the the location of the phone along with the speed at which the phone is traveling. To pause the updates, please click the pause button. To resume updates please click the pause button again. Click the help button to exit this window");
//                    askHelp = true;
//                }
//                else if (askHelp){
//                    startLocationUpdates();
//                    askHelp = false;
//                }
//            }
//        });
        locationTextView = findViewById(R.id.location_text);
        locationClient = LocationServices.getFusedLocationProviderClient(this);
        locationRequest = new LocationRequest.Builder(Priority.PRIORITY_HIGH_ACCURACY, 1000)
                .setIntervalMillis(4000)
                .setMinUpdateIntervalMillis(2000)
                .build();

        testButton = (Button)findViewById(R.id.button_test);
        testButton.setOnClickListener(new View.OnClickListener(){
            public void onClick(View view) {
                testMode = !testMode;  // Toggle testMode
            }
        });

    }

    @Override
    protected void onStart() {
        super.onStart();
        if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            requestPermission();
            getLastLocation();
        }
    }
    @Override
    protected void onResume() {
        super.onResume();
        startLocationUpdates();
    }
    @Override
    protected void onPause() {
        super.onPause();
        stopLocationUpdates();

    }
    @Override
    protected void onStop(){
        super.onStop();
        stopLocationUpdates();
    }


    private void getLastLocation() {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            requestPermission();
            return;
        }
        Task<Location> locationTask = locationClient.getLastLocation();
        locationTask.addOnSuccessListener(new OnSuccessListener<Location>() {
            @Override
            public void onSuccess(Location location) {
                if (location != null) {
                    startLocationUpdates();
                } else {
                    getLastLocation();
                }
            }
        });
        locationTask.addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                if (e instanceof ResolvableApiException) {
                    ResolvableApiException apiException = (ResolvableApiException) e;
                    try {
                        apiException.startResolutionForResult(MainActivity.this, 1003);
                    } catch (IntentSender.SendIntentException ex) {
                        throw new RuntimeException(ex);
                    }
                }
            }
        });


    }

    private void startLocationUpdates() {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            requestPermission();
            return;
        }
        locationClient.requestLocationUpdates(locationRequest, locationCallback, Looper.getMainLooper());
    }
    private void stopLocationUpdates(){
        locationClient.removeLocationUpdates(locationCallback);

    }
    private void requestPermission(){
        ActivityCompat.requestPermissions(MainActivity.this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, 1);
        ActivityCompat.requestPermissions(MainActivity.this, new String[]{Manifest.permission.ACCESS_COARSE_LOCATION}, 2);

    }
}
//References:
//https://developer.android.com/reference/android/location/LocationRequest.Builder
//https://stackoverflow.com/questions/70874702/android-locationrequest-is-private
//https://www.youtube.com/watch?v=4eWoXPSpA5Y&list=PLdHg5T0SNpN3GBUmpGqjiKGMcBaRT2A-m&index=2
//https://www.youtube.com/watch?v=rNYaEFl6Fms&list=PLdHg5T0SNpN3GBUmpGqjiKGMcBaRT2A-m&index=1
//https://developer.android.com/training/location/change-location-settings
//https://stackoverflow.com/questions/64009427/how-can-i-get-continuous-location-updates-in-android
