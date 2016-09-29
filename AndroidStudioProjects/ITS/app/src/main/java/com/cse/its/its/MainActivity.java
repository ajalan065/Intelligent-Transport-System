package com.cse.its.its;

import android.app.Activity;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Bitmap;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.BatteryManager;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.support.v4.app.NotificationCompat;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import org.achartengine.GraphicalView;

import java.io.File;
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
    private boolean lightstarted = false, accelstarted = false, soundstarted = false;
    private SensorManager sensorManager;
    protected Handler mHandler = new Handler();
    TextView lightValue, battery;
    Sensor lightSensor, batterySensor, accelSensor;
    SensorEventListener mSensorListener;
    Button lightStart, lightStop, accelStart, accelStop, soundStart, soundStop, bmp, ph, sbrk, ibrk;
    private GraphicalView view, view2, view3;
    private LightLineGraph lightLine;
    private AccelLineGraph accelLine;
    //private SoundLineGraph soundLine;
    private LinearLayout layout, layout2, layout3;
    private ArrayList<LightData> lightSensorData, lightResultData;
    private ArrayList<AccelData> accelSensorData, accelResultData;
    //private ArrayList<SoundData> soundSensorData, soundResultData;
    File result_folder, entire_path, training, its, result_folder2, entire_path2, training2, result_folder3, entire_path3, training3, folder1;
    private EditText result;
    private String transport;
    // Interval of sending notification
    private final static int INTERVAL = 1000*60*2; // Every 2 minutes


    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.main_layout);

        getBatteryPercentage();

        lightValue = (TextView)findViewById(R.id.lightValue);
        //create instance of sensor manager and get system service to interact with Sensor
        sensorManager= (SensorManager)getSystemService(Context.SENSOR_SERVICE);
        lightSensor= sensorManager.getDefaultSensor(Sensor.TYPE_LIGHT);
        accelSensor = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
        //soundSensor = sensorManager.getDefaultSensor(Sensor.TYPE_SOUND);
        lightStart = (Button)findViewById(R.id.lightStart);
        lightStop = (Button)findViewById(R.id.lightStop);
        accelStart = (Button)findViewById(R.id.accelStart);
        accelStop = (Button)findViewById(R.id.accelStop);
        soundStart = (Button)findViewById(R.id.soundStart);
        soundStop = (Button)findViewById(R.id.soundStop);
        bmp = (Button)findViewById(R.id.button_bmp);
        ph = (Button)findViewById(R.id.button_ph);
        ibrk = (Button)findViewById(R.id.button_ibrk);
        sbrk = (Button)findViewById(R.id.button_sbrk);
        layout = (LinearLayout)findViewById(R.id.chart_container);
        layout2 = (LinearLayout)findViewById(R.id.chart_container2);
        layout3 = (LinearLayout)findViewById(R.id.chart_container3);

        lightStart.setEnabled(true);
        lightStop.setEnabled(false);
        accelStart.setEnabled(true);
        accelStop.setEnabled(false);
        soundStart.setEnabled(true);
        soundStop.setEnabled(false);
        bmp.setEnabled(false);
        ph.setEnabled(false);
        ibrk.setEnabled(false);
        sbrk.setEnabled(false);

        transport = ModeOfTransport.getMode();

        //battery = (TextView)findViewById(R.id.battery);

        // On starting the accelerometer
        accelStart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                accelstarted = true;
                accelStart.setEnabled(false);
                accelStop.setEnabled(true);
                bmp.setEnabled(true);
                sbrk.setEnabled(true);
                ibrk.setEnabled(true);
                ph.setEnabled(true);

                try {
                    its = new File(Environment.getExternalStorageDirectory() + "/Intelligence Transport System");
                    if (!its.exists()) {
                        its.mkdir();
                    }

                    Date today = new Date();

                    // Create the folder for Accelerometer Data
                    File accel = new File(its.getAbsolutePath() + "/AccelExperiment");
                    if (!accel.exists()) {
                        accel.mkdir();
                    }
                    android.text.format.DateFormat.format("yyyy-MM-dd", today);
                    folder1 = new File(accel.getAbsolutePath()+ "/" + today.toString());
                    if (!folder1.exists())
                        folder1.mkdir();
                    result_folder2 = new File(folder1.getAbsolutePath()+ "/AccelResult");
                    if (!result_folder2.exists())
                        result_folder2.mkdir();
                    entire_path2 = new File(folder1.getAbsolutePath()+ "/AccelEntirePath");
                    if (!entire_path2.exists())
                        entire_path2.mkdir();
                    training2 = new File(folder1.getAbsolutePath()+ "/AccelTraining");
                    if (!training2.exists())
                        training2.mkdir();
                }
                catch (Exception e){
                    e.getStackTrace();
                    Toast.makeText(getApplicationContext(),"folders not created", Toast.LENGTH_SHORT).show();
                }

                accelSensorData = new ArrayList<AccelData>();
                accelResultData = new ArrayList<AccelData>();
                layout2.removeAllViewsInLayout();
                accelLine = new AccelLineGraph();
                view2 = accelLine.getGraph(MainActivity.this);
                layout2.addView(view2);
                sensorManager.registerListener(MainActivity.this, accelSensor, SensorManager.SENSOR_DELAY_NORMAL);
            }
        });

        // On stopping the accelerometer
        accelStop.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Date now = new Date();
                accelstarted = false;
                accelStart.setEnabled(true);
                accelStop.setEnabled(false);
                bmp.setEnabled(false);
                ph.setEnabled(false);
                ibrk.setEnabled(false);
                sbrk.setEnabled(false);
                writeEntireDataAccel(now);
                takeScreenShotAccel(now);
                sensorManager.unregisterListener(MainActivity.this, accelSensor);
            }
        });

        // On pressing Bump button
        bmp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Date now = new Date();
                //ArrayList<AccelData> record=accelResultData;
                takeScreenShotAccel(now);
                //writeTrainingSet(record, now);
                //writeResultSet(record, now);
            }
        });

        // On pressing the Pothole button
        ph.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Date now = new Date();
                //ArrayList<AccelData> record=accelResultData;
                takeScreenShotAccel(now);
                //writeTrainingSet(record, now);
                //writeResultSet(record, now);
            }
        });

        // On pressing the Slow break button
        sbrk.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Date now = new Date();
                //ArrayList<AccelData> record=accelResultData;
                takeScreenShotAccel(now);
                //writeTrainingSet(record, now);
                //writeResultSet(record, now);
            }
        });

        // On pressing the Immediate break button
        ibrk.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Date now = new Date();
                //ArrayList<AccelData> record=accelResultData;
                takeScreenShotAccel(now);
                //writeTrainingSet(record, now);
                //writeResultSet(record, now);
            }
        });
        
        //On starting the light sensor
        lightStart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                lightstarted = true;
                lightStart.setEnabled(false);
                lightStop.setEnabled(true);

                try {
                    // Create the universal "Intelligence Transport System" folder
                    its = new File(Environment.getExternalStorageDirectory() + "/Intelligence Transport System");
                    if (!its.exists()) {
                        its.mkdir();
                    }
                    // Create the subfolder for Light Data
                    File light = new File(its.getAbsolutePath() + "/LightExperiment");
                    if (!light.exists()) {
                        light.mkdir();
                    }
                    Date today = new Date();

                    android.text.format.DateFormat.format("yyyy-MM-dd", today);
                    folder1 = new File(light.getAbsolutePath()+ "/" + today.toString());
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
                }
                catch (Exception e){
                    e.getStackTrace();
                    Toast.makeText(getApplicationContext(),"folders not created", Toast.LENGTH_SHORT).show();
                }

                sensorManager.registerListener(MainActivity.this, lightSensor, SensorManager.SENSOR_DELAY_NORMAL);
                lightSensorData = new ArrayList<LightData>();
                lightResultData = new ArrayList<LightData>();
                layout.removeAllViewsInLayout();
                lightLine = new LightLineGraph();
                view = lightLine.getGraph(MainActivity.this);
                layout.addView(view);
            }
        });

        lightStop.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Date now =new Date();
                lightstarted = false;
                lightStart.setEnabled(true);
                lightStop.setEnabled(false);
                writeEntireDataLight(now);
                takeScreenShotLight(now);
                sensorManager.unregisterListener(MainActivity.this, lightSensor);

            }
        });

        soundStart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                soundstarted = true;
                soundStart.setEnabled(false);
                soundStop.setEnabled(true);
                try {
                    its = new File(Environment.getExternalStorageDirectory() + "/Intelligence Transport System");
                    if (!its.exists()) {
                        its.mkdir();
                    }

                    Date today = new Date();

                    // Create the subfolder for Sound Data
                    File sound = new File(its.getAbsolutePath() + "/SoundExperiment");
                    if (!sound.exists()) {
                        sound.mkdir();
                    }
                    android.text.format.DateFormat.format("yyyy-MM-dd", today);
                    folder1 = new File(sound.getAbsolutePath()+ "/" + today.toString());
                    if (!folder1.exists())
                        folder1.mkdir();
                    result_folder3 = new File(folder1.getAbsolutePath()+ "/SoundResult");
                    if (!result_folder3.exists())
                        result_folder3.mkdir();
                    entire_path3 = new File(folder1.getAbsolutePath()+ "/SoundEntirePath");
                    if (!entire_path3.exists())
                        entire_path3.mkdir();
                    training3 = new File(folder1.getAbsolutePath()+ "/SoundTraining");
                    if (!training3.exists())
                        training3.mkdir();

                }
                catch (Exception e){
                    e.getStackTrace();
                    Toast.makeText(getApplicationContext(),"folders not created", Toast.LENGTH_SHORT).show();
                }

                //soundSensorData = new ArrayList<SoundData>();
                //soundResultData = new ArrayList<SoundData>();
                //layout3.removeAllViews();
                //soundLine = new SoundLineGraph();
                //view3 = soundLine.getGraph(MainActivity.this);
                //layout3.addView(view3);
                //sensorManager.registerListener(MainActivity.this, soundSensor, SensorManager.SENSOR_DELAY_NORMAL);
            }
        });

        // Check the battery status every two minutes.
        final Handler mHandler = new Handler();
        mHandler.postDelayed(new Runnable() {
            @Override
            public void run() {
                getBatteryPercentage();
                mHandler.postDelayed(this, INTERVAL);
            }
        }, INTERVAL);

    }

    @Override
    protected void onResume() {
        super.onResume();
        if (lightstarted) {
            sensorManager.registerListener(this, lightSensor, SensorManager.SENSOR_DELAY_NORMAL);
        }

        if (accelstarted) {
            sensorManager.registerListener(this, accelSensor, SensorManager.SENSOR_DELAY_NORMAL);
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        if (lightstarted) {
            sensorManager.unregisterListener(this);
        }

        if (accelstarted) {
            sensorManager.unregisterListener(this);
        }
    }
    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {

    }

    // called when sensor value have changed
    @Override
    public void onSensorChanged(SensorEvent event) {
        //lightValue.setText("Light Intensity is " + event.values[0] + " luxes");
        Sensor sensor = event.sensor;
        if (sensor.getType() == Sensor.TYPE_LIGHT) {
            double x = event.values[0];
            double y = event.values[1];
            long timestamp = System.currentTimeMillis();
            LightData data = new LightData(timestamp, x/2, y/2);
            lightLine.addPoints(data);
            LightData data2 = new LightData(timestamp, x, y);
            lightSensorData.add(data);
            lightResultData.add(data);
            int length=lightResultData.size();
            while((lightResultData.get(length-1).getTimestamp()-lightResultData.get(0).getTimestamp())>10000){
                lightResultData.remove(0);
                length--;
            }
            view.repaint();
        }

        if (sensor.getType() == Sensor.TYPE_ACCELEROMETER) {
            double x = event.values[0];
            double y = event.values[1];
            double z = event.values[2];
            long timestamp = System.currentTimeMillis();
            AccelData data = new AccelData(timestamp, x/2, y/2, z/2);
            accelLine.addPoints(data);
            AccelData data2 = new AccelData(timestamp, x, y, z);
            accelSensorData.add(data);
            accelResultData.add(data);
            int length=accelResultData.size();
            while((accelResultData.get(length-1).getTimestamp()-accelResultData.get(0).getTimestamp())>10000){
                accelResultData.remove(0);
                length--;
            }
            view2.repaint();
        }
    }


    /**
     * Write the log of the entire data captured while the light sensor was active
     * @param now
     */
    protected void writeEntireDataLight(Date now) {
        try {
            android.text.format.DateFormat.format("yyyy-MM-dd_hh:mm:ss", now);
            File dataFile = new File(entire_path.getAbsolutePath(), "_" + now.toString() + ".txt");
            FileOutputStream writer = new FileOutputStream(dataFile);
            writer.write((lightSensorData+"").getBytes());
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
     * Capture the screenshot whenever the light sensor is stopped
     * @param now
     */
    protected void takeScreenShotLight(Date now) {
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
     * Write the log of the entire data captured while the accelerometer was active
     * @param now
     */
    public void writeEntireDataAccel(Date now) {
        try
        {
            android.text.format.DateFormat.format("yyyy-MM-dd_hh:mm:ss", now);
            File dataFile = new File(entire_path.getAbsolutePath(), transport + "_" + now.toString() + ".txt");
            FileOutputStream writer = new FileOutputStream(dataFile);
            writer.write((accelSensorData+"").getBytes());
            writer.flush();
            writer.close();
            Toast.makeText(this, "Entire path recorded", Toast.LENGTH_SHORT).show();
        }
        catch(IOException e)
        {
            e.printStackTrace();
            Toast.makeText(this, "Error writing entire path", Toast.LENGTH_SHORT).show();

        }
    }

    /**
     * Capture the screenshot whenever the accelerometer stops
     * @param now
     */
    protected void takeScreenShotAccel(Date now) {
        try {
            // image naming and path  to include sd card  appending name you choose for file
            String mPath = result_folder.getAbsolutePath() + "/" + transport + "_" + now.toString() + ".jpg";

            // create bitmap screen capture
            View v1 = getWindow().getDecorView().getRootView();
            v1.setDrawingCacheEnabled(true);
            Bitmap bitmap = Bitmap.createBitmap(v1.getDrawingCache());
            v1.setDrawingCacheEnabled(false);

            File imageFile = new File(mPath);

            FileOutputStream outputStream = new FileOutputStream(imageFile);
            int quality = 100;
            bitmap.compress(Bitmap.CompressFormat.JPEG, quality, outputStream);
            outputStream.flush();
            outputStream.close();
            Toast.makeText(this, "Snapshot captured", Toast.LENGTH_SHORT).show();
        }
        catch (Throwable e) {
            e.printStackTrace();
            Toast.makeText(this, "Error generating screenshot", Toast.LENGTH_SHORT).show();
        }
    }

    /**
     * Check for the battery status.
     */
    protected void getBatteryPercentage() {
        /*BroadcastReceiver batteryLevelReceiver = new BroadcastReceiver() {
            public void onReceive(Context context, Intent intent) {
                context.unregisterReceiver(this);
                int currentLevel = intent.getIntExtra(BatteryManager.EXTRA_LEVEL, -1);
                int scale = intent.getIntExtra(BatteryManager.EXTRA_SCALE, -1);
                int level = -1;
                if (currentLevel >= 0 && scale > 0) {
                    level = (currentLevel * 100) / scale;
                }
                //battery.setText("Battery Level Remaining: " + level + "%");
            }
        };
        IntentFilter batteryLevelFilter = new IntentFilter(Intent.ACTION_BATTERY_CHANGED);
        registerReceiver(batteryLevelReceiver, batteryLevelFilter);*/

        IntentFilter filter = new IntentFilter(Intent.ACTION_BATTERY_CHANGED);
        Intent batteryStatus = registerReceiver(null, filter);
        int chargeState = batteryStatus.getIntExtra(BatteryManager.EXTRA_STATUS, -1);
        int currentLevel = batteryStatus.getIntExtra(BatteryManager.EXTRA_LEVEL, -1);
        int scale = batteryStatus.getIntExtra(BatteryManager.EXTRA_SCALE, -1);
        int level = -1;
        if (currentLevel >= 0 && scale > 0) {
            level = (currentLevel * 100) / scale;
        }
        // If the device is in charging state or if the barrery level is above 15%
        if (chargeState == BatteryManager.BATTERY_STATUS_CHARGING || level >= 15) {
            // do nothing
        }

        // If battery level is below 15%
        else {
            getNotification();
        }
    }

    /**
     * Display the notification based on battery status.
     */
    public void getNotification() {
        final int NOTIFICATION_ID = 0;
        PendingIntent dismissIntent = CustomNotification.dismissNotification(NOTIFICATION_ID, this);
        PendingIntent actionIntent = CustomNotification.acceptNotification(NOTIFICATION_ID, this);
        NotificationManager notManager = (NotificationManager)getSystemService(NOTIFICATION_SERVICE);

        // Build the notification object along with its custom values.
        Notification notif = new Notification.Builder(this)
                .setSmallIcon(R.drawable.ic_notification)
                .setContentTitle("Intelligent Transport System")
                .setContentText("Low Battery Warning")
                .setTicker("Intelligent Transport System Notification")
                .addAction(R.drawable.ic_dismiss, "Dismiss", dismissIntent)
                .addAction(R.drawable.ic_open, "OK", actionIntent)
                .setAutoCancel(true)
                .build();

        // Set the default vibration and sound features.
        notif.defaults |= Notification.DEFAULT_VIBRATE;
        notif.defaults |= Notification.DEFAULT_SOUND;
        // Show the notification
        notManager.notify(NOTIFICATION_ID,notif);
    }

}