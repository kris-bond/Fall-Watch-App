package com.example.kris.myapplication;

import android.os.Bundle;
import android.os.Vibrator;
import android.support.wearable.activity.WearableActivity;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import android.hardware.Sensor;
import android.hardware.SensorManager;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.widget.TextView;

public class MainActivity extends WearableActivity implements SensorEventListener {

    private TextView mTextView, accelerationText, yText, zText;
    private Button button;
    private Sensor mySensor;
    private SensorManager SM;
    double acceleration;
    long weightlessTime;
    long impactTime;
    boolean fall = false;
    Vibrator vibrator;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //create sensor manager
        SM = (SensorManager)getSystemService(SENSOR_SERVICE);

        //accelerometer sensor
        mySensor = SM.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);

        //register sensor listener
        SM.registerListener(this, mySensor, 35000);

        //assign textview
        accelerationText = (TextView)findViewById(R.id.accelerationText);
        yText = (TextView)findViewById(R.id.yText);
        zText = (TextView)findViewById(R.id.zText);

        //Button
        button = (Button)findViewById(R.id.button);

        final Button button = findViewById(R.id.button);
        button.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                yText.setText("");
                zText.setText("");
            }
        });

        //Gets vibrator
        //Vibrator vibrator = (Vibrator) getSystemService(VIBRATOR_SERVICE);


        mTextView = (TextView) findViewById(R.id.text);



        // Enables Always-on
        setAmbientEnabled();
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy){
        // Not in use
    }

    //Detects Fall
    @Override
    public void onSensorChanged(SensorEvent event){
        //Gets values from accelerometer
        float xAxis = event.values[0];
        float yAxis = event.values[1];
        float zAxis = event.values[2];

        //Squares values from accelerometer
        float xsquaredText = (xAxis * xAxis);
        float ysquaredText = (yAxis * yAxis);
        float zsquaredText = (zAxis * zAxis);

        //Finds the total of squared values
        float total = (xsquaredText + ysquaredText + zsquaredText);

        //Gets acceleration in m/s^2 by finding square root of total squared accelerometer values
        acceleration = Math.sqrt(total);

        //Thresholds
        double fallWeightlessness = 5; // Acceleration of fall weightlessness
        double fallImpact = 27.5; //Acceleration of impact
        double fallMotionless = 9.8; //Acceleration of gravity
        double fallDuration = 1500000000; //Duration of fall in nanoseconds
        double fallAfterThreshold = 1; //Motionless threshold in m/s^2

        //Gets vibrator
        Vibrator vibrator = (Vibrator) getSystemService(VIBRATOR_SERVICE);

        //Shows acceleration on app
        accelerationText.setText("Acceleration: " + acceleration);

        //Detects weightlessness
        if (acceleration < fallWeightlessness) {
            yText.setText("detected weightless");
            weightlessTime = System.nanoTime(); // Gets time of weightlessness
        }

        //Detects impact
        if (acceleration > fallImpact) {
            zText.setText("detected impact");
            impactTime = System.nanoTime(); //Gets time of impact

            //Detects duration of fall
            if (impactTime - weightlessTime < fallDuration) {
                zText.setText("Duration okay");

                //Detects motionless after fall
                if (fallMotionless - acceleration <= fallAfterThreshold || fallMotionless - acceleration >= fallAfterThreshold) {
                    fall = true;
                    vibrator.vibrate(1000); //Vibrates watch
                    zText.setText("fall detected!");
                }
            }
        }
    }
}
