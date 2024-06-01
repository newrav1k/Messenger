package com.mirea.kt.ribo.messenger.chats;

public class Chat {
    private String chatId;
    private String chatName;
    private String userId1;
    private String userId2;

    public Chat(String chatId, String chatName, String userId1, String userId2) {
        this.chatId = chatId;
        this.chatName = chatName;
        this.userId1 = userId1;
        this.userId2 = userId2;
    }

    public String getChatId() {
        return chatId;
    }

    public String getChatName() {
        return chatName;
    }

    public String getUserId1() {
        return userId1;
    }

    public String getUserId2() {
        return userId2;
    }
}