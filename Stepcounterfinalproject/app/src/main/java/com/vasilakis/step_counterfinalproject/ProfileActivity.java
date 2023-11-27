package com.vasilakis.step_counterfinalproject;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.MenuItem;
import android.widget.EditText;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.util.Objects;

public class ProfileActivity extends AppCompatActivity {
    EditText enterWeight;
    EditText enterHeight;
    TextView weight;
    TextView height;
    TextView total;
    TextView totalKm;
    String FUCKMYLIFE;
    static int stepsTracked=0;
    double calcKm=0;
    @SuppressLint("SetTextI18n")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.profile_activity);

        // Initialize and assign variable
        BottomNavigationView bottomNavigationView=findViewById(R.id.bottom_navigation);

        // Set Home selected
        bottomNavigationView.setSelectedItemId(R.id.profile_screen_id);

        // Perform item selected listener
        bottomNavigationView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {

                switch(item.getItemId())
                {
                    case R.id.activity_main_id:
                        startActivity(new Intent(getApplicationContext(),MainActivity.class));
                        overridePendingTransition(0,0);
                        finish();
                        return true;
                    case R.id.profile_screen_id:
                        return true;
                    case R.id.settings_screen_id:
                        startActivity(new Intent(getApplicationContext(),SettingsActivity.class));
                        overridePendingTransition(0,0);
                        finish();
                        return true;
                }
                return false;
            }
        });

        enterHeight=findViewById(R.id.set_height_id);
        enterWeight=findViewById(R.id.set_weight_id);
        weight = findViewById(R.id.you_weight_view_id);
        height=findViewById(R.id.you_height_view_id);
        total=findViewById(R.id.total_steps_id);
        totalKm=findViewById(R.id.total_km);

        weight.setText(readFromFile("weight.txt"));
        height.setText(readFromFile("height.txt"));
        if (Objects.equals(readFromFile("total_steps.txt"), "Set value.")) {
            total.setText(R.string.total_steps);
        } else {
            total.setText(getString(R.string.total_steps) +" "+ readFromFile("total_steps.txt"));
        }



        if (Objects.equals(readFromFile("total_steps.txt"), "Set value.")) {
            totalKm.setText(R.string.km_done);
        } else {
            stepsTracked= Integer.parseInt(readFromFile("total_steps.txt"));
            calcKm=stepsTracked/1312.33595801;
            double roundOff = Math.round(calcKm * 100.0) / 100.0;
            totalKm.setText("Total km done: " + roundOff);
        }

        enterHeight.setOnEditorActionListener((textView, i, keyEvent) -> {
            height.setText(enterHeight.getText().toString());
            FUCKMYLIFE=enterHeight.getText().toString();
            writeToFile("height.txt",FUCKMYLIFE);
            return false;
        });
        enterWeight.setOnEditorActionListener((textView, i, keyEvent) -> {
            weight.setText(enterWeight.getText().toString());
            FUCKMYLIFE=enterWeight.getText().toString();
            writeToFile("weight.txt",FUCKMYLIFE);
            return false;
        });
    }
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
            return new String("Set value.");
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