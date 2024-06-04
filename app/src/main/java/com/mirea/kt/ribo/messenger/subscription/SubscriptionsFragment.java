package com.mirea.kt.ribo.messenger.subscription;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.mirea.kt.ribo.messenger.ChatActivity;
import com.mirea.kt.ribo.messenger.R;
import com.mirea.kt.ribo.messenger.databinding.FragmentSubscriptionsBinding;
import com.mirea.kt.ribo.messenger.users.User;
import com.mirea.kt.ribo.messenger.utils.ChatUtil;

import java.util.ArrayList;
import java.util.Objects;

public class SubscriptionsFragment extends Fragment {
    private FragmentSubscriptionsBinding binding;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = FragmentSubscriptionsBinding.inflate(inflater, container, false);

        Toolbar toolbar = getActivity().findViewById(R.id.toolbar);
        toolbar.setTitle(R.string.friends);

        updateView();

        return binding.getRoot();
    }

    private void updateView() {
        ArrayList<User> friends = new ArrayList<>();
        String uid = Objects.requireNonNull(FirebaseAuth.getInstance().getCurrentUser()).getUid();
        FirebaseDatabase.getInstance().getReference()
                .addListenerForSingleValueEvent(new ValueEventListener() {
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

                        if (!friends.isEmpty()) {
                            binding.subscriptions.setLayoutManager(new LinearLayoutManager(getContext(), RecyclerView.VERTICAL, false));
                            SubscriptionsAdapter adapter = getSubscriptionsAdapter(friends);
                            binding.subscriptions.setAdapter(adapter);
                        } else {
                            binding.subscriptions.setLayoutManager(new LinearLayoutManager(getContext(), LinearLayoutManager.VERTICAL, false));
                        }
                        binding.subscriptions.addItemDecoration(new DividerItemDecoration(getActivity(), DividerItemDecoration.VERTICAL));
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });
    }

    @NonNull
    private SubscriptionsAdapter getSubscriptionsAdapter(ArrayList<User> friends) {
        return new SubscriptionsAdapter(friends, (user, position) -> {
            if (!ChatUtil.isExistingChat(user)) {
                ChatUtil.createChat(user);
            }
            Intent intent = new Intent(getContext(), ChatActivity.class);
            intent.putExtra("chatId", ChatUtil.getChatId(user));
            startActivity(new Intent(intent));
        }, (user, position) -> getFragmentManager().beginTransaction()
                .replace(R.id.fragment_container, new SubscriptionsFragment()).commit());
    }
}