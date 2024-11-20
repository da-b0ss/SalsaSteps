package com.example.salsasteps;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Environment;
import androidx.core.content.FileProvider;
import java.io.*;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.concurrent.TimeUnit;

public class FileManager {
    private static final String FILE_NAME = "practice_sessions.csv";
    private final Context context;
    private final File file;

    public FileManager(Context context) {
        this.context = context;
        File directory = context.getExternalFilesDir(Environment.DIRECTORY_DOCUMENTS);
        this.file = new File(directory, FILE_NAME);

        // Create CSV header if file doesn't exist
        if (!file.exists()) {
            try {
                FileWriter writer = new FileWriter(file, true);
                writer.append("Date,Duration(ms),Steps\n");
                writer.flush();
                writer.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public void saveSession(String date, long duration, int steps) {
        try {
            FileWriter writer = new FileWriter(file, true);
            writer.append(String.format(Locale.US, "%s,%d,%d\n",
                    date, duration, steps));
            writer.flush();
            writer.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void deleteSessionsByDate(String date) {
        try {
            File tempFile = new File(context.getExternalFilesDir(Environment.DIRECTORY_DOCUMENTS), "temp.csv");
            BufferedReader reader = new BufferedReader(new FileReader(file));
            BufferedWriter writer = new BufferedWriter(new FileWriter(tempFile));

            String line;
            // Copy header
            writer.write(reader.readLine() + "\n");

            while ((line = reader.readLine()) != null) {
                String[] values = line.split(",");
                if (!values[0].equals(date)) {
                    writer.write(line + "\n");
                }
            }

            writer.close();
            reader.close();

            // Replace original file
            if (file.delete()) {
                tempFile.renameTo(file);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private String formatDuration(long milliseconds) {
        long hours = TimeUnit.MILLISECONDS.toHours(milliseconds);
        long minutes = TimeUnit.MILLISECONDS.toMinutes(milliseconds) % 60;
        long seconds = TimeUnit.MILLISECONDS.toSeconds(milliseconds) % 60;
        return String.format(Locale.US, "%02d:%02d:%02d", hours, minutes, seconds);
    }

    public Intent createExportIntent() {
        Intent intent = new Intent(Intent.ACTION_CREATE_DOCUMENT);
        intent.addCategory(Intent.CATEGORY_OPENABLE);
        intent.setType("text/csv");

        // Set default file name
        String timestamp = new SimpleDateFormat("yyyyMMdd", Locale.US).format(new Date());
        intent.putExtra(Intent.EXTRA_TITLE, "salsa_progress_" + timestamp + ".csv");

        return intent;
    }

    public void exportCSV(Uri destinationUri) {
        try {
            BufferedReader reader = new BufferedReader(new FileReader(file));
            BufferedWriter writer = new BufferedWriter(
                    new OutputStreamWriter(context.getContentResolver().openOutputStream(destinationUri))
            );

            // Write header with updated duration column name
            writer.write("Date,Duration (HH:MM:SS),Steps\n");

            String line;
            // Skip the original header
            reader.readLine();

            while ((line = reader.readLine()) != null) {
                String[] values = line.split(",");
                if (values.length >= 3) {
                    String date = values[0];
                    long duration = Long.parseLong(values[1]);
                    String steps = values[2];

                    // Format the line with converted duration
                    writer.write(String.format(Locale.US, "%s,%s,%s\n",
                            date,
                            formatDuration(duration),
                            steps));
                }
            }

            writer.flush();
            writer.close();
            reader.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}