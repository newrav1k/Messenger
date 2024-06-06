package com.mirea.kt.ribo.messenger.utils;

import androidx.annotation.NonNull;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.Objects;

public class SubscriptionUtil {

    public static void subscribe(String userId) {
        String uid = Objects.requireNonNull(FirebaseAuth.getInstance().getCurrentUser()).getUid();
        FirebaseDatabase.getInstance().getReference()
                .child("Users").child(uid).child("subscriptions")
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        if (!snapshot.exists()) {
                            FirebaseDatabase.getInstance().getReference()
                                    .child("Users").child(uid).child("subscriptions")
                                    .setValue(updateFriends("", userId));
                        }
                        FirebaseDatabase.getInstance().getReference()
                                .child("Users").child(uid).child("subscriptions")
                                .setValue(updateFriends(Objects.requireNonNull(snapshot.getValue()).toString(), userId));
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });
    }

    public static void unsubscribe(String userId) {
        String uid = Objects.requireNonNull(FirebaseAuth.getInstance().getCurrentUser()).getUid();
        FirebaseDatabase.getInstance().getReference()
                .child("Users").child(uid).child("subscriptions")
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        String subscriptions = Objects.requireNonNull(snapshot.getValue()).toString();
                        String new_subscriptions;
                        String[] stringBuffer = subscriptions.split(",");
                        ArrayList<String> buffer = new ArrayList<>();
                        Collections.addAll(buffer, stringBuffer);
                        if (buffer.indexOf(userId) == 0) {
                            new_subscriptions = subscriptions.replace(userId, "");
                        } else {
                            new_subscriptions = subscriptions.replace("," + userId, "");
                        }
//                        String new_subscriptions = subscriptions.replace(userId, "");
                        FirebaseDatabase.getInstance().getReference()
                                .child("Users").child(uid).child("subscriptions")
                                .setValue(new_subscriptions);
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });
    }

    private static String updateFriends(String str, String userId) {
        if (str.contains(userId)) {
            return str;
        }
        if (str.isEmpty()) {
            str += userId;
        } else {
            str += "," + userId;
        }
        return str;
    }
}