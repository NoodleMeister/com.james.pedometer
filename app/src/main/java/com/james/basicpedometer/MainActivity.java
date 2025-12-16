package com.james.basicpedometer;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;

public class MainActivity extends Activity {

    private TextView stepText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        startForegroundService(new Intent(this, StepService.class));

        setContentView(R.layout.activity_main);

        stepText = findViewById(R.id.stepText);
        Button settingsButton = findViewById(R.id.settingsButton);

        settingsButton.setOnClickListener(v ->
                startActivity(new Intent(MainActivity.this, SettingsActivity.class))
        );
    }

    @Override
    protected void onResume() {
        super.onResume();
        updateSteps();
    }

    private void updateSteps() {
        int steps = getSharedPreferences("steps", MODE_PRIVATE)
                .getInt("count", 0);
        stepText.setText("Steps: " + steps);
    }
}

