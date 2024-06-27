package com.mirea.kt.ribo.messenger;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.dynamiclinks.FirebaseDynamicLinks;
import com.google.firebase.dynamiclinks.PendingDynamicLinkData;
import com.mirea.kt.ribo.messenger.databinding.ActivityLoginBinding;

import java.util.Objects;

public class LoginActivity extends AppCompatActivity {
    private final String TAG = "LoginActivity";
    private ActivityLoginBinding binding;
    private Uri deepLink;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityLoginBinding.inflate(getLayoutInflater());
        Log.i(TAG, "onCreateView: initialization binding");
        setContentView(binding.getRoot());
        Log.i(TAG, "onCreate: .setContentView(binding.getRoot())");

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        ActionBar actionBar = getSupportActionBar();

        if (actionBar != null) {
            actionBar.setTitle("");
            Log.i(TAG, "onCreate: toolbar set title");
            actionBar.setHomeButtonEnabled(true);
            Log.i(TAG, "onCreate: toolbar setHomeButtonEnabled true");
            actionBar.setDisplayHomeAsUpEnabled(true);
            Log.i(TAG, "onCreate: toolbar setDisplayHomeAsUpEnabled true");
        }

        binding.loginButton.setOnClickListener(v -> {
            String email = Objects.requireNonNull(binding.emailEdittext.getText()).toString();
            Log.i(TAG, "onCreate: get email");
            String password = Objects.requireNonNull(binding.passwordEdittext.getText()).toString();
            Log.i(TAG, "onCreate: get password");
            if (!email.isEmpty() && !password.isEmpty()) {
                FirebaseAuth.getInstance().signInWithEmailAndPassword(email, password)
                        .addOnCompleteListener(task -> {
                            if (task.isSuccessful()) {
                                if (Objects.requireNonNull(FirebaseAuth.getInstance().getCurrentUser()).isEmailVerified()) {
                                    Toast.makeText(getApplicationContext(), R.string.successful_authorization, Toast.LENGTH_LONG).show();
                                    startActivity(new Intent(getApplicationContext(), MessengerActivity.class));
                                    Log.i(TAG, "onCreate: start MessengerActivity");
                                    finish();
                                    Log.i(TAG, "onCreate: call .finish()");
                                } else {
                                    Toast.makeText(getApplicationContext(), "Вы не перешли по ссылке подтверждения", Toast.LENGTH_LONG).show();
                                }
                            } else {
                                Toast.makeText(getApplicationContext(), R.string.incorrect_email_or_password, Toast.LENGTH_LONG).show();
                            }
                        });
            } else {
                Toast.makeText(getApplicationContext(), R.string.empty_fields, Toast.LENGTH_LONG).show();
            }
        });
        binding.registerButton.setOnClickListener(v -> {
            startActivity(new Intent(getApplicationContext(), RegisterActivity.class));
            Log.i(TAG, "onCreate: start RegisterActivity");
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.simple_menu, menu);
        Log.i(TAG, "onCreateOptionsMenu: inflate R.menu.simple_menu");
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            finish();
            Log.i(TAG, "onOptionsItemSelected: call .finish()");
            return true;
        } else {
            return super.onOptionsItemSelected(item);
        }
    }
}