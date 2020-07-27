package com.ixzmedia.colorgame.networkoperations.models;

import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;
import java.util.HashMap;

public class MyErrorClass {
    @SerializedName("message")
    private String message;
    @SerializedName("name")
    private String name;
    @SerializedName("status")
    private int status;
    @SerializedName("list")
    private ArrayList<HashMap> list;

    public MyErrorClass(String message, String name, int status, ArrayList<HashMap> list) {
        this.message = message;
        this.name = name;
        this.status = status;
        this.list = list;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public ArrayList<HashMap> getList() {
        return list;
    }

    public void setList(ArrayList<HashMap> list) {
        this.list = list;
    }
}
