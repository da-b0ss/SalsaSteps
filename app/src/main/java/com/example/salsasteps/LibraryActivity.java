package com.example.salsasteps;

import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class LibraryActivity extends AppCompatActivity {

    private RecyclerView recyclerView;
    private DanceMoveAdapter adapter;
    private List<DanceMove> danceMoves;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_library);

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        recyclerView = findViewById(R.id.recyclerView);
        recyclerView.setLayoutManager(new GridLayoutManager(this, 2));

        danceMoves = createDanceMoves();
        adapter = new DanceMoveAdapter(this, danceMoves);
        recyclerView.setAdapter(adapter);

        setupSortingSpinner();
    }

    private List<DanceMove> createDanceMoves() {
        List<DanceMove> moves = new ArrayList<>();
        moves.add(new DanceMove("Basic Step", "Beginner", "1.mp4"));
        moves.add(new DanceMove("Cross Body Lead", "Intermediate", "2.mp4"));
        moves.add(new DanceMove("Cuban Motion", "Beginner", "3.mp4"));
        moves.add(new DanceMove("Suzie Q", "Intermediate", "4.mp4"));
        moves.add(new DanceMove("Carousel", "Advanced", "5.mp4"));
        moves.add(new DanceMove("Sombrero", "Advanced", "6.mp4"));
        return moves;
    }

    private void setupSortingSpinner() {
        Spinner sortSpinner = findViewById(R.id.sortSpinner);
        ArrayAdapter<CharSequence> spinnerAdapter = ArrayAdapter.createFromResource(this,
                R.array.sort_options, android.R.layout.simple_spinner_item);
        spinnerAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        sortSpinner.setAdapter(spinnerAdapter);

        sortSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                String selectedOption = parent.getItemAtPosition(position).toString();
                sortDanceMoves(selectedOption);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });
    }

    private void sortDanceMoves(String sortOption) {
        if (sortOption.equals("Ascending Difficulty")) {
            Collections.sort(danceMoves, (m1, m2) -> m1.getDifficulty().compareTo(m2.getDifficulty()));
        } else if (sortOption.equals("Descending Difficulty")) {
            Collections.sort(danceMoves, (m1, m2) -> m2.getDifficulty().compareTo(m1.getDifficulty()));
        }
        adapter.notifyDataSetChanged();
    }
}