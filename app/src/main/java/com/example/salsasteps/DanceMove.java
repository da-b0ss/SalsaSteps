package com.example.salsasteps;

public class DanceMove {
    private String name;
    private String difficulty;
    private String videoFileName;
    private float rating;

    public DanceMove(String name, String difficulty, String videoFileName) {
        this.name = name;
        this.difficulty = difficulty;
        this.videoFileName = videoFileName;
        this.rating = 0;
    }

    public String getName() {
        return name;
    }

    public String getDifficulty() {
        return difficulty;
    }

    public String getVideoFileName() {
        return videoFileName;
    }

    public float getRating() {
        return rating;
    }

    public void setRating(float rating) {
        this.rating = rating;
    }
}
