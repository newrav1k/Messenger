package com.mirea.kt.ribo.messenger.bottom_navigation.messenger;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;

import com.mirea.kt.ribo.messenger.R;
import com.mirea.kt.ribo.messenger.chats.ChatsFragment;
import com.mirea.kt.ribo.messenger.databinding.FragmentMessengerBinding;
import com.mirea.kt.ribo.messenger.users.UsersFragment;

import java.util.HashMap;
import java.util.Objects;

public class MessengerFragment extends Fragment {
    private final String TAG = "MessengerFragment";

    private FragmentMessengerBinding binding;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = FragmentMessengerBinding.inflate(inflater, container, false);
        Log.i(TAG, "onCreateView: initialization binding");

        updateView();
        Log.i(TAG, "onCreateView: call updateView()");

        HashMap<Integer, Fragment> fragments = new HashMap<Integer, Fragment>() {{
            put(R.id.users, new UsersFragment());
            put(R.id.chats, new ChatsFragment());
        }};

        binding.topNavigationView.setOnItemSelectedListener(item -> {
            Fragment fragment = fragments.get(item.getItemId());
            getActivity().getSupportFragmentManager().beginTransaction().replace(binding.fragmentMessenger.getId(), Objects.requireNonNull(fragment)).commit();
            Log.i(TAG, "onCreateView: fragmentMessenger replace to fragment");
            return true;
        });

        return binding.getRoot();
    }

    private void updateView() {
        Toolbar toolbar = getActivity().findViewById(R.id.toolbar);
        toolbar.setTitle(R.string.app_name);
        Log.i(TAG, "updateView: toolbar change title app_name");

        getActivity().getSupportFragmentManager().beginTransaction().replace(binding.fragmentMessenger.getId(), new UsersFragment()).commit();
        binding.topNavigationView.setSelectedItemId(R.id.users);
        Log.i(TAG, "updateView: binding.topNavigationView select item");
    }
}