package com.mirea.kt.ribo.messenger.message;

public class Message {
    private String id;
    private String ownerId;
    private String text;
    private String date;
    private String photo;

    public Message(String id, String ownerId, String text, String date) {
        this.id = id;
        this.ownerId = ownerId;
        this.text = text;
        this.date = date;
    }

    public Message(String id, String ownerId, String text, String date, String photo) {
        this.id = id;
        this.ownerId = ownerId;
        this.text = text;
        this.date = date;
        this.photo = photo;
    }

    public String getId() {
        return id;
    }

    public String getOwnerId() {
        return ownerId;
    }

    public String getText() {
        return text;
    }

    public String getDate() {
        return date;
    }

    public String getPhoto() { return photo; }
}