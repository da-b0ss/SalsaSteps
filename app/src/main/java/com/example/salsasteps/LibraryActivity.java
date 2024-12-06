package com.example.salsasteps;

import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.RatingBar;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

public class LibraryActivity extends AppCompatActivity {

    private RecyclerView movesRecyclerView;
    private MoveAdapter moveAdapter;
    private SharedPreferences sharedPreferences;
    private SharedPreferences favoritePreferences;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_library);

        sharedPreferences = getSharedPreferences("DanceMoveRatings", MODE_PRIVATE);
        favoritePreferences = getSharedPreferences("FavoriteRatings", MODE_PRIVATE);

        movesRecyclerView = findViewById(R.id.movesRecyclerView);
        movesRecyclerView.setLayoutManager(new LinearLayoutManager(this));

        List<DanceMove> moves = createMovesList();
        moveAdapter = new MoveAdapter(moves);
        movesRecyclerView.setAdapter(moveAdapter);
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
        String notes;

        DanceMove(String name, String difficulty, String videoUrl) {
            this.name = name;
            this.difficulty = difficulty;
            this.videoUrl = videoUrl;
            this.notes = "";
        }
    }

    private class MoveAdapter extends RecyclerView.Adapter<MoveAdapter.MoveViewHolder> {
        private List<DanceMove> moves;
        private SharedPreferences notesPreferences;

        MoveAdapter(List<DanceMove> moves) {
            this.moves = moves;
            this.notesPreferences = getSharedPreferences("DanceMoveNotes", MODE_PRIVATE);
        }

        @NonNull
        @Override
        public MoveViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_dance_move, parent, false);
            return new MoveViewHolder(view);
        }

        @Override
        public int getItemCount() {
            return moves.size();
        }

        @Override
        public void onBindViewHolder(@NonNull MoveViewHolder holder, int position) {
            DanceMove move = moves.get(position);
            holder.nameTextView.setText(move.name);
            holder.difficultyTextView.setText(move.difficulty);

            // Load the saved rating
            float savedRating = sharedPreferences.getFloat(move.name, 0f);
            holder.ratingBar.setRating(savedRating);

            holder.ratingBar.setOnRatingBarChangeListener((ratingBar, rating, fromUser) -> {
                if (fromUser) {
                    SharedPreferences.Editor editor = sharedPreferences.edit();
                    editor.putFloat(move.name, rating);
                    editor.apply();
                    Toast.makeText(LibraryActivity.this, "Rating saved: " + rating, Toast.LENGTH_SHORT).show();
                }
            });

            // Load the saved favorite ratings
            float favoriteRating = favoritePreferences.getFloat(move.name, 0f);
            holder.favoriteBar.setRating(favoriteRating);

            holder.favoriteBar.setOnRatingBarChangeListener((ratingBar, rating, fromUser) -> {
                if (fromUser) {
                    SharedPreferences.Editor favEditor = favoritePreferences.edit();
                    favEditor.putFloat(move.name, rating);
                    favEditor.apply();
                    Toast.makeText(LibraryActivity.this, "Favorite saved", Toast.LENGTH_SHORT).show();
                }
            });

            // Add notes button click handler
            holder.notesButton.setOnClickListener(v -> showNotesDialog(move));

            holder.watchVideoButton.setOnClickListener(v -> {
                Intent intent = new Intent(Intent.ACTION_VIEW);
                intent.setData(Uri.parse(move.videoUrl));
                intent.setPackage("com.android.chrome");
                if (intent.resolveActivity(getPackageManager()) == null) {
                    intent.setPackage(null);
                }
                startActivity(intent);
            });
        }

        private void showNotesDialog(DanceMove move) {
            AlertDialog.Builder builder = new AlertDialog.Builder(LibraryActivity.this);
            View dialogView = LayoutInflater.from(LibraryActivity.this)
                    .inflate(R.layout.dialog_notes, null);
            EditText notesEdit = dialogView.findViewById(R.id.notesEditText);

            // Load existing notes
            String savedNotes = notesPreferences.getString(move.name, "");
            notesEdit.setText(savedNotes);

            builder.setView(dialogView)
                    .setTitle("Notes for " + move.name)
                    .setPositiveButton("Save", (dialog, which) -> {
                        String notes = notesEdit.getText().toString();
                        SharedPreferences.Editor editor = notesPreferences.edit();
                        editor.putString(move.name, notes);
                        editor.apply();
                        move.notes = notes;
                        Toast.makeText(LibraryActivity.this, "Notes saved", Toast.LENGTH_SHORT).show();
                    })
                    .setNegativeButton("Cancel", null)
                    .show();
        }

        class MoveViewHolder extends RecyclerView.ViewHolder {
            TextView nameTextView;
            TextView difficultyTextView;
            RatingBar ratingBar;
            RatingBar favoriteBar;
            Button watchVideoButton;
            Button notesButton;

            MoveViewHolder(View itemView) {
                super(itemView);
                nameTextView = itemView.findViewById(R.id.nameTextView);
                difficultyTextView = itemView.findViewById(R.id.difficultyTextView);
                ratingBar = itemView.findViewById(R.id.ratingBar);
                favoriteBar = itemView.findViewById(R.id.favoriteBar);
                watchVideoButton = itemView.findViewById(R.id.watchVideoButton);
                notesButton = itemView.findViewById(R.id.notesButton);
            }
        }
    }
}
