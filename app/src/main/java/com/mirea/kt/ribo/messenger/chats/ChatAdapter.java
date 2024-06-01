package com.mirea.kt.ribo.messenger.chats;

import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.FirebaseDatabase;
import com.mirea.kt.ribo.messenger.ChatActivity;
import com.mirea.kt.ribo.messenger.R;

import java.util.ArrayList;

import de.hdodenhof.circleimageview.CircleImageView;

public class ChatAdapter extends RecyclerView.Adapter<ChatAdapter.ViewHolder> {

    private final ArrayList<Chat> chats;

    public ChatAdapter(ArrayList<Chat> chats) {
        this.chats = chats;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_person, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        holder.chat_name.setText(chats.get(position).getChatName());

        String userId;
        if (!chats.get(position).getUserId1().equals(FirebaseAuth.getInstance().getCurrentUser().getUid())) {
            userId = chats.get(position).getUserId1();
        }
        else {
            userId = chats.get(position).getUserId2();
        }

        FirebaseDatabase.getInstance().getReference().child("Users").child(userId)
                .child("profile_image").get()
                .addOnCompleteListener(new OnCompleteListener<DataSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<DataSnapshot> task) {
                        try {
                            String profile_image = task.getResult().getValue().toString();
                            if (!profile_image.isEmpty()) {
                                Glide.with(holder.itemView.getContext()).load(profile_image).into(holder.chat_image);
                            } else {
                                holder.chat_image.setImageResource(R.drawable.anime_icon);
                            }
                        } catch (Exception exception) {
                            Toast.makeText(holder.itemView.getContext(), R.string.unknown_error, Toast.LENGTH_LONG).show();
                        }
                    }
                });

        holder.itemView.setOnClickListener(v -> {
            Intent intent = new Intent(holder.itemView.getContext(), ChatActivity.class);
            intent.putExtra("chatId", chats.get(position).getChatId());
            holder.itemView.getContext().startActivity(intent);
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
            chat_name = itemView.findViewById(R.id.username);
        }
    }
}