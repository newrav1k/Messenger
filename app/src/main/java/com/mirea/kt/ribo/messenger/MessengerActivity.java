package com.mirea.kt.ribo.messenger;

import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

import com.mirea.kt.ribo.messenger.bottom_navigation.messenger.MessengerFragment;
import com.mirea.kt.ribo.messenger.bottom_navigation.profile.ProfileFragment;
import com.mirea.kt.ribo.messenger.bottom_navigation.settings.SettingsFragment;
import com.mirea.kt.ribo.messenger.databinding.ActivityMessengerBinding;

import java.util.HashMap;
import java.util.Objects;

public class MessengerActivity extends AppCompatActivity {

    private ActivityMessengerBinding binding;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityMessengerBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        getSupportFragmentManager().beginTransaction().replace(binding.fragmentContainer.getId(), new MessengerFragment()).commit();
        binding.bottomNavigation.setSelectedItemId(R.id.action_mail_outline);

        HashMap<Integer, Fragment> fragments = new HashMap<Integer, Fragment>() {{
            put(R.id.action_person, new ProfileFragment());
            put(R.id.action_mail_outline, new MessengerFragment());
            put(R.id.action_settings, new SettingsFragment());
        }};

        binding.bottomNavigation.setOnItemSelectedListener(item -> {
            Fragment fragment = fragments.get(item.getItemId());
            getSupportFragmentManager().beginTransaction().replace(binding.fragmentContainer.getId(), Objects.requireNonNull(fragment)).commit();
            return true;
        });
    }
}