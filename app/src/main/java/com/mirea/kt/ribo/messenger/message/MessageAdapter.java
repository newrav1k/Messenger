package com.mirea.kt.ribo.messenger.message;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.google.firebase.auth.FirebaseAuth;
import com.mirea.kt.ribo.messenger.R;

import java.util.ArrayList;
import java.util.Objects;

public class MessageAdapter extends RecyclerView.Adapter<MessageAdapter.MessageViewHolder> {
    private static final String TAG = "MessageAdapter";

    private final ArrayList<Message> messages;

    public MessageAdapter(ArrayList<Message> messages) {
        this.messages = messages;
    }

    @NonNull
    @Override
    public MessageViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(viewType, parent, false);
        return new MessageViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MessageViewHolder holder, int position) {
        Message message = messages.get(position);
        Log.i(TAG, "onBindViewHolder: get message");
        if (!message.getPhoto().isEmpty()) {
            Glide.with(holder.itemView).load(message.getPhoto()).into(holder.photo);
            Log.i(TAG, "onBindViewHolder: load holder.photo to message.getPhoto()");
        } else {
            holder.message.setText(message.getText());
            Log.i(TAG, "onBindViewHolder: holder.message set text message.getText()");
        }
        holder.date.setText(message.getDate());
        Log.i(TAG, "onBindViewHolder: holder.date set text message.getDate()");
    }

    @Override
    public int getItemCount() {
        return messages.size();
    }

    @Override
    public int getItemViewType(int position) {
        Message message = messages.get(position);
        if (message.getPhoto().isEmpty()) {
            if (message.getOwnerId().equals(Objects.requireNonNull(Objects.requireNonNull(FirebaseAuth.getInstance().getCurrentUser()).getUid()))) {
                return R.layout.item_own_message;
            } else {
                return R.layout.item_other_message;
            }
        } else {
            if (message.getOwnerId().equals(Objects.requireNonNull(Objects.requireNonNull(FirebaseAuth.getInstance().getCurrentUser()).getUid()))) {
                return R.layout.item_own_photo;
            } else {
                return R.layout.item_other_photo;
            }
        }
    }

    public static class MessageViewHolder extends RecyclerView.ViewHolder {
        private final TextView message;
        private final TextView date;
        private final ImageView photo;

        public MessageViewHolder(@NonNull View itemView) {
            super(itemView);
            message = itemView.findViewById(R.id.message);
            Log.i(TAG, "ViewHolder: create message");
            date = itemView.findViewById(R.id.message_date);
            Log.i(TAG, "ViewHolder: create message_date");
            photo = itemView.findViewById(R.id.photo);
            Log.i(TAG, "ViewHolder: create photo");
        }
    }
}