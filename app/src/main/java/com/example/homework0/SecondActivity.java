package com.example.homework0;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import androidx.appcompat.app.AppCompatActivity;
import com.jjoe64.graphview.GraphView;
import com.jjoe64.graphview.series.DataPoint;
import com.jjoe64.graphview.series.LineGraphSeries;


public class SecondActivity extends AppCompatActivity {
    static GraphView lineGraph;
    Button exitBtn;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_second);
        lineGraph = findViewById(R.id.idGraphView);
        exitBtn = (Button)findViewById(R.id.button_exit);
        lineGraph.setTitle("Latitude(Degrees) vs Time(s)");
        lineGraph.addSeries(MainActivity.series);
        //set Scrollable and Scaleable
        lineGraph.getViewport().setScalable(true);
        lineGraph.getViewport().setScalableY(true);
        lineGraph.getViewport().setScrollable(true);
        lineGraph.getViewport().setScrollableY(true);

        exitBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //startActivity(new Intent(SecondActivity.this, MainActivity.class));
                Intent intent = new Intent(SecondActivity.this, MainActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
                startActivity(intent);
            }
        });
    }


}


