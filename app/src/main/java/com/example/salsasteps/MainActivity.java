package com.example.salsasteps;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import android.app.AlarmManager;
import android.app.PendingIntent;
import java.text.SimpleDateFormat;
import java.util.Random;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;
import java.util.List;
import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {
    private SharedPreferences moveOfDayPrefs;
    private TextView moveOfDayName;
    private TextView moveOfDayDifficulty;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);

        // Initialize views and preferences
        moveOfDayPrefs = getSharedPreferences("MoveOfDayPrefs", MODE_PRIVATE);
        moveOfDayName = findViewById(R.id.moveOfDayName);
        moveOfDayDifficulty = findViewById(R.id.moveOfDayDifficulty);

        setupMoveOfTheDay();
        schedulePracticeReminder();

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.dance_move_lib), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        Button practiceButton = findViewById(R.id.button);
        Button libraryButton = findViewById(R.id.button2);

        practiceButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, PracticeActivity.class);
                startActivity(intent);
            }
        });

        libraryButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, LibraryActivity.class);
                startActivity(intent);
            }
        });

        
    }

    private void setupMoveOfTheDay() {
        String today = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(new Date());
        String lastUpdated = moveOfDayPrefs.getString("last_updated", "");

        if (!today.equals(lastUpdated)) {
            // Get a new random move
            List<DanceMove> moves = createMovesList();
            Random random = new Random();
            DanceMove moveOfDay = moves.get(random.nextInt(moves.size()));

            // Save the new move and date
            SharedPreferences.Editor editor = moveOfDayPrefs.edit();
            editor.putString("last_updated", today);
            editor.putString("move_name", moveOfDay.name);
            editor.putString("move_difficulty", moveOfDay.difficulty);
            editor.apply();
        }

        moveOfDayName.setText(moveOfDayPrefs.getString("move_name", "Basic Steps"));
        moveOfDayDifficulty.setText(moveOfDayPrefs.getString("move_difficulty", "Beginner"));
    }

    private void schedulePracticeReminder() {
        AlarmManager alarmManager = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
        Intent intent = new Intent(this, PracticeReminderReceiver.class);
        PendingIntent pendingIntent = PendingIntent.getBroadcast(
                this,
                0,
                intent,
                PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE
        );

        Calendar calendar = Calendar.getInstance();
        calendar.set(Calendar.HOUR_OF_DAY, 18);
        calendar.set(Calendar.MINUTE, 0);
        calendar.set(Calendar.SECOND, 0);

        if (calendar.getTimeInMillis() < System.currentTimeMillis()) {
            calendar.add(Calendar.DAY_OF_YEAR, 1);
        }

        alarmManager.setRepeating(
                AlarmManager.RTC_WAKEUP,
                calendar.getTimeInMillis(),
                AlarmManager.INTERVAL_DAY,
                pendingIntent
        );
    }

    private List<DanceMove> createMovesList() {
        List<DanceMove> moves = new ArrayList<>();
        moves.add(new DanceMove("Basic Steps", "Beginner", "https://www.youtube.com/watch?v=24i7zR2LKwk"));
        moves.add(new DanceMove("Para Abajo", "Beginner", "https://www.youtube.com/watch?v=W-K3VEjbCHw"));
        moves.add(new DanceMove("Enchufla Complicado", "Intermediate", "https://www.youtube.com/watch?v=Hq2OBXdCIfk"));
        moves.add(new DanceMove("Adios", "Intermediate", "https://www.youtube.com/watch?v=p29w1PoiKOE"));
        moves.add(new DanceMove("El Sombrero", "Difficult", "https://www.youtube.com/watch?v=AAcX5hcJwi0"));
        moves.add(new DanceMove("Setenta", "Difficult", "https://www.youtube.com/watch?v=_B55w-0o0qw"));
        return moves;
    }

    private static class DanceMove {
        String name;
        String difficulty;
        String videoUrl;

        DanceMove(String name, String difficulty, String videoUrl) {
            this.name = name;
            this.difficulty = difficulty;
            this.videoUrl = videoUrl;
        }
    }
}