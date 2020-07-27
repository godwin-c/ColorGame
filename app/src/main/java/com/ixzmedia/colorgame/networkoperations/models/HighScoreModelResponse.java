package com.ixzmedia.colorgame.networkoperations.models;

import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;

public class HighScoreModelResponse {
    @SerializedName("_id")
    private String _id;
    @SerializedName("user_id")
    private String user_id;
    @SerializedName("highscore")
    private int highscore;
    @SerializedName("game_level")
    private String game_level;
    @SerializedName("highscore_date")
    private String highscore_date;

    @SerializedName("user_name")
    private String user_name;
    @SerializedName("photo_url")
    private String photo_url;

    @SerializedName("_created")
    private String created;
    @SerializedName("_changed")
    private String changed;
    @SerializedName("_createdby")
    private String _createdby;
    @SerializedName("_changedby")
    private String _changedby;
    @SerializedName("_keywords")
    private ArrayList<String> _keywords;
    @SerializedName("_tags")
    private String _tags;
    @SerializedName("_version")
    private int _version;


    public HighScoreModelResponse(String _id, String user_id, int highscore, String game_level, String highscore_date, String user_name, String photo_url, String created, String changed, String _createdby, String _changedby, ArrayList<String> _keywords, String _tags, int _version) {
        this._id = _id;
        this.user_id = user_id;
        this.highscore = highscore;
        this.game_level = game_level;
        this.highscore_date = highscore_date;
        this.user_name = user_name;
        this.photo_url = photo_url;
        this.created = created;
        this.changed = changed;
        this._createdby = _createdby;
        this._changedby = _changedby;
        this._keywords = _keywords;
        this._tags = _tags;
        this._version = _version;
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

    public String getHighscore_date() {
        return highscore_date;
    }

    public void setHighscore_date(String highscore_date) {
        this.highscore_date = highscore_date;
    }

    public String get_id() {
        return _id;
    }

    public void set_id(String _id) {
        this._id = _id;
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

    public String getCreated() {
        return created;
    }

    public void setCreated(String created) {
        this.created = created;
    }

    public String getChanged() {
        return changed;
    }

    public void setChanged(String changed) {
        this.changed = changed;
    }

    public String get_createdby() {
        return _createdby;
    }

    public void set_createdby(String _createdby) {
        this._createdby = _createdby;
    }

    public String get_changedby() {
        return _changedby;
    }

    public void set_changedby(String _changedby) {
        this._changedby = _changedby;
    }

    public ArrayList<String> get_keywords() {
        return _keywords;
    }

    public void set_keywords(ArrayList<String> _keywords) {
        this._keywords = _keywords;
    }

    public String get_tags() {
        return _tags;
    }

    public void set_tags(String _tags) {
        this._tags = _tags;
    }

    public int get_version() {
        return _version;
    }

    public void set_version(int _version) {
        this._version = _version;
    }
}
