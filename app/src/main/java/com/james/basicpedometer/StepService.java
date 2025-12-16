package com.james.basicpedometer;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Build;
import android.os.IBinder;
import android.util.Log;

import androidx.core.app.NotificationCompat;

public class StepService extends Service implements SensorEventListener {

    private static final String CHANNEL_ID = "step_channel";
    private static final int NOTIF_ID = 1;

    private SensorManager sensorManager;
    private Sensor stepCounter;
    private SharedPreferences prefs;

    private float baseline = -1f;

    @Override
    public void onCreate() {
        super.onCreate();

        prefs = getSharedPreferences("steps", MODE_PRIVATE);

        // IMPORTANT: read baseline ONCE
        baseline = prefs.getFloat("baseline", -1f);

        startForegroundServiceInternal();

        sensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
        stepCounter = sensorManager.getDefaultSensor(Sensor.TYPE_STEP_COUNTER);

        if (stepCounter != null) {
            sensorManager.registerListener(
                    this,
                    stepCounter,
                    SensorManager.SENSOR_DELAY_NORMAL
            );
        } else {
            Log.e("STEP", "No STEP_COUNTER sensor available");
        }
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        return START_STICKY;
    }

    @Override
    public void onSensorChanged(SensorEvent event) {
        if (event.sensor.getType() != Sensor.TYPE_STEP_COUNTER) return;

        float totalSteps = event.values[0];
        Log.d("STEP", "Raw total steps=" + totalSteps);

        // Set baseline ONLY ONCE
        if (baseline < 0) {
            baseline = totalSteps;
            prefs.edit().putFloat("baseline", baseline).apply();
            Log.d("STEP", "Baseline set to " + baseline);
            return;
        }

        int steps = (int) (totalSteps - baseline);
        if (steps < 0) steps = 0;

        boolean resetRequested =
                prefs.getBoolean("reset_requested", false);

        if (resetRequested) {
            baseline = totalSteps;
            prefs.edit()
                    .putFloat("baseline", baseline)
                    .putInt("count", 0)
                    .putBoolean("reset_requested", false)
                    .apply();
            return;
        }

// Read multiplier (default = 1)
        int multiplier = prefs.getInt("multiplier", 1);
        steps = steps * multiplier;

        prefs.edit().putInt("count", steps).apply();
        Log.d("STEP", "User steps=" + steps);
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {}

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (sensorManager != null) {
            sensorManager.unregisterListener(this);
        }
    }

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    private void startForegroundServiceInternal() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel channel = new NotificationChannel(
                    CHANNEL_ID,
                    "Pedometer",
                    NotificationManager.IMPORTANCE_MIN
            );
            NotificationManager nm =
                    (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
            nm.createNotificationChannel(channel);
        }

        Notification notification =
                new NotificationCompat.Builder(this, CHANNEL_ID)
                        .setContentTitle("Pedometer running")
                        .setContentText("Counting steps")
                        .setSmallIcon(android.R.drawable.ic_dialog_info)
                        .setOngoing(true)
                        .build();

        startForeground(NOTIF_ID, notification);
    }
}
