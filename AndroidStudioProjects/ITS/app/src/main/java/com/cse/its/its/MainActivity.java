package com.cse.its.its;

import android.app.ActionBar;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.app.Activity;
import android.content.Context;
import android.view.Window;
import android.view.WindowManager;
import android.widget.TextView;
import android.widget.Toast;

public class MainActivity extends Activity implements SensorEventListener {
    //SensorManager lets you access the device's sensors
    //declare Variables
    private SensorManager sensorManager;
    TextView tVProximity;

    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main_layout);

        tVProximity = (TextView)findViewById(R.id.tVProximity);
        //create instance of sensor manager and get system service to interact with Sensor
        sensorManager= (SensorManager)getSystemService(Context.SENSOR_SERVICE);
        Sensor proximitySensor= sensorManager.getDefaultSensor(Sensor.TYPE_LIGHT);


    }

    @Override
    protected void onResume() {
        super.onResume();
        // register this class as a listener for the Proximity Sensor
        sensorManager.registerListener(this,
                sensorManager.getDefaultSensor(Sensor.TYPE_LIGHT),
                SensorManager.SENSOR_DELAY_NORMAL);
    }
    @Override
    protected void onPause() {
        // unregister listener
        super.onPause();
        sensorManager.unregisterListener(this);
    }
    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {

    }
    // called when sensor value have changed
    @Override
    public void onSensorChanged(SensorEvent event) {
        if (event.values[0] == 0) {

            Toast.makeText(getApplicationContext(), "sensor in 0",Toast.LENGTH_LONG).show();
            WindowManager.LayoutParams params = getWindow().getAttributes();
            params.flags |= WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON;
            params.screenBrightness = 0;
            getWindow().setAttributes(params);


        } else {

            Toast.makeText(getApplicationContext(), "sensor in 1",Toast.LENGTH_LONG).show();
            WindowManager.LayoutParams params = getWindow().getAttributes();

            params.screenBrightness = -1;
            getWindow().setAttributes(params);
        }
    }
}