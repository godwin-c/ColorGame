package com.ixzmedia.colorgame.classes;

import java.io.Serializable;

public class AppUsers implements Serializable {
    private String name;
    private String email;
    private String photo_url;


    public AppUsers(String name, String email, String photo_url) {
        this.name = name;
        this.email = email;
        this.photo_url = photo_url;
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

    public String getPhoto_url() {
        return photo_url;
    }

    public void setPhoto_url(String photo_url) {
        this.photo_url = photo_url;
    }
}
