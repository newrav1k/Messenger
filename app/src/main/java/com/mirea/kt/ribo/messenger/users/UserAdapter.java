package com.mirea.kt.ribo.messenger.users;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.mirea.kt.ribo.messenger.R;
import com.mirea.kt.ribo.messenger.utils.SubscriptionUtil;

import java.util.ArrayList;
import java.util.Objects;

import de.hdodenhof.circleimageview.CircleImageView;

public class UserAdapter extends RecyclerView.Adapter<UserAdapter.ViewHolder> {

    public interface OnUserClickListener {
        void onUserClickListener(User user, int position);
    }

    private final ArrayList<User> users;
    private final OnUserClickListener onUserClickListener;

    public UserAdapter(ArrayList<User> users, OnUserClickListener onUserClickListener) {
        this.users = users;
        this.onUserClickListener = onUserClickListener;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_user, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        User user = users.get(position);

        holder.username.setText(user.getUsername());

        String uid = Objects.requireNonNull(FirebaseAuth.getInstance().getCurrentUser()).getUid();
        FirebaseDatabase.getInstance().getReference().child("Users")
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        String subscriptions = Objects.requireNonNull(snapshot.child(uid).child("friends").getValue()).toString();
                        if (subscriptions.contains(user.getUserId())) {
                            holder.subscribeStatus.setImageResource(R.drawable.friend_delete);
                        } else {
                            holder.subscribeStatus.setImageResource(R.drawable.subscribe_black);
                        }
                        holder.username.setText(Objects.requireNonNull(snapshot.child(user.getUserId()).child("username").getValue()).toString());
                        String profile_image = Objects.requireNonNull(snapshot.child(user.getUserId()).child("profile_image").getValue()).toString();
                        if (!profile_image.isEmpty()) {
                            Glide.with(holder.itemView.getContext())
                                    .load(profile_image)
                                    .into(holder.profile_image);
                        } else {
                            holder.profile_image.setImageResource(R.drawable.anime_icon);
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });

        holder.itemView.setOnClickListener(v -> onUserClickListener.onUserClickListener(user, holder.getAdapterPosition()));
        holder.subscribeStatus.setOnClickListener(v -> {
            FirebaseDatabase.getInstance().getReference()
                    .child("Users").child(uid).child("friends").addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                            String subscriptions = Objects.requireNonNull(snapshot.getValue()).toString();
                            if (subscriptions.contains(user.getUserId())) {
                                holder.subscribeStatus.setImageResource(R.drawable.subscribe_black);
                                SubscriptionUtil.unsubscribe(user.getUserId());
                            } else {
                                holder.subscribeStatus.setImageResource(R.drawable.friend_delete);
                                SubscriptionUtil.subscribe(user.getUserId());
                            }
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError error) {

                        }
                    });
        });
    }

    @Override
    public int getItemCount() {
        return users.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        private final CircleImageView profile_image;
        private final TextView username;
        private final ImageButton subscribeStatus;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            profile_image = itemView.findViewById(R.id.user_image);
            username = itemView.findViewById(R.id.username);
            subscribeStatus = itemView.findViewById(R.id.subscribe_status);
        }
    }
}