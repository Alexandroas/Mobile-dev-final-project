package com.vasilakis.step_counterfinalproject;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.res.Configuration;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.PowerManager;
import android.provider.Settings;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.util.Locale;

public class MainActivity extends AppCompatActivity implements SensorEventListener {
    TextView stepCounter;
    TextView kmDone;
    static int stepsTracked;
    ProgressBar progressBar;
    double goal=7500;
    double calcKm;
    double percentDone;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            Intent intent = new Intent();
            String packageName = getPackageName();
            PowerManager pm = (PowerManager) getSystemService(POWER_SERVICE);
            if (!pm.isIgnoringBatteryOptimizations(packageName)) {
                intent.setAction(Settings.ACTION_REQUEST_IGNORE_BATTERY_OPTIMIZATIONS);
                intent.setData(Uri.parse("package:" + packageName));
                startActivity(intent);
            }
        }

        if(readFromFile("lang_pref.txt").equals("1")) {
            String languageToLoad  = "el"; // your language
            Locale locale = new Locale(languageToLoad);
            Locale.setDefault(locale);
            Configuration config = new Configuration();
            config.locale = locale;
            getBaseContext().getResources().updateConfiguration(config,
                    getBaseContext().getResources().getDisplayMetrics());

        } else {
            String languageToLoad  = "en"; // your language
            Locale locale = new Locale(languageToLoad);
            Locale.setDefault(locale);
            Configuration config = new Configuration();
            config.locale = locale;
            getBaseContext().getResources().updateConfiguration(config,
                    getBaseContext().getResources().getDisplayMetrics());
        }
        setContentView(R.layout.activity_main);
        BottomNavigationView bottomNavigationView=findViewById(R.id.bottom_navigation);


        // Set Home selected
        bottomNavigationView.setSelectedItemId(R.id.activity_main_id);


        // Perform item selected listener
        //noinspection deprecation
        bottomNavigationView.setOnNavigationItemSelectedListener(item -> {
            switch(item.getItemId())
            {
                case R.id.profile_screen_id:
                    startActivity(new Intent(getApplicationContext(),ProfileActivity.class));
                    overridePendingTransition(0,0);
                    finish();
                    return true;
                case R.id.activity_main_id:
                    return true;
                case R.id.settings_screen_id:
                    startActivity(new Intent(getApplicationContext(),SettingsActivity.class));
                    overridePendingTransition(0,0);
                    finish();
                    return true;
            }
            return false;
        });
        stepCounter = findViewById(R.id.step_counter);
        kmDone = findViewById(R.id.km_id);
        progressBar = findViewById(R.id.progress_bar);
        stepCounter.setText(readFromFile("daily_steps.txt"));
        kmDone.setText(readFromFile("daily_km.txt"));
        stepsTracked = Integer.parseInt(readFromFile("daily_steps.txt"));
        findViewById(R.id.reset_id).setOnClickListener(new View.OnClickListener() {
            int tempSteps;
            @Override
            public void onClick(View view) {
                stepCounter.setText("0");
                kmDone.setText("0");
                tempSteps=Integer.parseInt(readFromFile("total_steps.txt"));
                tempSteps=tempSteps+stepsTracked;
                writeToFile("total_steps.txt", String.valueOf(tempSteps));
                stepsTracked=0;
                writeToFile("daily_steps.txt", String.valueOf(stepsTracked));
                writeToFile("daily_km.txt", String.valueOf(0));
                Toast.makeText(MainActivity.this, "RESET DATA", Toast.LENGTH_SHORT).show();
            }
        });

        findViewById(R.id.share_id).setOnClickListener(view -> {
            String shareBody;
            Intent intent = new Intent(Intent.ACTION_SEND);
            /*This will be the actual content you wish you share.*/
            if(stepsTracked==goal) {
                shareBody = getString(R.string.share_string)+stepsTracked+getString(R.string.steps_string)+System.lineSeparator()+" " +
                        getString(R.string.goal_done_string);
            } else {
                shareBody =  getString(R.string.share_string)+System.lineSeparator()+getString(R.string.done_string)+stepsTracked+
                        getString(R.string.steps_today);
            }
            /*The type of the content is text, obviously.*/
            intent.setType("text/plain");
            /*Applying information Subject and Body.*/
            intent.putExtra(Intent.EXTRA_SUBJECT, getString(R.string.share_subject));
            intent.putExtra(Intent.EXTRA_TEXT, shareBody);
            /*Fire!*/
            startActivity(Intent.createChooser(intent, getString(R.string.share_using)));
        });


        if (ContextCompat.checkSelfPermission(this,
                Manifest.permission.ACTIVITY_RECOGNITION) == PackageManager.PERMISSION_DENIED) { //ask for permission
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                    requestPermissions(new String[]{Manifest.permission.ACTIVITY_RECOGNITION}, 0);
                }
            }
        }
        percentDone = (stepsTracked / goal) * 100;
        progressBar.setProgress((int) Math.round(percentDone));

        SensorManager sensorManager = (SensorManager) getSystemService(SENSOR_SERVICE);
        if (sensorManager == null) {
            //stopSelf();
            Toast.makeText(this, "Sensor not found!", Toast.LENGTH_SHORT).show();
        } else {
            Sensor accel = sensorManager.getDefaultSensor(Sensor.TYPE_STEP_DETECTOR);
            if (accel != null) {
                sensorManager.registerListener(this, accel, SensorManager.SENSOR_DELAY_NORMAL);
            } else {
                Toast.makeText(this, "Sorry Sensor is not available!", Toast.LENGTH_LONG).show();
            }
        }
    }


    @Override
    public void onSensorChanged(SensorEvent event) {
        Sensor sensor = event.sensor;
        if (sensor.getType() == Sensor.TYPE_STEP_DETECTOR) {
            stepsTracked++;
            calcKm=stepsTracked/1312.33595801;
            double roundOff = Math.round(calcKm * 100.0) / 100.0;
            kmDone.setText(String.valueOf(roundOff));
            stepCounter.setText(String.valueOf(stepsTracked));
            percentDone = (stepsTracked / goal) * 100;
            progressBar.setProgress((int) Math.round(percentDone));
            writeToFile("daily_km.txt",String.valueOf(roundOff));
            writeToFile("daily_steps.txt", String.valueOf(stepsTracked));
        }
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int i) {}

    public String readFromFile(String filename) {
        File path = getApplicationContext().getFilesDir();
        File readFrom = new File(path, filename);
        byte[] content = new byte[(int) readFrom.length()];
        try {
            FileInputStream stream = new FileInputStream(readFrom);
            stream.read(content);
            return new String(content);
        } catch (Exception e) {
            e.printStackTrace();
            return new String("0");
        }
    }
    public void writeToFile(String filename, String content) {
        File path=getApplicationContext().getFilesDir();
        try {
            FileOutputStream writer = new FileOutputStream(new File(path, filename));
            writer.write(content.getBytes());
            writer.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}