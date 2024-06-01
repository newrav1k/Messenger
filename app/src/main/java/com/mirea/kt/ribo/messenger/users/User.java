package com.mirea.kt.ribo.messenger.users;

import java.util.Comparator;

public class User implements Comparator<User> {
    private String userId;
    private String username;
    private String profileImage;

    public User(String username, String profileImage) {
        this.username = username;
        this.profileImage = profileImage;
    }

    public User(String userId, String username, String profileImage) {
        this.userId = userId;
        this.username = username;
        this.profileImage = profileImage;
    }

    public String getUsername() {
        return username;
    }

    public String getProfileImage() {
        return profileImage;
    }

    public String getUserId() {
        return userId;
    }

    @Override
    public int compare(User o1, User o2) {
        return o1.getUsername().compareTo(o2.getUsername());
    }
}