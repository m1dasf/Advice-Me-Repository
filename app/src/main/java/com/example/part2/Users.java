package com.example.part2;

public class Users {
    String username;
    String bio;
    String uid;

    public Users() {
    }

    public Users(String username, String bio, String uid) {
        this.username = username;
        this.bio = bio;
        this.uid = uid;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getBio() {
        return bio;
    }

    public void setBio(String bio) {
        this.bio = bio;
    }

    public String getUid() {
        return uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }
}
