package com.ixzmedia.colorgame.networkoperations.models;

import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;

public class UserModelResponse {
    @SerializedName("_id")
    private String _id;
    @SerializedName("name")
    private String name;
    @SerializedName("email_address")
    private String email;
    @SerializedName("photo_url")
    private String profile_url;
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


    public UserModelResponse(String _id, String name, String email, String profile_url, String created, String changed,
                             String _createdby, String _changedby, ArrayList<String> _keywords, String _tags, int _version) {
        this._id = _id;
        this.name = name;
        this.email = email;
        this.profile_url = profile_url;
        this.created = created;
        this.changed = changed;
        this._createdby = _createdby;
        this._changedby = _changedby;
        this._keywords = _keywords;
        this._tags = _tags;
        this._version = _version;
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

    public String get_id() {
        return _id;
    }

    public void set_id(String _id) {
        this._id = _id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getProfile_url() {
        return profile_url;
    }

    public void setProfile_url(String profile_url) {
        this.profile_url = profile_url;
    }

    public String getCreated() {
        return created;
    }

    public void setCreated(String created) {
        this.created = created;
    }
}
