package com.example.homework0;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import java.text.MessageFormat;

public class StatsActivity extends AppCompatActivity{
    TextView latitudeHighView;
    TextView latitudeLowView;
    TextView longitudeHighView;
    TextView longitudeLowView;
    TextView speedHighView;
    TextView speedLowView;
    TextView altitudeHighView;
    TextView altitudeLowView;
    TextView accuracyHighView;
    TextView accuracyLowView;
    Button exitBtn;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_stats);
        exitBtn = (Button)findViewById(R.id.button_back);
        latitudeHighView = findViewById(R.id.latHigh);
        latitudeLowView = findViewById(R.id.latHigh2);
        longitudeHighView = findViewById(R.id.longHigh);
        longitudeLowView = findViewById(R.id.longHigh2);
        speedHighView = findViewById(R.id.speedHigh);
        speedLowView = findViewById(R.id.speedHigh2);
        altitudeHighView = findViewById(R.id.altHigh);
        altitudeLowView = findViewById(R.id.altHigh2);
        accuracyHighView = findViewById(R.id.accHigh);
        accuracyLowView = findViewById(R.id.accHigh2);

        latitudeHighView.setText("Latitude High: " + MainActivity.highLat);
        latitudeLowView.setText("Latitude Low: " + MainActivity.lowLat);
        longitudeHighView.setText("Longitude High: " + MainActivity.highLong);
        longitudeLowView.setText("Longitude Low: " + MainActivity.lowLong);
        speedHighView.setText("Speed High: " + MainActivity.highSpeed);
        speedLowView.setText("Speed Low: " + MainActivity.lowSpeed);
        altitudeHighView.setText("Altitude High: " + MainActivity.highAlt);
        altitudeLowView.setText("Altitude Low: " + MainActivity.lowAlt);
        accuracyHighView.setText("Accuracy High: " + MainActivity.highAcc);
        accuracyLowView.setText("Accuracy Low: " + MainActivity.lowAcc);

        exitBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //startActivity(new Intent(SecondActivity.this, MainActivity.class));
                Intent intent = new Intent(StatsActivity.this, MainActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
                startActivity(intent);
            }
        });
    }
}
