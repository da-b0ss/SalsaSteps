package com.example.salsasteps;

import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.RatingBar;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

public class LibraryActivity extends AppCompatActivity {

    private RecyclerView movesRecyclerView;
    private MoveAdapter moveAdapter;
    private SharedPreferences sharedPreferences;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_library);

        sharedPreferences = getSharedPreferences("DanceMoveRatings", MODE_PRIVATE);

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

        DanceMove(String name, String difficulty, String videoUrl) {
            this.name = name;
            this.difficulty = difficulty;
            this.videoUrl = videoUrl;
        }
    }

    private class MoveAdapter extends RecyclerView.Adapter<MoveAdapter.MoveViewHolder> {
        private List<DanceMove> moves;

        MoveAdapter(List<DanceMove> moves) {
            this.moves = moves;
        }

        @NonNull
        @Override
        public MoveViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_dance_move, parent, false);
            return new MoveViewHolder(view);
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
                    // Save the new rating
                    SharedPreferences.Editor editor = sharedPreferences.edit();
                    editor.putFloat(move.name, rating);
                    editor.apply();
                    Toast.makeText(LibraryActivity.this, "Rating saved: " + rating, Toast.LENGTH_SHORT).show();
                }
            });

            holder.watchVideoButton.setOnClickListener(v -> {
                Intent intent = new Intent(Intent.ACTION_VIEW);
                intent.setData(Uri.parse(move.videoUrl));
                intent.setPackage("com.android.chrome");  // Prefer Chrome if available
                if (intent.resolveActivity(getPackageManager()) == null) {
                    // If Chrome is not installed, fall back to the default browser
                    intent.setPackage(null);
                }
                startActivity(intent);
            });
        }

        @Override
        public int getItemCount() {
            return moves.size();
        }

        class MoveViewHolder extends RecyclerView.ViewHolder {
            TextView nameTextView;
            TextView difficultyTextView;
            RatingBar ratingBar;
            Button watchVideoButton;

            MoveViewHolder(View itemView) {
                super(itemView);
                nameTextView = itemView.findViewById(R.id.nameTextView);
                difficultyTextView = itemView.findViewById(R.id.difficultyTextView);
                ratingBar = itemView.findViewById(R.id.ratingBar);
                watchVideoButton = itemView.findViewById(R.id.watchVideoButton);
            }
        }
    }
}