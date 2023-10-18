package com.example.homework0;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

public class HelpActivity extends AppCompatActivity {
    TextView locationTextView;
    Button help;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_help);
        locationTextView = findViewById(R.id.help_msg);
        locationTextView.setText("This app displays the the location of the phone along with the speed at which the phone is traveling. To pause the updates, please click the pause button. To resume updates please click the pause button again. Click the help button to exit this window");
        help = (Button) findViewById(R.id.button_helppg);
        help.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //startActivity(new Intent(SecondActivity.this, MainActivity.class));
                Intent intent = new Intent(HelpActivity.this, MainActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
                startActivity(intent);
            }
        });


    }

}
