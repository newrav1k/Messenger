package com.mirea.kt.ribo.messenger.users;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.google.firebase.auth.FirebaseAuth;
import com.mirea.kt.ribo.messenger.R;
import com.mirea.kt.ribo.messenger.utils.ChatUtil;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Objects;

import de.hdodenhof.circleimageview.CircleImageView;

public class UserAdapter extends RecyclerView.Adapter<UserAdapter.ViewHolder> {

    public interface onUserClickListener {
        void onUserClickListener(User user, int position);
    }

    private onUserClickListener onUserClickListener;
    private ArrayList<User> users;

    public UserAdapter(ArrayList<User> users, onUserClickListener onUserClickListener) {
        this.users = users;
        this.onUserClickListener = onUserClickListener;
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

        holder.username.setText(user.getUsername());

        if (!user.getProfileImage().isEmpty()) {
            Glide.with(holder.itemView.getContext())
                    .load(user.getProfileImage())
                    .into(holder.profile_image);
        } else {
            holder.profile_image.setImageResource(R.drawable.anime_icon);
        }

        holder.itemView.setOnClickListener(v -> ChatUtil.createChat(user));
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