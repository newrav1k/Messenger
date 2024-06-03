package com.mirea.kt.ribo.messenger.utils;

import androidx.annotation.NonNull;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.mirea.kt.ribo.messenger.users.User;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Objects;

public class ChatUtil {
    public static void createChat(User user) {
        FirebaseDatabase.getInstance().getReference()
                .child("Chats").child(generateChatId(FirebaseAuth.getInstance().getCurrentUser().getUid(), user.getUserId()))
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        if (!snapshot.exists()) {
                            String uid = Objects.requireNonNull(FirebaseAuth.getInstance().getCurrentUser()).getUid();
                            HashMap<String, String> chatInfo = new HashMap<>();
                            chatInfo.put("user1", uid);
                            chatInfo.put("user2", user.getUserId());

                            String chatId = generateChatId(uid, user.getUserId());
                            FirebaseDatabase.getInstance().getReference().child("Chats").child(chatId)
                                    .setValue(chatInfo);
                            addChatIdToUser(uid, chatId);
                            addChatIdToUser(user.getUserId(), chatId);
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });
    }

    private static String generateChatId(String userId1, String userId2) {
        String sumUser1User2 = userId1 + userId2;
        char[] charArray = sumUser1User2.toCharArray();
        Arrays.sort(charArray);

        return new String(charArray);
    }

    private static void addChatIdToUser(String userId, String chatId) {
        FirebaseDatabase.getInstance().getReference().child("Users").child(userId)
                .child("chats").get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        String chats = task.getResult().getValue().toString();

                        if (!chats.contains(chatId)) {
                            String chatsUpdated = addIdToString(chats, chatId);

                            FirebaseDatabase.getInstance().getReference().child("Users").child(userId)
                                    .child("chats").setValue(chatsUpdated);
                        }
                    }
                });
    }

    private static String addIdToString(String str, String chatId) {
        if (str.isEmpty()) {
            str += chatId;
        } else {
            str += "," + chatId;
        }
        return str;
    }

    public static String getChatId(User user) {
        return generateChatId(Objects.requireNonNull(FirebaseAuth.getInstance()
                .getCurrentUser()).getUid(), user.getUserId());
    }

    public static boolean isExistingChat(User user) {
        return FirebaseDatabase.getInstance().getReference()
                .child("Chats").child(generateChatId(FirebaseAuth.getInstance().getCurrentUser().getUid(), user.getUserId()))
                .get().isSuccessful();
    }
}