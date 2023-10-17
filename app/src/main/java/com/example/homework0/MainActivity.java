package com.example.homework0;


import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import android.Manifest;
import android.app.AlertDialog;
import android.content.DialogInterface;
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
import com.jjoe64.graphview.GraphView;
import com.jjoe64.graphview.series.DataPoint;
import com.jjoe64.graphview.series.LineGraphSeries;


import java.text.MessageFormat;
import java.util.Timer;
import java.util.TimerTask;


public class MainActivity extends AppCompatActivity {
    private FusedLocationProviderClient locationClient;
    private LocationRequest locationRequest;
    TextView locationTextView;
    TextView toolbarTextView;
    TextView altitudeText;
    private double speedms = 0.0;
    private double speedmph = 0.0;
    private boolean isOn = false;
    private boolean askHelp = false;
    private Button pauseBtn;
    private Button helpBtn;
    private Button resetBtn;
    private Button graphBtn;
    //boolean timeStarted = false;
    Timer timer;
    TimerTask timerTask;
    Double time = 0.0;
    GraphView lineGraph;
    //private LocationResult locationResult;
    private LocationCallback locationCallback = new LocationCallback() {
        @Override
        public void onLocationResult(LocationResult locationResult) {
            if (locationResult == null){
                return;
            }
            for(Location location: locationResult.getLocations()){
                speedms = location.getSpeed();
                speedmph = speedms * 2.23694;
                locationTextView.setText(MessageFormat.format("Lat: {0} Long: {1} Accuracy: {2} Speed(mph): {3} Altitude: {4}", location.getLatitude(),
                        location.getLongitude(), location.getAccuracy(), speedmph, location.getAltitude()));
                //altitudeText.setText(MessageFormat.format("{0}", location.getAltitude()));
            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        initControl();
        initControlListener();
        //timeStarted = true;
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
    private void pauseButton(){
        if (isOn == false){
            onPause();
            locationTextView.setText("App is paused");
            isOn = true;
        }
        else if (isOn){
            startLocationUpdates();
            isOn = false;
        }
    }
    private void helpButton(){
        if (askHelp == false){
            onPause();
            locationTextView.setText("This app displays the the location of the phone along with the speed at which the phone is traveling. To pause the updates, please click the pause button. To resume updates please click the pause button again. Click the help button to exit this window");
            askHelp = true;
        }
        else if (askHelp){
            startLocationUpdates();
            askHelp = false;
        }
    }

    private void resetButton(){
        if(timerTask != null){
            timerTask.cancel();
            toolbarTextView.setText(formatTime(0,0,0));
            time = 0.0;
            startTimer();
        }

//        AlertDialog.Builder resetAlert = new AlertDialog.Builder(MainActivity.this);
//        resetAlert.setTitle("Reset Timer");
//        resetAlert.setMessage("Are you sure you want to reset the timer?");
//        resetAlert.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
//            @Override
//            public void onClick(DialogInterface dialog, int which) {
//                if(timerTask != null){
//                    timerTask.cancel();
//                    toolbarTextView.setText(formatTime(0,0,0));
//                    //startTimer();
//                }
//
//
//            }
//        });
//        resetAlert.setNegativeButton("No", new DialogInterface.OnClickListener() {
//            @Override
//            public void onClick(DialogInterface dialog, int which) {
//                //Nothing happens
//            }
//        });
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
    private void startTimer()
    {
        timerTask = new TimerTask()
        {
            @Override
            public void run()
            {
                runOnUiThread(new Runnable()
                {
                    @Override
                    public void run()
                    {
                        time++;
                        toolbarTextView.setText(getTimerText());
                    }
                });
            }

        };
        timer.scheduleAtFixedRate(timerTask, 0 ,1000);
    }
    private String getTimerText(){
        int rounded = (int) Math.round(time);
        int seconds = ((rounded%86400)%3600)%60;
        int minutes = ((rounded%86400)%3600)/60;
        int hours = ((rounded%86400)/3600);

        return formatTime(seconds,minutes,hours);

    }
    private String formatTime(int seconds, int minutes, int hours){
        return String.format("%02d",hours) + ":" + String.format("%02d",minutes)+":"+String.format("%02d",seconds);
    }
//    private void showGraph(){
//
//    }

    private void initControl(){
//        lineGraph = findViewById(R.id.idGraphView);
//        LineGraphSeries<DataPoint> series = new LineGraphSeries<DataPoint>(new DataPoint[]{
//        });
//        lineGraph.setTitle("Latitude vs Time");
        //graphBtn = (Button)findViewById(R.id.button_graph);
        pauseBtn = (Button)findViewById(R.id.button_pause);
        helpBtn = (Button)findViewById(R.id.button_help);
        resetBtn = (Button)findViewById(R.id.button_reset);
        toolbarTextView = findViewById(R.id.toolbar_time_value);
        locationTextView = findViewById(R.id.location_text);
        altitudeText = findViewById(R.id.altitudevalue);
        locationClient = LocationServices.getFusedLocationProviderClient(this);
        locationRequest = new LocationRequest.Builder(Priority.PRIORITY_HIGH_ACCURACY, 1000)
                .setIntervalMillis(4000)
                .setMinUpdateIntervalMillis(2000)
                .build();
        timer = new Timer();
        startTimer();


    }

    private void initControlListener(){
        pauseBtn.setOnClickListener(new View.OnClickListener(){
            public void onClick(View view) {
                pauseButton();
            }
        });
        helpBtn.setOnClickListener(new View.OnClickListener(){
            public void onClick(View view) {
                helpButton();
            }
        });

        resetBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) { resetButton();

            }
        });
//        graphBtn.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) { showGraph();
//
//            }
//        });



    }
}
//References:
//https://developer.android.com/reference/android/location/LocationRequest.Builder
//https://stackoverflow.com/questions/70874702/android-locationrequest-is-private
//https://www.youtube.com/watch?v=4eWoXPSpA5Y&list=PLdHg5T0SNpN3GBUmpGqjiKGMcBaRT2A-m&index=2
//https://www.youtube.com/watch?v=rNYaEFl6Fms&list=PLdHg5T0SNpN3GBUmpGqjiKGMcBaRT2A-m&index=1
//https://developer.android.com/training/location/change-location-settings
//https://stackoverflow.com/questions/64009427/how-can-i-get-continuous-location-updates-in-android
//https://www.youtube.com/watch?v=7QVr5SgpVog
