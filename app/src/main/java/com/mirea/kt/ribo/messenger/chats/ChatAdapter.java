package com.mirea.kt.ribo.messenger.chats;

import android.content.Intent;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.FirebaseDatabase;
import com.mirea.kt.ribo.messenger.ChatActivity;
import com.mirea.kt.ribo.messenger.R;

import java.util.ArrayList;
import java.util.Objects;

import de.hdodenhof.circleimageview.CircleImageView;

public class ChatAdapter extends RecyclerView.Adapter<ChatAdapter.ViewHolder> {
    private static final String TAG = "ChatAdapter";

    private final ArrayList<Chat> chats;

    public ChatAdapter(ArrayList<Chat> chats) {
        this.chats = chats;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_chat, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        holder.chat_name.setText(chats.get(position).getChatName());
        Log.i(TAG, "onBindViewHolder: holder.chat_name set text to chats.get(position).getChatName()");

        String userId;
        if (!chats.get(position).getUserId1().equals(Objects.requireNonNull(FirebaseAuth.getInstance().getCurrentUser()).getUid())) {
            userId = chats.get(position).getUserId1();
        }
        else {
            userId = chats.get(position).getUserId2();
        }

        FirebaseDatabase.getInstance().getReference().child("Users").child(userId)
                .child("profile_image").get()
                .addOnCompleteListener(task -> {
                    try {
                        String profile_image = Objects.requireNonNull(task.getResult().getValue()).toString();
                        Log.i(TAG, "onBindViewHolder: get profile_image");
                        if (!profile_image.isEmpty()) {
                            Glide.with(holder.itemView.getContext()).load(profile_image).into(holder.chat_image);
                            Log.i(TAG, "onBindViewHolder: load profile_image to holder.chat_image");
                        } else {
                            holder.chat_image.setImageResource(R.drawable.anime_icon);
                            Log.i(TAG, "onBindViewHolder: holder.chat_image set anime_icon");
                        }
                    } catch (Exception exception) {
                        Toast.makeText(holder.itemView.getContext(), R.string.unknown_error, Toast.LENGTH_LONG).show();
                    }
                });

        holder.itemView.setOnClickListener(v -> {
            Intent intent = new Intent(holder.itemView.getContext(), ChatActivity.class);
            intent.putExtra("chatId", chats.get(position).getChatId());
            holder.itemView.getContext().startActivity(intent);
            Log.i(TAG, "onBindViewHolder: start ChatActivity");
        });
    }

    @Override
    public int getItemCount() {
        return chats.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        private final CircleImageView chat_image;
        private final TextView chat_name;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            chat_image = itemView.findViewById(R.id.user_image);
            Log.i(TAG, "ViewHolder: create user_image");
            chat_name = itemView.findViewById(R.id.username);
            Log.i(TAG, "ViewHolder: create username");
        }
    }
}