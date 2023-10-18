package com.example.homework0;


import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import android.Manifest;
import android.content.Intent;
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
import com.jjoe64.graphview.series.DataPoint;
import com.jjoe64.graphview.series.LineGraphSeries;


import java.text.MessageFormat;
import java.util.Timer;
import java.util.TimerTask;
import java.util.ArrayList;


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
    Timer timer;
    TimerTask timerTask;
    Double time = 0.0;
    public static LineGraphSeries<DataPoint> series = new LineGraphSeries<>();
    private ArrayList<XYPair> xyPairArray = new ArrayList<>();
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
    private Button statsButton;
    TextView altChangeText;
    TextView latChangeText;
    TextView lonChangeText;
    TextView speedChangeText;
    TextView accChangeText;
    TextView distanceChangeText;
    static double highLat = 0.0;
    static double lowLat = 0.0;
    static double highLong = 0.0;
    static double lowLong = 0.0;
    static double highSpeed = 0.0;
    static double lowSpeed = 0.0;
    static double highAlt = 0.0;
    static double lowAlt = 0.0;
    static double highAcc = 0.0;
    static double lowAcc = 0.0;


    private LocationCallback locationCallback = new LocationCallback() {
        @Override
        public void onLocationResult(LocationResult locationResult) {
            if (locationResult == null){
                return;
            }
            if (testMode == true) {
                prevLocation.setLatitude(testLocation.getLatitude());
                prevLocation.setLongitude(testLocation.getLongitude());
                prevLocation.setAltitude(testLocation.getAltitude());
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
                altitudeText.setText(MessageFormat.format("Altitude: {0} {1}", testLocation.getAltitude(), unitsDistance));

                if (calculatedSpeed < 5) {
                    speedTextView.setTextColor(getResources().getColor(R.color.slow_speed, null));
                } else if (calculatedSpeed >= 5 && calculatedSpeed < 20) {
                    speedTextView.setTextColor(getResources().getColor(R.color.medium_speed, null));
                } else {
                    speedTextView.setTextColor(getResources().getColor(R.color.fast_speed, null));
                }
                //check if current location is highest or lowest latitude, longitude, speed, altitude, or accuracy
                if (testLocation.getLatitude() > highLat){
                    highLat = testLocation.getLatitude();
                }
                if (testLocation.getLatitude() < lowLat){
                    lowLat = testLocation.getLatitude();
                }
                if (testLocation.getLongitude() > highLong){
                    highLong = testLocation.getLongitude();
                }
                if (testLocation.getLongitude() < lowLong){
                    lowLong = testLocation.getLongitude();
                }
                if (calculatedSpeed > highSpeed){
                    highSpeed = calculatedSpeed;
                }
                if (calculatedSpeed < lowSpeed){
                    lowSpeed = calculatedSpeed;
                }
                if (testLocation.getAltitude() > highAlt){
                    highAlt = testLocation.getAltitude();
                }
                if (testLocation.getAltitude() < lowAlt){
                    lowAlt = testLocation.getAltitude();
                }
                if (testLocation.getAccuracy() > highAcc){
                    highAcc = testLocation.getAccuracy();
                }
                if (testLocation.getAccuracy() < lowAcc){
                    lowAcc = testLocation.getAccuracy();
                }

                //determine change between current and previous displayed data
                //if change is positive, display "!" in green
                //if change is negative, display "!" in red
                //if change is 0, do not display anything
                if (testLocation.getLatitude() > prevTestLat){
                    latChangeText.setTextColor(getResources().getColor(R.color.green, null));
                    // display !! if change magnitude more than 10% of previous value
                    if (testLocation.getLatitude() > prevTestLat * 1.1){
                        latChangeText.setText("!!");
                    }
                    else {
                        latChangeText.setText("!");
                    }
                }
                else if (testLocation.getLatitude() < prevTestLat){
                    latChangeText.setTextColor(getResources().getColor(R.color.red, null));
                    // display !! if change magnitude more than 10% of previous value
                    if (testLocation.getLatitude() < prevTestLat * 0.9){
                        latChangeText.setText("!!");
                    }
                    else {
                        latChangeText.setText("!");
                    }
                }
                else {
                    latChangeText.setText("");
                }
                if (testLocation.getLongitude() > prevTestLong){
                    // display !! if change magnitude more than 10% of previous value
                    if (testLocation.getLongitude() > prevTestLong * 1.1){
                        lonChangeText.setText("!!");
                    }
                    else {
                        lonChangeText.setText("!");
                    }
                    lonChangeText.setTextColor(getResources().getColor(R.color.green, null));
                }
                else if (testLocation.getLongitude() < prevTestLong){
                    // display !! if change magnitude more than 10% of previous value
                    if (testLocation.getLongitude() < prevTestLong * 0.9){
                        lonChangeText.setText("!!");
                    }
                    else {
                        lonChangeText.setText("!");
                    }
                    lonChangeText.setTextColor(getResources().getColor(R.color.red, null));
                }
                else {
                    lonChangeText.setText("");
                }
                if (calculatedSpeed > speedResult){
                    // display !! if change magnitude more than 10% of previous value
                    if (calculatedSpeed > speedResult * 1.1){
                        speedChangeText.setText("!!");
                    }
                    else {
                        speedChangeText.setText("!");
                    }
                    speedChangeText.setTextColor(getResources().getColor(R.color.green, null));
                }
                else if (calculatedSpeed < speedResult){
                    // display !! if change magnitude more than 10% of previous value
                    if (calculatedSpeed < speedResult * 0.9){
                        speedChangeText.setText("!!");
                    }
                    else {
                        speedChangeText.setText("!");
                    }
                    speedChangeText.setTextColor(getResources().getColor(R.color.red, null));
                }
                else {
                    speedChangeText.setText("");
                }
                if (testLocation.getAltitude() > prevLocation.getAltitude()){
                    // display !! if change magnitude more than 10% of previous value
                    if (testLocation.getAltitude() > prevLocation.getAltitude() * 1.1){
                        altChangeText.setText("!!");
                    }
                    else {
                        altChangeText.setText("!");
                    }
                    altChangeText.setTextColor(getResources().getColor(R.color.green, null));
                }
                else if (testLocation.getAltitude() < prevLocation.getAltitude()){
                    // display !! if change magnitude more than 10% of previous value
                    if (testLocation.getAltitude() < prevLocation.getAltitude() * 0.9){
                        altChangeText.setText("!!");
                    }
                    else {
                        altChangeText.setText("!");
                    }
                    altChangeText.setTextColor(getResources().getColor(R.color.red, null));
                }
                else {
                    altChangeText.setText("");
                }
                if (testLocation.getAccuracy() > prevLocation.getAccuracy()){
                    // display !! if change magnitude more than 10% of previous value
                    if (testLocation.getAccuracy() > prevLocation.getAccuracy() * 1.1){
                        accChangeText.setText("!!");
                    }
                    else {
                        accChangeText.setText("!");
                    }
                    accChangeText.setTextColor(getResources().getColor(R.color.red, null));
                }
                else if (testLocation.getAccuracy() < prevLocation.getAccuracy()){
                    // display !! if change magnitude more than 10% of previous value
                    if (testLocation.getAccuracy() < prevLocation.getAccuracy() * 0.9){
                        accChangeText.setText("!!");
                    }
                    else {
                        accChangeText.setText("!");
                    }
                    accChangeText.setTextColor(getResources().getColor(R.color.green, null));
                }
                else {
                    accChangeText.setText("");
                }
                if (totalDistanceMeters > 0){
                    // display !! if change magnitude more than 10% of previous value
                    if (totalDistanceMeters > 0.1){
                        distanceChangeText.setText("!!");
                    }
                    else {
                        distanceChangeText.setText("!");
                    }
                    distanceChangeText.setTextColor(getResources().getColor(R.color.green, null));
                }
                else if (totalDistanceMeters < 0){
                    // display !! if change magnitude more than 10% of previous value
                    if (totalDistanceMeters < -0.1){
                        distanceChangeText.setText("!!");
                    }
                    else {
                        distanceChangeText.setText("!");
                    }
                    distanceChangeText.setTextColor(getResources().getColor(R.color.red, null));
                }
                else {
                    distanceChangeText.setText("");
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
                    //Latitudinal coordinates and respective time saved
                    xyPairArray.add(new XYPair(time,location.getLatitude()));
                    if (prevLocation != null) {
                        float[] results = new float[1];
                        Location.distanceBetween(prevLocation.getLatitude(), prevLocation.getLongitude(), locationResult.getLastLocation().getLatitude(), locationResult.getLastLocation().getLongitude(), results);
                        totalDistanceMeters += results[0];
                    }
                    //check if current location is highest or lowest latitude, longitude, speed, altitude, or accuracy
                    if (location.getLatitude() > highLat){
                        highLat = location.getLatitude();
                    }
                    if (location.getLatitude() < lowLat){
                        lowLat = location.getLatitude();
                    }
                    if (location.getLongitude() > highLong){
                        highLong = location.getLongitude();
                    }
                    if (location.getLongitude() < lowLong){
                        lowLong = location.getLongitude();
                    }
                    if (calculatedSpeed > highSpeed){
                        highSpeed = calculatedSpeed;
                    }
                    if (calculatedSpeed < lowSpeed){
                        lowSpeed = calculatedSpeed;
                    }
                    if (altitude > highAlt){
                        highAlt = altitude;
                    }
                    if (altitude < lowAlt){
                        lowAlt = altitude;
                    }
                    if (location.getAccuracy() > highAcc){
                        highAcc = location.getAccuracy();
                    }
                    if (location.getAccuracy() < lowAcc){
                        lowAcc = location.getAccuracy();
                    }

                    //if prevLocation is not null: determine change between current and previous displayed data
                    //if change is positive, display "!" in green
                    //if change is negative, display "!" in red
                    //if change is 0, do not display anything
                    if (prevLocation != null){
                        if (location.getLatitude() > prevLocation.getLatitude()){
                            // display !! if change magnitude more than 10% of previous value
                            if (location.getLatitude() > prevLocation.getLatitude() * 1.1){
                                latChangeText.setText("!!");
                            }
                            else {
                                latChangeText.setText("!");
                            }
                            latChangeText.setTextColor(getResources().getColor(R.color.green, null));
                        }
                        else if (location.getLatitude() < prevLocation.getLatitude()){
                            // display !! if change magnitude more than 10% of previous value
                            if (location.getLatitude() < prevLocation.getLatitude() * 0.9){
                                latChangeText.setText("!!");
                            }
                            else {
                                latChangeText.setText("!");
                            }
                            latChangeText.setTextColor(getResources().getColor(R.color.red, null));
                        }
                        else {
                            latChangeText.setText("");
                        }
                        if (location.getLongitude() > prevLocation.getLongitude()){
                            // display !! if change magnitude more than 10% of previous value
                            if (location.getLongitude() > prevLocation.getLongitude() * 1.1){
                                lonChangeText.setText("!!");
                            }
                            else {
                                lonChangeText.setText("!");
                            }
                            lonChangeText.setTextColor(getResources().getColor(R.color.green, null));
                        }
                        else if (location.getLongitude() < prevLocation.getLongitude()){
                            // display !! if change magnitude more than 10% of previous value
                            if (location.getLongitude() < prevLocation.getLongitude() * 0.9){
                                lonChangeText.setText("!!");
                            }
                            else {
                                lonChangeText.setText("!");
                            }
                            lonChangeText.setTextColor(getResources().getColor(R.color.red, null));
                        }
                        else {
                            lonChangeText.setText("");
                        }
                        if (calculatedSpeed > speedResult){
                            // display !! if change magnitude more than 10% of previous value
                            if (calculatedSpeed > speedResult * 1.1){
                                speedChangeText.setText("!!");
                            }
                            else {
                                speedChangeText.setText("!");
                            }
                            speedChangeText.setTextColor(getResources().getColor(R.color.green, null));
                        }
                        else if (calculatedSpeed < speedResult){
                            // display !! if change magnitude more than 10% of previous value
                            if (calculatedSpeed < speedResult * 0.9){
                                speedChangeText.setText("!!");
                            }
                            else {
                                speedChangeText.setText("!");
                            }
                            speedChangeText.setTextColor(getResources().getColor(R.color.red, null));
                        }
                        else {
                            speedChangeText.setText("");
                        }
                        if (altitude > prevLocation.getAltitude()){
                            // display !! if change magnitude more than 10% of previous value
                            if (altitude > prevLocation.getAltitude() * 1.1){
                                altChangeText.setText("!!");
                            }
                            else {
                                altChangeText.setText("!");
                            }
                            altChangeText.setTextColor(getResources().getColor(R.color.green, null));
                        }
                        else if (altitude < prevLocation.getAltitude()){
                            // display !! if change magnitude more than 10% of previous value
                            if (altitude < prevLocation.getAltitude() * 0.9){
                                altChangeText.setText("!!");
                            }
                            else {
                                altChangeText.setText("!");
                            }
                            altChangeText.setTextColor(getResources().getColor(R.color.red, null));
                        }
                        else {
                            altChangeText.setText("");
                        }
                        if (location.getAccuracy() > prevLocation.getAccuracy()){
                            // display !! if change magnitude more than 10% of previous value
                            if (location.getAccuracy() > prevLocation.getAccuracy() * 1.1){
                                accChangeText.setText("!!");
                            }
                            else {
                                accChangeText.setText("!");
                            }
                            accChangeText.setTextColor(getResources().getColor(R.color.red, null));
                        }
                        else if (location.getAccuracy() < prevLocation.getAccuracy()){
                            // display !! if change magnitude more than 10% of previous value
                            if (location.getAccuracy() < prevLocation.getAccuracy() * 0.9){
                                accChangeText.setText("!!");
                            }
                            else {
                                accChangeText.setText("!");
                            }
                            accChangeText.setTextColor(getResources().getColor(R.color.green, null));
                        }
                        else {
                            accChangeText.setText("");
                        }
                        if (totalDistanceMeters > 0){
                            // display !! if change magnitude more than 10% of previous value
                            if (totalDistanceMeters > 0.1){
                                distanceChangeText.setText("!!");
                            }
                            else {
                                distanceChangeText.setText("!");
                            }
                            distanceChangeText.setTextColor(getResources().getColor(R.color.green, null));
                        }
                        else if (totalDistanceMeters < 0){
                            // display !! if change magnitude more than 10% of previous value
                            if (totalDistanceMeters < -0.1){
                                distanceChangeText.setText("!!");
                            }
                            else {
                                distanceChangeText.setText("!");
                            }
                            distanceChangeText.setTextColor(getResources().getColor(R.color.red, null));
                        }
                        else {
                            distanceChangeText.setText("");
                        }
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

    }

    @Override
    protected void onStart() {
        super.onStart();
        //Get permission for location information if necessary
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
            isOn = true;
            onPause();
            Intent intent = new Intent(MainActivity.this, PauseActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
            startActivity(intent);
            startLocationUpdates();
            isOn = false;
        }
    }
    private void helpButton(){
        if (askHelp == false){
            askHelp = true;
            stopLocationUpdates();
            Intent intent = new Intent(MainActivity.this, HelpActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
            startActivity(intent);
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
            //Reset time elapsed and latitudinal coordinates
            time = 0.0;
            startTimer();
            xyPairArray.clear();
        }
        //reset total distance traveled
        totalDistanceMeters = 0.0;
        prevLocation = null;
        distanceTextView.setText(MessageFormat.format("Distance: {0} meters", totalDistanceMeters));

        //reset highs and lows
        highLat = 0.0;
        lowLat = 0.0;
        highLong = 0.0;
        lowLong = 0.0;
        highSpeed = 0.0;
        lowSpeed = 0.0;
        highAlt = 0.0;
        lowAlt = 0.0;
        highAcc = 0.0;
        lowAcc = 0.0;
    }

    //Ask for permission and get last location of device
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
    //Starts time elapsed timer
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
        //return total time in units specified by timeUnits
        if (unitsTime == "seconds"){
            return String.format("%02d s",rounded);
        }
        else if (unitsTime == "minutes"){
            return String.format("%02d m",rounded/60);
        }
        else if (unitsTime == "hours"){
            return String.format("%02d h",rounded/3600);
        }
        else if (unitsTime == "days"){
            return String.format("%02d d",rounded/86400);
        }

        int seconds = ((rounded%86400)%3600)%60;
        int minutes = ((rounded%86400)%3600)/60;
        int hours = ((rounded%86400)/3600);



        return formatTime(seconds,minutes,hours);

    }
    private String formatTime(int seconds, int minutes, int hours){
        return String.format("%02d",hours) + ":" + String.format("%02d",minutes)+":"+String.format("%02d",seconds);
    }


    private void initControl(){
        graphBtn = (Button)findViewById(R.id.button_graph);
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
        altChangeText = findViewById(R.id.alt_increase);
        latChangeText = findViewById(R.id.lat_increase);
        lonChangeText = findViewById(R.id.lon_increase);
        speedChangeText = findViewById(R.id.spd_increase);
        accChangeText = findViewById(R.id.acc_increase);
        distanceChangeText = findViewById(R.id.dst_increase);
        statsButton = findViewById(R.id.button_stats);
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
        statsButton.setOnClickListener(new View.OnClickListener(){
            public void onClick(View view) {
                Intent intent = new Intent(MainActivity.this, StatsActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
                startActivity(intent);
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
                    //reset total distance traveled
                    totalDistanceMeters = 0.0;
                    prevLocation = null;
                    distanceTextView.setText(MessageFormat.format("Distance: {0} meters", totalDistanceMeters));
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

        graphBtn.setOnClickListener(new View.OnClickListener() {
            //Sorts and adds coordinates to be added to graph
            @Override
            public void onClick(View v) {
                xyPairArray = sortArray(xyPairArray);
                for (int i = 0; i<xyPairArray.size();i++){
                    double x = xyPairArray.get(i).getX();
                    double y = xyPairArray.get(i).getY();
                    series.appendData(new DataPoint(x,y),true,1000);
                }
                Intent intent = new Intent(MainActivity.this, SecondActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
                startActivity(intent);
            }
        });




    }
    private ArrayList<XYPair> sortArray(ArrayList<XYPair> array){
      /*
      //Sorts the xyValues in Ascending order to prepare them for the PointsGraphSeries<DataSet>
       */
        int factor = Integer.parseInt(String.valueOf(Math.round(Math.pow(array.size(),2))));
        int m = array.size() - 1;
        int count = 0;
        //Log.d(TAG, "sortArray: Sorting the XYArray.");


        while (true) {
            m--;
            if (m <= 0) {
                m = array.size() - 1;
            }

            try {

                double tempY = array.get(m - 1).getY();
                double tempX = array.get(m - 1).getX();
                if (tempX > array.get(m).getX()) {
                    array.get(m - 1).setY(array.get(m).getY());
                    array.get(m).setY(tempY);
                    array.get(m - 1).setX(array.get(m).getX());
                    array.get(m).setX(tempX);
                } else if (tempX == array.get(m).getX()) {
                    count++;

                } else if (array.get(m).getX() > array.get(m - 1).getX()) {
                    count++;

                }
                //break when factorial is done
                if (count == factor) {
                    break;
                }
            } catch (ArrayIndexOutOfBoundsException e) {
                break;
            }
        }
        return array;
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
//https://github.com/mitchtabian/Adding-Data-in-REAL-TIME-to-a-Graph-Graphview-lib-/blob/master/ScatterPlotDynamic
// /app/src/main/java/com/tabian/scatterplotdynamic/MainActivity.java
