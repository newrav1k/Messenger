package com.mirea.kt.ribo.messenger.utils;

import androidx.annotation.NonNull;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class FriendUtil {

    public static void addToFriends(String friendId) {
        String uid = FirebaseAuth.getInstance().getCurrentUser().getUid();
        FirebaseDatabase.getInstance().getReference()
                .child("Users").child(uid).child("friends")
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        if (!snapshot.exists()) {
                            FirebaseDatabase.getInstance().getReference()
                                    .child("Users").child(uid).child("friends")
                                    .setValue(updateFriends("", friendId));
                        }
                        FirebaseDatabase.getInstance().getReference()
                                .child("Users").child(uid).child("friends")
                                .setValue(updateFriends(snapshot.getValue().toString(), friendId));
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });
    }

    private static String updateFriends(String str, String userId) {
        if (str.isEmpty()) {
            str += userId;
        } else {
            str += "," + userId;
        }
        return str;
    }
}