package com.example.salsasteps;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RatingBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class DanceMoveAdapter extends RecyclerView.Adapter<DanceMoveAdapter.DanceMoveViewHolder> {

    private Context context;
    private List<DanceMove> danceMoves;

    public DanceMoveAdapter(Context context, List<DanceMove> danceMoves) {
        this.context = context;
        this.danceMoves = danceMoves;
    }

    @NonNull
    @Override
    public DanceMoveViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.dance_move_item, parent, false);
        return new DanceMoveViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull DanceMoveViewHolder holder, int position) {
        DanceMove danceMove = danceMoves.get(position);
        holder.nameTextView.setText(danceMove.getName());
        holder.difficultyTextView.setText(danceMove.getDifficulty());
        holder.ratingBar.setRating(danceMove.getRating());

        holder.itemView.setOnClickListener(v -> {
            Intent intent = new Intent(context, VideoPlayerActivity.class);
            intent.putExtra("videoFileName", danceMove.getVideoFileName());
            context.startActivity(intent);
        });

        holder.ratingBar.setOnRatingBarChangeListener((ratingBar, rating, fromUser) -> {
            if (fromUser) {
                danceMove.setRating(rating);
                // TODO: Save the rating to persistent storage
            }
        });
    }

    @Override
    public int getItemCount() {
        return danceMoves.size();
    }

    static class DanceMoveViewHolder extends RecyclerView.ViewHolder {
        TextView nameTextView;
        TextView difficultyTextView;
        RatingBar ratingBar;

        DanceMoveViewHolder(@NonNull View itemView) {
            super(itemView);
            nameTextView = itemView.findViewById(R.id.nameTextView);
            difficultyTextView = itemView.findViewById(R.id.difficultyTextView);
            ratingBar = itemView.findViewById(R.id.ratingBar);
        }
    }
}