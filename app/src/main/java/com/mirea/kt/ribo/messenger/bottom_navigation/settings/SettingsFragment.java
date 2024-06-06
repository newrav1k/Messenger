package com.mirea.kt.ribo.messenger.bottom_navigation.settings;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.FirebaseDatabase;
import com.mirea.kt.ribo.messenger.R;
import com.mirea.kt.ribo.messenger.databinding.FragmentSettingsBinding;

import java.util.Objects;

public class SettingsFragment extends Fragment {
    private final String TAG = "SettingsFragment";
    private FragmentSettingsBinding binding;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = FragmentSettingsBinding.inflate(inflater, container, false);
        Log.i(TAG, "onCreateView: initialization binding");

        updateView();
        Log.i(TAG, "onCreateView: call .updateView()");

        binding.confirmButton.setOnClickListener(v -> {
            Log.i(TAG, "onCreateView: ");
            String username = binding.username.getText().toString().trim();
            Log.i(TAG, "onCreateView: get username");
            String status = binding.status.getText().toString().trim();
            Log.i(TAG, "onCreateView: get status");

            String uid = Objects.requireNonNull(FirebaseAuth.getInstance().getCurrentUser()).getUid();
            Log.i(TAG, "onCreateView: FirebaseAuth get user id");

            if (!username.isEmpty()) {
                FirebaseDatabase.getInstance().getReference().child("Users").child(uid)
                        .child("username").setValue(username);
                Log.i(TAG, "onCreateView: FirebaseDatabase set username");
                binding.username.setText("");
                Log.i(TAG, "onCreateView: binding.username set empty text");
            }
            if (!status.isEmpty()) {
                FirebaseDatabase.getInstance().getReference().child("Users").child(uid)
                        .child("status").setValue(status);
                Log.i(TAG, "onCreateView: FirebaseDatabase set status");
                binding.status.setText("");
                Log.i(TAG, "onCreateView: binding.status set empty text");
            }
        });

        return binding.getRoot();
    }

    private void updateView() {
        assert getActivity() != null;
        Toolbar toolbar = getActivity().findViewById(R.id.toolbar);
        toolbar.setTitle(R.string.settings_title);
        Log.i(TAG, "updateView: toolbar change title");
    }
}