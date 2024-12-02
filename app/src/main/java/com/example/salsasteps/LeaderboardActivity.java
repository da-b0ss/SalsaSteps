package com.example.salsasteps;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

public class LeaderboardActivity extends AppCompatActivity {

    private RecyclerView leaderboardLayoutView;
    private LeaderboardActivity.LeaderboardAdapter leaderboardAdapter;
    private SharedPreferences sharedPreferences;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_leaderboards);

        sharedPreferences = getSharedPreferences("DanceMoveRatings", MODE_PRIVATE);

        leaderboardLayoutView = findViewById(R.id.leaderboardLayoutView);
        leaderboardLayoutView.setLayoutManager(new LinearLayoutManager(this));

        List<LeaderboardActivity.LeaderboardEntry> moves = createTestLeaderboard();
        leaderboardAdapter = new LeaderboardActivity.LeaderboardAdapter(moves);
        leaderboardLayoutView.setAdapter(leaderboardAdapter);
    }

    private List<LeaderboardActivity.LeaderboardEntry> createTestLeaderboard() {
        List<LeaderboardActivity.LeaderboardEntry> lb = new ArrayList<>();
        lb.add(new LeaderboardActivity.LeaderboardEntry("Nate", 15));
        lb.add(new LeaderboardActivity.LeaderboardEntry("Robert", 10));
        return lb;
    }

    private static class LeaderboardEntry {
        String name;
        int stepCount;

        LeaderboardEntry(String name, int stepCount) {
            this.name = name;
            this.stepCount = stepCount;
        }
    }

    private class LeaderboardAdapter extends RecyclerView.Adapter<LeaderboardActivity.LeaderboardAdapter.EntryViewHolder> {
        private List<LeaderboardActivity.LeaderboardEntry> lb;

        LeaderboardAdapter(List<LeaderboardActivity.LeaderboardEntry> lb) {
            this.lb = lb;
        }

        @NonNull
        @Override
        public LeaderboardActivity.LeaderboardAdapter.EntryViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.leaderboard_entry, parent, false);
            return new LeaderboardActivity.LeaderboardAdapter.EntryViewHolder(view);
        }

        @Override
        public void onBindViewHolder(@NonNull LeaderboardActivity.LeaderboardAdapter.EntryViewHolder holder, int position) {
            LeaderboardActivity.LeaderboardEntry move = lb.get(position);
            holder.nameTextView.setText(move.name);
            String sc = "" + move.stepCount;
            holder.stepCountTextView.setText(sc);

        }

        @Override
        public int getItemCount() {
            return lb.size();
        }

        class EntryViewHolder extends RecyclerView.ViewHolder {
            TextView nameTextView;
            TextView stepCountTextView;

            EntryViewHolder(View itemView) {
                super(itemView);
                nameTextView = itemView.findViewById(R.id.nameTextView);
                stepCountTextView = itemView.findViewById(R.id.stepCountView);
            }
        }
    }
}
