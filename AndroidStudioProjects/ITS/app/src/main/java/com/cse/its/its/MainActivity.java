package com.cse.its.its;

import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.os.Environment;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import org.achartengine.GraphicalView;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;

/**
 * Created by ajalan on 10/9/16.
 */
public class MainActivity extends Activity implements SensorEventListener {
    //SensorManager lets you access the device's sensors
    //declare Variables
    private boolean started = false;
    private SensorManager sensorManager;
    TextView lightValue;
    Sensor lightSensor, batterySensor;
    Button lightStart, lightStop;
    private GraphicalView view;
    private LightLineGraph lightLine;
    private LinearLayout layout;
    private ArrayList<LightData> sensorData,resultData;
    File result_folder, entire_path, training;
    

    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main_layout);

        lightValue = (TextView)findViewById(R.id.lightValue);
        //create instance of sensor manager and get system service to interact with Sensor
        sensorManager= (SensorManager)getSystemService(Context.SENSOR_SERVICE);
        lightSensor= sensorManager.getDefaultSensor(Sensor.TYPE_LIGHT);
        lightStart = (Button)findViewById(R.id.lightStart);
        lightStop = (Button)findViewById(R.id.lightStop);
        layout = (LinearLayout) findViewById(R.id.chart_container);

        lightStart.setEnabled(true);
        lightStop.setEnabled(false);

        lightStart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                started = true;
                lightStart.setEnabled(false);
                lightStop.setEnabled(true);

                try {
                    File root = new File(Environment.getExternalStorageDirectory() + "/LightExperiment");
                    if (!root.exists()) {
                        root.mkdir();
                    }
                    Date today = new Date();
                    android.text.format.DateFormat.format("yyyy-MM-dd", today);
                    File folder1 = new File(root.getAbsolutePath()+ "/" + today.toString());
                    if (!folder1.exists())
                        folder1.mkdir();
                    result_folder = new File(folder1.getAbsolutePath()+ "/LightResult");
                    if (!result_folder.exists())
                        result_folder.mkdir();
                    entire_path = new File(folder1.getAbsolutePath()+ "/LightEntirePath");
                    if (!entire_path.exists())
                        entire_path.mkdir();
                    training = new File(folder1.getAbsolutePath()+ "/LightTraining");
                    if (!training.exists())
                        training.mkdir();
                }catch (Exception e){
                    e.getStackTrace();
                    Toast.makeText(getApplicationContext(),"folders not created", Toast.LENGTH_SHORT).show();
                }

                sensorData = new ArrayList<LightData>();
                resultData = new ArrayList<LightData>();
                layout.removeAllViews();
                lightLine = new LightLineGraph();
                view = lightLine.getGraph(MainActivity.this);
                layout.addView(view);
                sensorManager.registerListener(MainActivity.this, lightSensor, SensorManager.SENSOR_DELAY_NORMAL);
            }
        });

        lightStop.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Date now =new Date();
                started = false;
                lightStart.setEnabled(true);
                lightStop.setEnabled(false);
                writeEntireData(now);
                takeScreenShot(now);
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

        if (started) {
            double x = event.values[0];
            double y = event.values[1];
            long timestamp = System.currentTimeMillis();
            LightData data = new LightData(timestamp, x/2, y/2);
            lightLine.addPoints(data);
            LightData data2 = new LightData(timestamp, x, y);
            sensorData.add(data);
            resultData.add(data);
            int length=resultData.size();
            while((resultData.get(length-1).getTimestamp()-resultData.get(0).getTimestamp())>10000){
                resultData.remove(0);
                length--;
            }
            view.repaint();
        }
    }

    /**
     * Write the log of the entire data captured while the sensor was active
     * @param now
     */
    protected void writeEntireData(Date now) {
        try {
            android.text.format.DateFormat.format("yyyy-MM-dd_hh:mm:ss", now);
            File dataFile = new File(entire_path.getAbsolutePath(), "_" + now.toString() + ".txt");
            FileOutputStream writer = new FileOutputStream(dataFile);
            writer.write((sensorData+"").getBytes());
            writer.flush();
            writer.close();
            Toast.makeText(this, "Entire path recorded", Toast.LENGTH_SHORT).show();
        }
        catch(IOException e) {
            e.printStackTrace();
            Toast.makeText(this, "Error writing entire path", Toast.LENGTH_SHORT).show();

        }
    }

    /**
     * Capture the screenshot whenever the sensor is stopped
     * @param now
     */
    protected void takeScreenShot(Date now) {
       String path = result_folder.getAbsolutePath() + "/" + "_" + now.toString() + ".jpg";
        // Create bitmap screen capture
        View v1 = getWindow().getDecorView().getRootView();
        v1.setDrawingCacheEnabled(true);
        Bitmap bitmap = Bitmap.createBitmap(v1.getDrawingCache());
        v1.setDrawingCacheEnabled(false);
        File imageFile = new File(path);
        try {
            FileOutputStream outputStream = new FileOutputStream(imageFile);
            int quality = 100;
            bitmap.compress(Bitmap.CompressFormat.JPEG, quality, outputStream);
            outputStream.flush();
            outputStream.close();
            Toast.makeText(this, "Snapshot captured", Toast.LENGTH_SHORT).show();
        } catch (Throwable e) {
            e.printStackTrace();
            Toast.makeText(this, "Error generating screenshot", Toast.LENGTH_SHORT).show();
        }
    }

    /**
     * Check for the battery status.
     */
    protected boolean checkBatteryStatus() {
        return false;
    }
}