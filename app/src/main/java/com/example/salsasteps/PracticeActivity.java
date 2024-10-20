package com.example.salsasteps;

import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.os.Handler;
import android.widget.Button;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import java.util.Locale;

public class PracticeActivity extends AppCompatActivity implements SensorEventListener {

    private TextView stepCounterTextView;
    private TextView timerTextView;
    private Button startButton;
    private Button pauseResumeButton;
    private Button stopButton;

    private SensorManager sensorManager;
    private Sensor stepCounterSensor;

    private int stepCount = 0;
    private long startTime = 0;
    private boolean isRunning = false;
    private Handler timerHandler = new Handler();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_practice);

        stepCounterTextView = findViewById(R.id.stepCounterTextView);
        timerTextView = findViewById(R.id.timerTextView);
        startButton = findViewById(R.id.startButton);
        pauseResumeButton = findViewById(R.id.pauseResumeButton);
        stopButton = findViewById(R.id.stopButton);

        sensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
        stepCounterSensor = sensorManager.getDefaultSensor(Sensor.TYPE_STEP_COUNTER);

        startButton.setOnClickListener(v -> startPractice());
        pauseResumeButton.setOnClickListener(v -> togglePauseResume());
        stopButton.setOnClickListener(v -> stopPractice());

        pauseResumeButton.setEnabled(false);
        stopButton.setEnabled(false);
    }

    private void startPractice() {
        isRunning = true;
        startTime = System.currentTimeMillis();
        sensorManager.registerListener(this, stepCounterSensor, SensorManager.SENSOR_DELAY_NORMAL);
        timerHandler.post(timerRunnable);
        startButton.setEnabled(false);
        pauseResumeButton.setEnabled(true);
        stopButton.setEnabled(true);
    }

    private void togglePauseResume() {
        if (isRunning) {
            isRunning = false;
            sensorManager.unregisterListener(this);
            timerHandler.removeCallbacks(timerRunnable);
            pauseResumeButton.setText("Resume");
        } else {
            isRunning = true;
            startTime = System.currentTimeMillis() - (Long.parseLong(timerTextView.getText().toString().split(":")[0]) * 60000
                    + Long.parseLong(timerTextView.getText().toString().split(":")[1]) * 1000);
            sensorManager.registerListener(this, stepCounterSensor, SensorManager.SENSOR_DELAY_NORMAL);
            timerHandler.post(timerRunnable);
            pauseResumeButton.setText("Pause");
        }
    }

    private void stopPractice() {
        isRunning = false;
        sensorManager.unregisterListener(this);
        timerHandler.removeCallbacks(timerRunnable);
        // TODO: Save practice data
        resetUI();
    }

    private void resetUI() {
        stepCount = 0;
        stepCounterTextView.setText("0 STEPS");
        timerTextView.setText("00:00");
        startButton.setEnabled(true);
        pauseResumeButton.setEnabled(false);
        stopButton.setEnabled(false);
        pauseResumeButton.setText("Pause");
    }

    private Runnable timerRunnable = new Runnable() {
        @Override
        public void run() {
            long millis = System.currentTimeMillis() - startTime;
            int seconds = (int) (millis / 1000);
            int minutes = seconds / 60;
            seconds = seconds % 60;
            timerTextView.setText(String.format(Locale.getDefault(), "%02d:%02d", minutes, seconds));
            if (isRunning) {
                timerHandler.postDelayed(this, 500);
            }
        }
    };

    @Override
    public void onSensorChanged(SensorEvent event) {
        if (event.sensor.getType() == Sensor.TYPE_STEP_COUNTER) {
            stepCount++;
            stepCounterTextView.setText(String.format(Locale.getDefault(), "%d STEPS", stepCount));
        }
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {
        // Not used in this implementation
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        sensorManager.unregisterListener(this);
        timerHandler.removeCallbacks(timerRunnable);
    }
}