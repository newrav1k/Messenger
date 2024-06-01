package com.mirea.kt.ribo.messenger.bottom_navigation.settings;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.FirebaseDatabase;
import com.mirea.kt.ribo.messenger.R;
import com.mirea.kt.ribo.messenger.databinding.FragmentSettingsBinding;

public class SettingsFragment extends Fragment {

    private FragmentSettingsBinding binding;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = FragmentSettingsBinding.inflate(inflater, container, false);

        updateView();

        binding.confirmButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String username = binding.username.getText().toString().trim();
                String status = binding.status.getText().toString().trim();

                String uid = FirebaseAuth.getInstance().getCurrentUser().getUid();

                if (!username.isEmpty()) {
                    FirebaseDatabase.getInstance().getReference().child("Users").child(uid)
                            .child("username").setValue(username);
                    binding.username.setText("");
                }
                if (!status.isEmpty()) {
                    FirebaseDatabase.getInstance().getReference().child("Users").child(uid)
                            .child("status").setValue(status);
                    binding.status.setText("");
                }
            }
        });

        return binding.getRoot();
    }

    private void updateView() {
        Toolbar toolbar = getActivity().findViewById(R.id.toolbar);
        toolbar.setTitle(R.string.settings_title);
    }
}