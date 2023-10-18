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
import com.jjoe64.graphview.GraphView;


import java.text.MessageFormat;
import java.util.Timer;
import java.util.TimerTask;


public class MainActivity extends AppCompatActivity {
    private FusedLocationProviderClient locationClient;
    private LocationRequest locationRequest;
    TextView locationTextView;
    TextView speedTextView;
    TextView latTextView;
    TextView lonTextView;
    TextView accTextView;
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
    private boolean testMode = false;
    private boolean kmMode = false;
    private Button testButton;
    private Button unitsButton;
    private Button distanceButton;
    private Button speedButton;
    private Button timeButton;
    private String unitsSpeed = "mph";
    private String unitsTime = "seconds";
    private String unitsDistance = "meters";
    private double testLat =  42.3601;
    private double prevTestLat = 42.3601;
    private double testLong = -71.0589;
    private double prevTestLong = -71.0589;
    private Location testLocation;
    private double fakeSpeedMph = 10.0;
    private double speedResult = 0;
    private Location prevLocation = null;
    private double totalDistanceMeters = 0.0;
    private TextView distanceTextView;


    private LocationCallback locationCallback = new LocationCallback() {
        @Override
        public void onLocationResult(LocationResult locationResult) {
            if (locationResult == null){
                return;
            }
            if (testMode == true) {
                prevLocation.setLatitude(testLocation.getLatitude());
                prevLocation.setLongitude(testLocation.getLatitude());
                double timeHours = 4.0 / 3600.0; //for update interval of 4 seconds
                double distanceMiles = fakeSpeedMph * timeHours; //how many miles traveled each update
                double changeLong = distanceMiles / 51.05; //approx. number of miles per 1 degree of longitude at latitude 42.3601
                testLocation.setLongitude(prevLocation.getLongitude() + changeLong);
                testLocation.setLatitude(prevLocation.getLatitude() + 0.0); //for testing of calculations and color coding
                double calculatedSpeed = testLocation.getSpeed();
                if (unitsSpeed == "kmph") {
                    calculatedSpeed *= 1.60934;
                }
                else if (unitsSpeed == "mph"){
                    calculatedSpeed *= 2.23694;
                }
                else if (unitsSpeed == "m/s"){
                    calculatedSpeed *= 1;
                }
                else if (unitsSpeed == "miles/minute"){
                    calculatedSpeed *= 2.23694;
                    calculatedSpeed *= 60;
                }

                //calculate distance between prevLocation and testLocation and add to totalDistanceMeters
                float[] results = new float[1];
                Location.distanceBetween(prevLocation.getLatitude(), prevLocation.getLongitude(), testLocation.getLatitude(), testLocation.getLongitude(), results);
                totalDistanceMeters += results[0];

                latTextView.setText(MessageFormat.format("Lat: {0}", testLocation.getLatitude()));
                lonTextView.setText(MessageFormat.format("Lon: {0}", testLocation.getLongitude()));
                accTextView.setText("Accuracy: Test");
                speedTextView.setText(MessageFormat.format("Speed: {0} {1}", calculatedSpeed, unitsSpeed));
                altitudeText.setText(MessageFormat.format("Altitude: {0} {1}", testLocation.getAltitude()));

                if (calculatedSpeed < 5) {
                    speedTextView.setTextColor(getResources().getColor(R.color.slow_speed, null));
                } else if (calculatedSpeed >= 5 && calculatedSpeed < 20) {
                    speedTextView.setTextColor(getResources().getColor(R.color.medium_speed, null));
                } else {
                    speedTextView.setTextColor(getResources().getColor(R.color.fast_speed, null));
                }
            }
            else {
                for (Location location : locationResult.getLocations()) {
                speedms = location.getSpeed();
                double calculatedSpeed = speedms;
                    if (unitsSpeed == "kmph") {
                        calculatedSpeed *= 1.60934;
                    }
                    else if (unitsSpeed == "mph"){
                        calculatedSpeed *= 2.23694;
                    }
                    else if (unitsSpeed == "miles/minute"){
                        calculatedSpeed *= 2.23694;
                        calculatedSpeed *= 60;
                    }

                    double altitude = location.getAltitude();
                    if (unitsDistance == "km"){
                        altitude /= 1000;
                    } else if (unitsDistance == "miles"){
                        altitude /= 1609.34;
                    } else if (unitsDistance == "feet"){
                        altitude /= 0.3048;
                    }
                    latTextView.setText(MessageFormat.format("Lat: {0}", location.getLatitude()));
                    lonTextView.setText(MessageFormat.format("Lon: {0}", location.getLongitude()));
                    accTextView.setText(MessageFormat.format("Accuracy: {0}", location.getAccuracy()));
                    speedTextView.setText(MessageFormat.format("Speed: {0} {1}", speedResult, unitsSpeed));
                    altitudeText.setText(MessageFormat.format("Altitude: {0} {1}", location.getAltitude(), unitsDistance));
                    if (prevLocation != null) {
                        float[] results = new float[1];
                        Location.distanceBetween(prevLocation.getLatitude(), prevLocation.getLongitude(), locationResult.getLastLocation().getLatitude(), locationResult.getLastLocation().getLongitude(), results);
                        totalDistanceMeters += results[0];
                    }
                    prevLocation = location;
                }
                if (speedmph < 5) {
                    speedTextView.setTextColor(getResources().getColor(R.color.slow_speed, null));
                } else if (speedmph >= 5 && speedmph < 20) {
                    speedTextView.setTextColor(getResources().getColor(R.color.medium_speed, null));
                } else {
                    speedTextView.setTextColor(getResources().getColor(R.color.fast_speed, null));
                }
            }
            //display total distance traveled in selected units
            double totalDistance = totalDistanceMeters;
            if (unitsDistance == "km"){
                totalDistance /= 1000;
            } else if (unitsDistance == "miles"){
                totalDistance /= 1609.34;
            } else if (unitsDistance == "feet"){
                totalDistance /= 0.3048;
            }

            distanceTextView.setText(MessageFormat.format("Distance: {0} {1}", totalDistance, unitsDistance));
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

    private void setUnitsSpeed(){
        if (unitsSpeed == "kmph"){
            unitsSpeed = "mph";
        }
        else if (unitsSpeed == "mph"){
            unitsSpeed = "m/s";
        }
        else if (unitsSpeed == "m/s"){
            unitsSpeed = "miles/minute";
        }
        else if (unitsSpeed == "miles/minute"){
            unitsSpeed = "kmph";
        }
    }
    private void setUnitsTime(){
        if (unitsTime == "seconds"){
            unitsTime = "minutes";
        }
        else if (unitsTime == "minutes"){
            unitsTime = "hours";
        }
        else if (unitsTime == "hours"){
            unitsTime = "days";
        }
        else if (unitsTime == "days"){
            unitsTime = "seconds";
        }
    }
    private void setUnitsDistance(){
        if (unitsDistance == "meters"){
            unitsDistance = "km";
        }
        else if (unitsDistance == "km"){
            unitsDistance = "miles";
        }
        else if (unitsDistance == "miles"){
            unitsDistance = "feet";
        }
        else if (unitsDistance == "feet"){
            unitsDistance = "meters";
        }
    }

    private void resetButton(){
        if(timerTask != null){
            timerTask.cancel();
            toolbarTextView.setText(formatTime(0,0,0));
            time = 0.0;
            startTimer();
        }
        //reset total distance traveled
        totalDistanceMeters = 0.0;
        prevLocation = null;
        distanceTextView.setText(MessageFormat.format("Distance: {0} meters", totalDistanceMeters));

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
        testButton = (Button)findViewById(R.id.button_test);
        speedButton = (Button)findViewById(R.id.button_speed);
        timeButton = (Button)findViewById(R.id.button_time);
        distanceButton = (Button)findViewById(R.id.button_distance);
        toolbarTextView = findViewById(R.id.toolbar_time_value);
        locationTextView = findViewById(R.id.location_text);
        latTextView = findViewById(R.id.latitude_text);
        lonTextView = findViewById(R.id.longitude_text);
        speedTextView = findViewById(R.id.speed_text);
        accTextView = findViewById(R.id.accuracy_text);
        distanceTextView = findViewById(R.id.distance_text);
        altitudeText = findViewById(R.id.altitude_text);
        locationClient = LocationServices.getFusedLocationProviderClient(this);
        locationRequest = new LocationRequest.Builder(Priority.PRIORITY_HIGH_ACCURACY, 1000)
                .setIntervalMillis(4000)
                .setMinUpdateIntervalMillis(2000)
                .build();
        testLocation = new Location("Test");
        testLocation.setLatitude(42.3601);
        testLocation.setLongitude(-71.0589);
        testLocation.setSpeed((float) fakeSpeedMph);
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
        testButton.setOnClickListener(new View.OnClickListener(){
            public void onClick(View view) {
                testMode = !testMode;  // Toggle testMode
                testLocation.setLatitude(testLat);
                testLocation.setLongitude(testLong);

                if (testMode) {
                    testButton.setText("Disable Test Mode");
                } else {
                    testButton.setText("Enable Test Mode");
                }
            }
        });
        speedButton.setOnClickListener(new View.OnClickListener(){
            public void onClick(View view) {
                setUnitsSpeed();
                speedButton.setText(unitsSpeed);
            }
        });
        timeButton.setOnClickListener(new View.OnClickListener(){
            public void onClick(View view) {
                setUnitsTime();
                timeButton.setText(unitsTime);
            }
        });
        distanceButton.setOnClickListener(new View.OnClickListener(){
            public void onClick(View view) {
                setUnitsDistance();
                distanceButton.setText(unitsDistance);
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
