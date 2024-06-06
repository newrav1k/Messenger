package com.mirea.kt.ribo.messenger.chats;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.mirea.kt.ribo.messenger.R;
import com.mirea.kt.ribo.messenger.databinding.FragmentChatsBinding;

import java.util.ArrayList;
import java.util.Objects;

public class ChatsFragment extends Fragment {
    private FragmentChatsBinding binding;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = FragmentChatsBinding.inflate(inflater, container, false);

        loadChats();

        return binding.getRoot();
    }

    private void loadChats() {
        ArrayList<Chat> chats = new ArrayList<>();

        String uid = Objects.requireNonNull(FirebaseAuth.getInstance().getCurrentUser()).getUid();
        FirebaseDatabase.getInstance().getReference().addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.child("Users").child(uid).child("chats").getValue().toString().isEmpty()) {
                    return;
                }
                String chatsStr = Objects.requireNonNull(snapshot.child("Users").child(uid).child("chats").getValue()).toString();
                String[] chatsIds = chatsStr.split(",");

                for (String chatId : chatsIds) {
                    DataSnapshot chatSnapshot = snapshot.child("Chats").child(chatId);
                    String userId1 = Objects.requireNonNull(chatSnapshot.child("user1").getValue()).toString();
                    String userId2 = Objects.requireNonNull(chatSnapshot.child("user2").getValue()).toString();

                    String chatUserId = uid.equals(userId1) ? userId2 : userId1;

                    String chat_name = Objects.requireNonNull(snapshot.child("Users").child(chatUserId).child("username").getValue()).toString();

                    chats.add(new Chat(chatId, chat_name, userId1, userId2));
                }

                if (!chats.isEmpty()) {
                    binding.chats.setLayoutManager(new LinearLayoutManager(getContext(), RecyclerView.VERTICAL, false));
                    binding.chats.setAdapter(new ChatAdapter(chats));
                } else {
                    binding.chats.setLayoutManager(new LinearLayoutManager(getContext(), LinearLayoutManager.VERTICAL, false));
                }
                assert getActivity() != null;
                binding.chats.addItemDecoration(new DividerItemDecoration(getActivity(), DividerItemDecoration.VERTICAL));
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(getContext(), R.string.error_receiving_chats, Toast.LENGTH_LONG).show();
            }
        });
    }
}