package com.james.basicpedometer;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.hardware.Sensor;
import android.hardware.SensorManager;
import android.net.Uri;
import android.os.Bundle;
import android.provider.Settings;
import android.view.View;
import android.widget.TextView;

public class SettingsActivity extends Activity {

    private TextView sensorStatus;
    private TextView multiplierStatus;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

        sensorStatus = findViewById(R.id.sensorStatus);
        multiplierStatus = findViewById(R.id.multiplierStatus);

        updateSensorStatus();
        updateMultiplierText();
    }

    @Override
    protected void onResume() {
        super.onResume();
        updateMultiplierText();
    }

    /* ----------------------------
       SENSOR STATUS (READ-ONLY)
       ---------------------------- */
    private void updateSensorStatus() {
        SensorManager sm = (SensorManager) getSystemService(SENSOR_SERVICE);
        if (sm == null) {
            sensorStatus.setText("Step counter: unavailable");
            return;
        }

        Sensor stepSensor = sm.getDefaultSensor(Sensor.TYPE_STEP_COUNTER);
        if (stepSensor != null) {
            sensorStatus.setText("Step counter: PRESENT");
        } else {
            sensorStatus.setText("Step counter: NOT available");
        }
    }

    /* ----------------------------
       MULTIPLIER HANDLING
       ---------------------------- */
    private void setMultiplier(int value) {
        SharedPreferences prefs =
                getSharedPreferences("steps", MODE_PRIVATE);

        prefs.edit().putInt("multiplier", value).apply();
        updateMultiplierText();
    }

    public void resetSteps(android.view.View v) {
        SharedPreferences prefs =
                getSharedPreferences("steps", MODE_PRIVATE);

        // Signal StepService to reset baseline
        prefs.edit()
                .putBoolean("reset_requested", true)
                .apply();
    }

    private void updateMultiplierText() {
        SharedPreferences prefs =
                getSharedPreferences("steps", MODE_PRIVATE);

        int m = prefs.getInt("multiplier", 1);
        multiplierStatus.setText("Current multiplier: " + m + "×");
    }

    /* ----------------------------
       BUTTON CALLBACKS (XML)
       ---------------------------- */
    public void setMultiplier1(View v) {
        setMultiplier(1);
    }

    public void setMultiplier2(View v) {
        setMultiplier(2);
    }

    public void setMultiplier3(View v) {
        setMultiplier(3);
    }

    public void openAppSettings(View v) {
        Intent intent =
                new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
        intent.setData(Uri.parse("package:" + getPackageName()));
        startActivity(intent);
    }
}
