package com.mirea.kt.ribo.messenger.friends;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

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
import com.mirea.kt.ribo.messenger.R;
import com.mirea.kt.ribo.messenger.chats.Chat;
import com.mirea.kt.ribo.messenger.chats.ChatAdapter;
import com.mirea.kt.ribo.messenger.databinding.FragmentFriendsBinding;
import com.mirea.kt.ribo.messenger.users.User;
import com.mirea.kt.ribo.messenger.users.UserAdapter;

import java.util.ArrayList;
import java.util.Objects;

public class FriendsFragment extends Fragment {

    private FragmentFriendsBinding binding;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = FragmentFriendsBinding.inflate(inflater, container, false);

        Toolbar toolbar = getActivity().findViewById(R.id.toolbar);
        toolbar.setTitle(R.string.friends);

        updateView();

        return binding.getRoot();
    }

    private void updateView() {
        ArrayList<User> friends = new ArrayList<>();

        String uid = Objects.requireNonNull(FirebaseAuth.getInstance().getCurrentUser()).getUid();
        FirebaseDatabase.getInstance().getReference().addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                String friendsStr = Objects.requireNonNull(snapshot.child("Users").child(uid).child("friends").getValue()).toString();
                String[] friendsIds = friendsStr.split(",");

                for (String friendsId : friendsIds) {
                    DataSnapshot usersSnapshot = snapshot.child("Users").child(friendsId);

                    String userId = usersSnapshot.getValue().toString();
                    String username = usersSnapshot.child("username").getValue().toString();
                    String profile_image = usersSnapshot.child("profile_image").getValue().toString();

                    friends.add(new User(userId, username, profile_image));
                }

                if (!friends.isEmpty()) {
                    binding.friends.setLayoutManager(new LinearLayoutManager(getContext(), RecyclerView.VERTICAL, false));
                    binding.friends.setAdapter(new FriendsAdapter(friends));
                } else {
                    binding.friends.setLayoutManager(new LinearLayoutManager(getContext(), LinearLayoutManager.VERTICAL, false));
                }
                binding.friends.addItemDecoration(new DividerItemDecoration(getActivity(), DividerItemDecoration.VERTICAL));
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(getContext(), R.string.error_receiving_chats, Toast.LENGTH_LONG).show();
            }
        });
    }
}