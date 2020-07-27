package com.ixzmedia.colorgame.networkoperations.models;

import com.google.gson.annotations.SerializedName;

public class HighScoreModel {
    @SerializedName("user_id")
    private String user_id;
    @SerializedName("highscore")
    private int highscore;
    @SerializedName("game_level")
    private String game_level;
    @SerializedName("highscore_date")
    private String date;
    @SerializedName("user_name")
    private String user_name;
    @SerializedName("photo_url")
    private String photo_url;


    public HighScoreModel(String user_id, int highscore, String game_level, String date, String user_name, String photo_url) {
        this.user_id = user_id;
        this.highscore = highscore;
        this.game_level = game_level;
        this.date = date;
        this.user_name = user_name;
        this.photo_url = photo_url;
    }

    public String getUser_name() {
        return user_name;
    }

    public void setUser_name(String user_name) {
        this.user_name = user_name;
    }

    public String getPhoto_url() {
        return photo_url;
    }

    public void setPhoto_url(String photo_url) {
        this.photo_url = photo_url;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getUser_id() {
        return user_id;
    }

    public void setUser_id(String user_id) {
        this.user_id = user_id;
    }

    public int getHighscore() {
        return highscore;
    }

    public void setHighscore(int highscore) {
        this.highscore = highscore;
    }

    public String getGame_level() {
        return game_level;
    }

    public void setGame_level(String game_level) {
        this.game_level = game_level;
    }
}
