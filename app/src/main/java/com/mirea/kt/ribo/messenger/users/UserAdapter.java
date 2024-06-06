package com.mirea.kt.ribo.messenger.users;

import android.util.Log;
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
    private static final String TAG = "UserAdapter";

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
        Log.i(TAG, "onBindViewHolder: get user");
        holder.username.setText(user.getUsername());
        Log.i(TAG, "onBindViewHolder: holder.username set text user.getUsername()");
        String uid = Objects.requireNonNull(FirebaseAuth.getInstance().getCurrentUser()).getUid();
        Log.i(TAG, "onBindViewHolder: FirebaseAuth get user id");
        FirebaseDatabase.getInstance().getReference().child("Users")
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        String subscriptions = Objects.requireNonNull(snapshot.child(uid).child("subscriptions").getValue()).toString();
                        Log.i(TAG, "onDataChange: get subscriptions");
                        if (subscriptions.contains(user.getUserId())) {
                            holder.subscribeStatus.setImageResource(R.drawable.subscriptions_delete);
                            Log.i(TAG, "onDataChange: holder.subscribeStatus set image subscriptions_delete");
                        } else {
                            holder.subscribeStatus.setImageResource(R.drawable.subscribe_black);
                            Log.i(TAG, "onDataChange: holder.subscribeStatus set image subscribe_black");
                        }
                        holder.username.setText(Objects.requireNonNull(snapshot.child(user.getUserId()).child("username").getValue()).toString());
                        Log.i(TAG, "onDataChange: holder.username set text username");
                        String profile_image = Objects.requireNonNull(snapshot.child(user.getUserId()).child("profile_image").getValue()).toString();
                        Log.i(TAG, "onDataChange: get profile_image");
                        if (!profile_image.isEmpty()) {
                            Glide.with(holder.itemView.getContext())
                                    .load(profile_image)
                                    .into(holder.profile_image);
                            Log.i(TAG, "onDataChange: load profile_image to holder.profile_image");
                        } else {
                            holder.profile_image.setImageResource(R.drawable.anime_icon);
                            Log.i(TAG, "onDataChange: holder.profile_image set image anime_icon");
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });

        holder.itemView.setOnClickListener(v -> onUserClickListener.onUserClickListener(user, holder.getAdapterPosition()));
        holder.subscribeStatus.setOnClickListener(v -> {
            Log.i(TAG, "onBindViewHolder: pressing holder.subscribeStatus");
            FirebaseDatabase.getInstance().getReference()
                    .child("Users").child(uid).child("subscriptions").addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                            String subscriptions = Objects.requireNonNull(snapshot.getValue()).toString();
                            Log.i(TAG, "onDataChange: get subscriptions");
                            if (subscriptions.contains(user.getUserId())) {
                                holder.subscribeStatus.setImageResource(R.drawable.subscribe_black);
                                Log.i(TAG, "onDataChange: holder.subscribeStatus set image subscribe_black");
                                SubscriptionUtil.unsubscribe(user.getUserId());
                                Log.i(TAG, "onDataChange: SubscriptionUtil call .unsubscribe(user.getUserId())");
                            } else {
                                holder.subscribeStatus.setImageResource(R.drawable.subscriptions_delete);
                                Log.i(TAG, "onDataChange: holder.subscribeStatus set image subscriptions_delete");
                                SubscriptionUtil.subscribe(user.getUserId());
                                Log.i(TAG, "onDataChange: SubscriptionUtil call .subscribe(user.getUserId())");
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
            Log.i(TAG, "ViewHolder: create user_image");
            username = itemView.findViewById(R.id.username);
            Log.i(TAG, "ViewHolder: create username");
            subscribeStatus = itemView.findViewById(R.id.subscribe_status);
            Log.i(TAG, "ViewHolder: create subscribe_status");
        }
    }
}