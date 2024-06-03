package com.mirea.kt.ribo.messenger.utils;

import androidx.annotation.NonNull;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.mirea.kt.ribo.messenger.users.User;

import java.util.ArrayList;
import java.util.Objects;

public class SubscriptionUtil {

    public static void subscribe(String userId) {
        String uid = Objects.requireNonNull(FirebaseAuth.getInstance().getCurrentUser()).getUid();
        FirebaseDatabase.getInstance().getReference()
                .child("Users").child(uid).child("friends")
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        if (!snapshot.exists()) {
                            FirebaseDatabase.getInstance().getReference()
                                    .child("Users").child(uid).child("friends")
                                    .setValue(updateFriends("", userId));
                        }
                        FirebaseDatabase.getInstance().getReference()
                                .child("Users").child(uid).child("friends")
                                .setValue(updateFriends(Objects.requireNonNull(snapshot.getValue()).toString(), userId));
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });
    }

    public static ArrayList<User> loadAllSubscriptionsFromFirebase() {
        ArrayList<User> friends = new ArrayList<>();
        String uid = Objects.requireNonNull(FirebaseAuth.getInstance().getCurrentUser()).getUid();
        FirebaseDatabase.getInstance().getReference().addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                String friendsStr = Objects.requireNonNull(snapshot.child("Users").child(uid).child("friends").getValue()).toString();
                String[] friendsIds = friendsStr.split(",");

                for (String friendsId : friendsIds) {
                    DataSnapshot usersSnapshot = snapshot.child("Users").child(friendsId);

                    String userId = usersSnapshot.getKey();
                    String username = Objects.requireNonNull(usersSnapshot.child("username").getValue()).toString();
                    String profile_image = Objects.requireNonNull(usersSnapshot.child("profile_image").getValue()).toString();

                    friends.add(new User(userId, username, profile_image));
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
        return friends;
    }

    public static void unsubscribe(String userId) {
        String uid = Objects.requireNonNull(FirebaseAuth.getInstance().getCurrentUser()).getUid();
        FirebaseDatabase.getInstance().getReference()
                .child("Users").child(uid).child("friends")
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        String friends = Objects.requireNonNull(snapshot.getValue()).toString();
                        String[] arr = friends.split(",");
                        for (int i = 0; i < arr.length; i++) {
                            if (arr[i].equals(userId)) {
                                arr[i] = "";
                            }
                        }
                        String new_friends = String.join(",", arr);
                        FirebaseDatabase.getInstance().getReference()
                                .child("Users").child(uid).child("friends")
                                .setValue(new_friends);
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