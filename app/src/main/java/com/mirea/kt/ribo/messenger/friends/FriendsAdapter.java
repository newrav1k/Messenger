package com.mirea.kt.ribo.messenger.friends;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.mirea.kt.ribo.messenger.R;
import com.mirea.kt.ribo.messenger.users.User;

import java.util.ArrayList;

import de.hdodenhof.circleimageview.CircleImageView;

public class FriendsAdapter extends RecyclerView.Adapter<FriendsAdapter.ViewHolder> {

    public interface OnFriendClickListener {
        void onFriendClickListener(User user, int position);
    }

    private ArrayList<User> users;
    private final OnFriendClickListener onFriendClickListener;

    public FriendsAdapter(ArrayList<User> users, OnFriendClickListener onFriendClickListener) {
        this.users = users;
        this.onFriendClickListener = onFriendClickListener;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_person, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        User user = users.get(position);
        FirebaseDatabase.getInstance().getReference()
                .child("Users").child(user.getUserId())
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        holder.username.setText(snapshot.child("username").getValue().toString());

                        String profile_image = snapshot.child("profile_image").getValue().toString();
                        if (!profile_image.isEmpty()) {
                            Glide.with(holder.itemView.getContext()).load(profile_image).into(holder.profile_image);
                        } else {
                            holder.profile_image.setImageResource(R.drawable.anime_icon);
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });
        holder.itemView.setOnClickListener(v -> onFriendClickListener.onFriendClickListener(user, holder.getAdapterPosition()));
    }

    @Override
    public int getItemCount() {
        return users.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        CircleImageView profile_image;
        TextView username;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            profile_image = itemView.findViewById(R.id.user_image);
            username = itemView.findViewById(R.id.username);
        }
    }
}