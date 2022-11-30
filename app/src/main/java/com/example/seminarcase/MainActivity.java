package com.example.seminarcase;

import androidx.appcompat.app.AppCompatActivity;

import android.graphics.Color;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.jjoe64.graphview.GraphView;
import com.jjoe64.graphview.Viewport;
import com.jjoe64.graphview.series.DataPoint;
import com.jjoe64.graphview.series.LineGraphSeries;

public class MainActivity extends AppCompatActivity {

    TextView txt_currentAccel,txt_prevAccel,txt_acceleration;
    ProgressBar prog_shakeMeter;

    private SensorManager mSensorManager;
    private Sensor mAccelerometer;
    private double accelerationCurrentValue;
    private double accelerationPreviousValue;
    private int pointsPlotted = 5;
    private int graphIntervalCounter = 0;

    LineGraphSeries<DataPoint> series = new LineGraphSeries<DataPoint>(new DataPoint[] {
            new DataPoint(0, 1),
            new DataPoint(1, 5),
            new DataPoint(2, 3),
            new DataPoint(3, 2),
            new DataPoint(4, 6)
    });


    //    this will become the object that will listen for any changes that occur
    private SensorEventListener sensorEventListener = new SensorEventListener() {
    @Override
    public void onSensorChanged(SensorEvent sensorEvent) {
        float x = sensorEvent.values[0];
        float y = sensorEvent.values[1];
        float z = sensorEvent.values[2];

        accelerationCurrentValue = Math.sqrt((x*x+y*y+z*z));
        double changeInAcceleration = Math.abs(accelerationCurrentValue-accelerationPreviousValue);
        accelerationPreviousValue = accelerationCurrentValue;

//        Update Text Views
        txt_currentAccel.setText("Current = "+(int)accelerationCurrentValue);
        txt_prevAccel.setText("Prev = "+(int)accelerationPreviousValue);
        txt_acceleration.setText("Acceleration Change = " + (int)changeInAcceleration );

        prog_shakeMeter.setProgress((int) changeInAcceleration);
        //Change colors of the progress bar
        if(changeInAcceleration>14){
            txt_acceleration.setBackgroundColor(Color.RED);
        }
        else if(changeInAcceleration>5){
            txt_acceleration.setBackgroundColor(Color.parseColor("#fcad03"));
        }
        else if(changeInAcceleration>2){
            txt_acceleration.setBackgroundColor(Color.YELLOW);
        }
        else{
            txt_acceleration.setBackgroundColor(getResources().getColor(com.google.android.material.R.color.design_default_color_background));
        }

//        Update the graph
        pointsPlotted++;
        if (pointsPlotted>1000){
            pointsPlotted=1;
            series.resetData(new DataPoint[]{new DataPoint(1,0)});
        }
        series.appendData( new DataPoint(pointsPlotted, changeInAcceleration),true,pointsPlotted);
        viewport.setMaxX(pointsPlotted);
        viewport.setMinX(pointsPlotted - 200);

    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int i) {

    }
};
    private Viewport viewport;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        txt_acceleration=findViewById(R.id.txt_accel);
        txt_currentAccel=findViewById(R.id.txt_currentAccel);
        txt_prevAccel=findViewById(R.id.txt_prevAccel);

        prog_shakeMeter=findViewById(R.id.prog_shakemeter);

//        Initialize sensor objects
        mSensorManager = (SensorManager)getSystemService(SENSOR_SERVICE);
        mAccelerometer = mSensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);

//        Sample graph Code
        GraphView graph = (GraphView) findViewById(R.id.graph);
        viewport = graph.getViewport();
        viewport.setScalable(true);
        viewport.setXAxisBoundsManual(true);


        graph.addSeries(series);
    }


    protected void onResume() {
        super.onResume();
        mSensorManager.registerListener(sensorEventListener, mAccelerometer, SensorManager.SENSOR_DELAY_NORMAL);
    }

    protected void onPause() {
        super.onPause();
        mSensorManager.unregisterListener(sensorEventListener);
    }

}