package com.mirea.kt.ribo.messenger;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.mirea.kt.ribo.messenger.databinding.ActivityMessengerBinding;
import com.mirea.kt.ribo.messenger.databinding.ActivityRegisterBinding;

import java.io.IOException;
import java.util.HashMap;
import java.util.Objects;

public class RegisterActivity extends AppCompatActivity {
    private final String TAG = "RegisterActivity";
    private ActivityRegisterBinding binding;
    private Uri filePath;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityRegisterBinding.inflate(getLayoutInflater());
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

        binding.registerButton.setOnClickListener(v -> {
            String username = Objects.requireNonNull(binding.usernameEdittext.getText()).toString();
            Log.i(TAG, "onCreate: get username");
            String email = Objects.requireNonNull(binding.emailEdittext.getText()).toString();
            Log.i(TAG, "onCreate: get email");
            String password = Objects.requireNonNull(binding.passwordEdittext.getText()).toString();
            Log.i(TAG, "onCreate: get password");
            String confirm_password = Objects.requireNonNull(binding.confirmPasswordEdittext.getText()).toString();
            Log.i(TAG, "onCreate: get confirm_password");
            if (!username.isEmpty() && !email.isEmpty() && !password.isEmpty() && !confirm_password.isEmpty()) {
                if (password.length() >= 6) {
                    if (password.equals(confirm_password)) {
                        FirebaseAuth.getInstance().createUserWithEmailAndPassword(email, password)
                                .addOnCompleteListener(task -> {
                                    if (task.isSuccessful()) {
                                        HashMap<String, String> user_info = new HashMap<String, String>() {{
                                            put("chats", "");
                                            put("subscriptions", "");
                                            put("email", email);
                                            put("username", username);
                                            put("profile_image", "");
                                            put("status", "Привет! Я есть Грут!");
                                        }};
                                        Log.i(TAG, "onCreate: create user_info");
                                        FirebaseDatabase.getInstance().getReference()
                                                .child("Users")
                                                .child(Objects.requireNonNull(FirebaseAuth.getInstance().getCurrentUser()).getUid())
                                                .setValue(user_info);
                                        Log.i(TAG, "onCreate: FirebaseDatabase set value user_info");
                                        uploadImage();
                                        Log.i(TAG, "onCreate: call .uploadImage()");
                                        Toast.makeText(getApplicationContext(), R.string.successful_registration, Toast.LENGTH_LONG).show();
                                        startActivity(new Intent(getApplicationContext(), MessengerActivity.class));
                                        finish();
                                    } else {
                                        Toast.makeText(getApplicationContext(), R.string.account_already_exists, Toast.LENGTH_LONG).show();
                                    }
                                });
                    } else {
                        Toast.makeText(getApplicationContext(), R.string.mismatch_passwords, Toast.LENGTH_LONG).show();
                    }
                } else {
                    Toast.makeText(getApplicationContext(), R.string.length_passwords, Toast.LENGTH_LONG).show();
                }
            } else {
                Toast.makeText(getApplicationContext(), R.string.empty_fields, Toast.LENGTH_LONG).show();
            }
        });

        binding.profileImage.setOnClickListener(v -> selectImage());
    }

    ActivityResultLauncher<Intent> activityResultLaunch = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            result -> {
                if (result.getResultCode() == Activity.RESULT_OK && result.getData() != null && result.getData().getData() != null) {
                    filePath = result.getData().getData();
                    try {
                        Bitmap bitmap = MediaStore.Images.Media
                                .getBitmap(
                                        getContentResolver(),
                                        filePath
                                );
                        binding.profileImage.setImageBitmap(bitmap);
                    } catch (IOException exception) {
                        Toast.makeText(getApplicationContext(), exception.getMessage(), Toast.LENGTH_LONG).show();
                    }
                }
            });

    private void selectImage() {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        activityResultLaunch.launch(intent);
    }

    private void uploadImage() {
        if (filePath != null) {
            String uid = Objects.requireNonNull(FirebaseAuth.getInstance().getCurrentUser()).getUid();
            FirebaseStorage.getInstance().getReference().child("images/").child("profile_images").child(uid)
                    .putFile(filePath).addOnSuccessListener(taskSnapshot -> {
                        Toast.makeText(getApplicationContext(), R.string.photo_uploaded_successfully, Toast.LENGTH_LONG).show();

                        FirebaseStorage.getInstance().getReference().child("images/").child("profile_images").child(uid).getDownloadUrl()
                                .addOnSuccessListener(uri -> FirebaseDatabase.getInstance().getReference()
                                        .child("Users")
                                        .child(FirebaseAuth.getInstance().getCurrentUser().getUid())
                                        .child("profile_image").setValue(uri.toString()));
                    });
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.simple_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            finish();
            return true;
        } else {
            return super.onOptionsItemSelected(item);
        }
    }
}