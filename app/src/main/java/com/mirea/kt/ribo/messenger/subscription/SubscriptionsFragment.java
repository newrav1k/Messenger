package com.mirea.kt.ribo.messenger.subscription;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
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
    private final String TAG = "SubscriptionsFragment";
    private FragmentSubscriptionsBinding binding;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = FragmentSubscriptionsBinding.inflate(inflater, container, false);
        Log.i(TAG, "onCreateView: initialization binding");

        Toolbar toolbar = getActivity().findViewById(R.id.toolbar);
        toolbar.setTitle(R.string.subscriptions);
        Log.i(TAG, "onCreateView: toolbar set title subscriptions");

        updateView();
        Log.i(TAG, "onCreateView: call .updateView()");

        return binding.getRoot();
    }

    private void updateView() {
        ArrayList<User> subscriptions = new ArrayList<>();
        String uid = Objects.requireNonNull(FirebaseAuth.getInstance().getCurrentUser()).getUid();
        Log.i(TAG, "updateView: FirebaseAuth get user id");
        FirebaseDatabase.getInstance().getReference()
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        if (snapshot.child("Users").child(uid).child("subscriptions").getValue().toString().isEmpty()) {
                            return;
                        }
                        String friendsStr = Objects.requireNonNull(snapshot.child("Users").child(uid).child("subscriptions").getValue()).toString();
                        String[] friendsIds = friendsStr.split(",");

                        for (String friendsId : friendsIds) {
                            DataSnapshot usersSnapshot = snapshot.child("Users").child(friendsId);

                            String userId = usersSnapshot.getKey();
                            Log.i(TAG, "onDataChange: get userId");
                            String username = Objects.requireNonNull(usersSnapshot.child("username").getValue()).toString();
                            Log.i(TAG, "onDataChange: get username");
                            String profile_image = Objects.requireNonNull(usersSnapshot.child("profile_image").getValue()).toString();
                            Log.i(TAG, "onDataChange: get profile_image");

                            subscriptions.add(new User(userId, username, profile_image));
                            Log.i(TAG, "onDataChange: subscriptions add new user");
                        }

                        if (!subscriptions.isEmpty()) {
                            binding.subscriptions.setLayoutManager(new LinearLayoutManager(getContext(), RecyclerView.VERTICAL, false));
                            Log.i(TAG, "onDataChange: binding.subscriptions set layout manager");
                            SubscriptionsAdapter adapter = getSubscriptionsAdapter(subscriptions);
                            binding.subscriptions.setAdapter(adapter);
                            Log.i(TAG, "onDataChange: binding.subscriptions set adapter");
                        } else {
                            binding.subscriptions.setLayoutManager(new LinearLayoutManager(getContext(), LinearLayoutManager.VERTICAL, false));
                            Log.i(TAG, "onDataChange: binding.subscriptions set layout manager");
                        }
                        binding.subscriptions.addItemDecoration(new DividerItemDecoration(getActivity(), DividerItemDecoration.VERTICAL));
                        Log.i(TAG, "onDataChange: binding.subscriptions add item decoration");
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
                Log.i(TAG, "getSubscriptionsAdapter: ChatUtil call .createChat(user)");
            }
            Intent intent = new Intent(getContext(), ChatActivity.class);
            intent.putExtra("chatId", ChatUtil.getChatId(user));
            startActivity(new Intent(intent));
            Log.i(TAG, "getSubscriptionsAdapter: start ChatActivity");
        }, (user, position) -> {
            getFragmentManager().beginTransaction()
                    .replace(R.id.fragment_container, new SubscriptionsFragment()).commit();
            Log.i(TAG, "getSubscriptionsAdapter: fragment_container replace to SubscriptionsFragment()");
        });
    }
}