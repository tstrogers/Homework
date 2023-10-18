package com.example.homework0;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

public class PauseActivity extends AppCompatActivity {
    TextView locationTextView;
    Button pause;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pause);
        locationTextView = findViewById(R.id.pause_msg);
        locationTextView.setText("App is paused");
        pause = (Button) findViewById(R.id.button_pausepg);
        pause.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //startActivity(new Intent(SecondActivity.this, MainActivity.class));
                Intent intent = new Intent(PauseActivity.this, MainActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
                startActivity(intent);
            }
        });


    }
}
