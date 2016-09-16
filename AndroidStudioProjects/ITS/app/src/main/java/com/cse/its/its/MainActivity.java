package com.cse.its.its;

import android.app.ActionBar;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.app.Activity;
import android.content.Context;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import org.achartengine.GraphicalView;

/**
 * Created by ajalan on 10/9/16.
 */
public class MainActivity extends Activity implements SensorEventListener {
    //SensorManager lets you access the device's sensors
    //declare Variables
    private boolean started = false;
    private SensorManager sensorManager;
    TextView tvLight, lightValue;
    Sensor lightSensor;
    Button start, stop;
    private GraphicalView view;
    private LineGraph line;
    private LinearLayout layout;
    

    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main_layout);

        tvLight = (TextView)findViewById(R.id.tvLight);
        lightValue = (TextView)findViewById(R.id.lightValue);
        //create instance of sensor manager and get system service to interact with Sensor
        sensorManager= (SensorManager)getSystemService(Context.SENSOR_SERVICE);
        lightSensor= sensorManager.getDefaultSensor(Sensor.TYPE_LIGHT);
        start = (Button)findViewById(R.id.start);
        stop = (Button)findViewById(R.id.stop);
        layout = (LinearLayout) findViewById(R.id.chart_container);

        start.setEnabled(true);
        stop.setEnabled(false);

        start.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                started = true;
                start.setEnabled(false);
                stop.setEnabled(true);
                Toast.makeText(getApplicationContext(), "Light Sensor enabled", Toast.LENGTH_SHORT).show();
                sensorManager.registerListener(MainActivity.this, lightSensor, SensorManager.SENSOR_DELAY_NORMAL);
                layout.removeAllViews();
                line = new LineGraph();
                view = line.getGraph(MainActivity.this);
                layout.addView(view);
            }
        });

        stop.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                started = false;
                start.setEnabled(true);
                stop.setEnabled(false);
                Toast.makeText(getApplicationContext(), "Light Sensor disabled", Toast.LENGTH_SHORT).show();
                sensorManager.unregisterListener(MainActivity.this);
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (started) {
            sensorManager.registerListener(this, lightSensor, SensorManager.SENSOR_DELAY_NORMAL);
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        if (started) {
            sensorManager.unregisterListener(this);
        }
    }
    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {

    }
    // called when sensor value have changed
    @Override
    public void onSensorChanged(SensorEvent event) {
        lightValue.setText("Light Intensity is " + event.values[0] + " luxes");
        if (event.values[0] == 0) {

            //Toast.makeText(getApplicationContext(), "Light Value " + event.values[0],Toast.LENGTH_LONG).show();
            WindowManager.LayoutParams params = getWindow().getAttributes();
            params.flags |= WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON;
            params.screenBrightness = 0;
            getWindow().setAttributes(params);


        } else {

            //Toast.makeText(getApplicationContext(), "Light Value" + event.values[0],Toast.LENGTH_LONG).show();
            WindowManager.LayoutParams params = getWindow().getAttributes();

            params.screenBrightness = -1;
            getWindow().setAttributes(params);
        }
    }
}