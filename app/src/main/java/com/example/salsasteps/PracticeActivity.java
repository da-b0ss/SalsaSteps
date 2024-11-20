package com.example.salsasteps;

import android.content.Context;
import android.content.Intent;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.widget.Button;
import android.widget.CalendarView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.concurrent.TimeUnit;

public class PracticeActivity extends AppCompatActivity implements SensorEventListener {

    private TextView stepCounterTextView;
    private TextView timerTextView;
    private TextView selectedDateStats;
    private Button startButton;
    private Button pauseResumeButton;
    private Button stopButton;
    private Button deleteDateButton;
    private CalendarView calendarView;

    private SensorManager sensorManager;
    private Sensor stepCounterSensor;
    private PracticeSessionDbHelper dbHelper;

    private int stepCount = 0;
    private long startTime = 0;
    private long elapsedTimeBeforePause = 0; // Track time accumulated before pauses
    private boolean isRunning = false;
    private Handler timerHandler = new Handler();
    private String selectedDate;
    private SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
    private static final int EXPORT_REQUEST_CODE = 123;
    private FileManager fileManager;
    private Button exportButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_practice);

        dbHelper = new PracticeSessionDbHelper(this);
        setupViews();
        setupListeners();
        selectedDate = dateFormat.format(new Date());
        updateSelectedDateStats();
        fileManager = new FileManager(this);
        exportButton = findViewById(R.id.exportButton);
        exportButton.setOnClickListener(v -> initiateExport());

    }

    private void initiateExport() {
        Intent intent = fileManager.createExportIntent();
        startActivityForResult(intent, EXPORT_REQUEST_CODE);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == EXPORT_REQUEST_CODE && resultCode == RESULT_OK) {
            if (data != null && data.getData() != null) {
                Uri uri = data.getData();
                fileManager.exportCSV(uri);
                Toast.makeText(this, "Progress exported successfully!", Toast.LENGTH_SHORT).show();
            }
        }
    }

    private void setupViews() {
        stepCounterTextView = findViewById(R.id.stepCounterTextView);
        timerTextView = findViewById(R.id.timerTextView);
        selectedDateStats = findViewById(R.id.selectedDateStats);
        startButton = findViewById(R.id.startButton);
        pauseResumeButton = findViewById(R.id.pauseResumeButton);
        stopButton = findViewById(R.id.stopButton);
        deleteDateButton = findViewById(R.id.deleteDateButton);
        calendarView = findViewById(R.id.calendarView);

        sensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
        stepCounterSensor = sensorManager.getDefaultSensor(Sensor.TYPE_STEP_COUNTER);

        pauseResumeButton.setEnabled(false);
        stopButton.setEnabled(false);
    }

    private void setupListeners() {
        startButton.setOnClickListener(v -> startPractice());
        pauseResumeButton.setOnClickListener(v -> togglePauseResume());
        stopButton.setOnClickListener(v -> stopPractice());
        deleteDateButton.setOnClickListener(v -> deleteCurrentDateRecords());

        calendarView.setOnDateChangeListener((view, year, month, dayOfMonth) -> {
            selectedDate = String.format(Locale.getDefault(), "%d-%02d-%02d", year, month + 1, dayOfMonth);
            updateSelectedDateStats();
        });
    }

    private void startPractice() {
        isRunning = true;
        startTime = System.currentTimeMillis();
        elapsedTimeBeforePause = 0; // Reset accumulated time
        sensorManager.registerListener(this, stepCounterSensor, SensorManager.SENSOR_DELAY_NORMAL);
        timerHandler.post(timerRunnable);
        startButton.setEnabled(false);
        pauseResumeButton.setEnabled(true);
        stopButton.setEnabled(true);
    }

    private void togglePauseResume() {
        if (isRunning) {
            // Pause
            isRunning = false;
            sensorManager.unregisterListener(this);
            timerHandler.removeCallbacks(timerRunnable);
            elapsedTimeBeforePause += System.currentTimeMillis() - startTime; // Add current session time
            pauseResumeButton.setText("Resume");
        } else {
            // Resume
            isRunning = true;
            startTime = System.currentTimeMillis();
            sensorManager.registerListener(this, stepCounterSensor, SensorManager.SENSOR_DELAY_NORMAL);
            timerHandler.post(timerRunnable);
            pauseResumeButton.setText("Pause");
        }
    }

    private void stopPractice() {
        if (isRunning || elapsedTimeBeforePause > 0) {
            long totalDuration;
            if (isRunning) {
                totalDuration = elapsedTimeBeforePause + (System.currentTimeMillis() - startTime);
            } else {
                totalDuration = elapsedTimeBeforePause;
            }
            String currentDate = dateFormat.format(new Date());
            dbHelper.addSession(currentDate, totalDuration, stepCount);
            fileManager.saveSession(currentDate, totalDuration, stepCount);
            updateSelectedDateStats();
        }

        isRunning = false;
        elapsedTimeBeforePause = 0;
        sensorManager.unregisterListener(this);
        timerHandler.removeCallbacks(timerRunnable);
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

    private void updateSelectedDateStats() {
        PracticeSessionDbHelper.PracticeStats stats = dbHelper.getStatsByDate(selectedDate);

        long hours = TimeUnit.MILLISECONDS.toHours(stats.totalDuration);
        long minutes = TimeUnit.MILLISECONDS.toMinutes(stats.totalDuration) % 60;
        long seconds = TimeUnit.MILLISECONDS.toSeconds(stats.totalDuration) % 60;

        String statsText = String.format(Locale.getDefault(),
                "Date: %s\nTotal time practiced: %02d:%02d:%02d\nTotal steps: %d",
                selectedDate, hours, minutes, seconds, stats.totalSteps);

        selectedDateStats.setText(statsText);
    }

    private void deleteCurrentDateRecords() {
        dbHelper.deleteSessionsByDate(selectedDate);
        fileManager.deleteSessionsByDate(selectedDate);
        updateSelectedDateStats();
        Toast.makeText(this, "Records deleted for " + selectedDate, Toast.LENGTH_SHORT).show();
    }

    private Runnable timerRunnable = new Runnable() {
        @Override
        public void run() {
            long currentElapsedTime;
            if (isRunning) {
                currentElapsedTime = elapsedTimeBeforePause + (System.currentTimeMillis() - startTime);
            } else {
                currentElapsedTime = elapsedTimeBeforePause;
            }

            int seconds = (int) (currentElapsedTime / 1000);
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
        dbHelper.close();
    }
}