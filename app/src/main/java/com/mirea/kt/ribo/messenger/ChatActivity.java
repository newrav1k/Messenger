package com.mirea.kt.ribo.messenger;

import static java.security.AccessController.getContext;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.bumptech.glide.Glide;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.mirea.kt.ribo.messenger.databinding.ActivityChatBinding;
import com.mirea.kt.ribo.messenger.message.Message;
import com.mirea.kt.ribo.messenger.message.MessageAdapter;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Objects;

public class ChatActivity extends AppCompatActivity {
    private final String TAG = "ChatActivity";
    private ActivityChatBinding binding;
    private Uri photoPath;
    private String chatId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityChatBinding.inflate(getLayoutInflater());
        Log.i(TAG, "onCreateView: initialization binding");
        setContentView(binding.getRoot());
        Log.i(TAG, "onCreate: .setContentView(binding.getRoot())");

        Toolbar toolbar = binding.toolbar;
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

        String chatId = getIntent().getStringExtra("chatId");
        Log.i(TAG, "onCreate: get chatId");
        this.chatId = chatId;

        updateView(chatId);
        Log.i(TAG, "onCreate: call .update(chatId)");

        binding.sendMessage.setOnClickListener(v -> {
            Log.i(TAG, "onCreate: pressing binding.sendMessage");
            String message = binding.enterMessage.getText().toString().trim();
            Log.i(TAG, "onCreate: get message");
            if (message.isEmpty()) {
                Toast.makeText(getApplicationContext(), R.string.message_input_field_cannot_be_empty, Toast.LENGTH_LONG).show();
                return;
            }

            @SuppressLint("SimpleDateFormat")
            SimpleDateFormat simpleDateFormat = new SimpleDateFormat("dd.MM.yyyy hh:mm");
            String date = simpleDateFormat.format(new Date());

            binding.enterMessage.setText("");
            Log.i(TAG, "onCreate: binding.enterMessage set text");
            sendMessage(chatId, message, date, null);
            Log.i(TAG, "onCreate: call .sendMessage(chatId, message, date, null)");
        });
        binding.sendPhoto.setOnClickListener(v -> {
            Log.i(TAG, "onCreate: pressing binding.sendPhoto");
            getImage();
        });
    }

    public boolean getImage() {
        Intent intentChooser = new Intent();
        intentChooser.setType("image/*");
        intentChooser.setAction(Intent.ACTION_GET_CONTENT);
        activityResultLauncher.launch(intentChooser);
        Log.i(TAG, "selectImage: start choose image intent");
        return true;
    }

    ActivityResultLauncher<Intent> activityResultLauncher = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            result -> {
                if (result.getResultCode() == Activity.RESULT_OK && result.getData() != null && result.getData().getData() != null) {
                    photoPath = result.getData().getData();
                    Log.i(TAG, "ActivityResultLauncher: get result");

                    if (photoPath == null) {
                        Toast.makeText(getApplicationContext(), R.string.message_input_field_cannot_be_empty, Toast.LENGTH_LONG).show();
                        return;
                    }

//                    @SuppressLint("SimpleDateFormat")
//                    SimpleDateFormat simpleDateFormat = new SimpleDateFormat("dd.MM.yyyy hh:mm");
//                    String date = simpleDateFormat.format(new Date());

                    binding.enterMessage.setText("");
                    Log.i(TAG, "ActivityResultLauncher: binding.enterMessage set text");
//                    sendMessage(chatId, "", date, photoPath);
                    uploadImage();
//                    photoPath = null;
                    Log.i(TAG, "ActivityResultLauncher: call .sendMessage(chatId, \"\", date, photoPath)");
                }
            });

    private void updateView(String chatId) {
        uploadMessages(chatId);
        Log.i(TAG, "updateView: call .uploadMessages(chatId)");
        uploadPartnerInfo();
        Log.i(TAG, "updateView: call .uploadPartnerInfo()");
    }

    private void uploadImage() {
        if (photoPath != null) {
            FirebaseStorage.getInstance().getReference()
                    .child("images/")
                    .child("Chats")
                    .child(chatId)
                    .child(photoPath.toString().replace("/", ""))
                    .putFile(photoPath)
                    .addOnSuccessListener(taskSnapshot -> {
                        Toast.makeText(getApplicationContext(), R.string.photo_uploaded_successfully, Toast.LENGTH_LONG).show();

                        FirebaseStorage.getInstance().getReference()
                                .child("images/")
                                .child("Chats")
                                .child(chatId)
                                .child(photoPath.toString().replace("/", ""))
                                .getDownloadUrl()
                                .addOnSuccessListener(uri -> {
                                    @SuppressLint("SimpleDateFormat")
                                    SimpleDateFormat simpleDateFormat = new SimpleDateFormat("dd.MM.yyyy hh:mm");
                                    String date = simpleDateFormat.format(new Date());
                                    HashMap<String, String> messageInfo = new HashMap<>();
                                    messageInfo.put("text", "");
                                    messageInfo.put("ownerId", Objects.requireNonNull(FirebaseAuth.getInstance().getCurrentUser()).getUid());
                                    messageInfo.put("date", date);
                                    messageInfo.put("photo", uri.toString());

                                    FirebaseDatabase.getInstance().getReference().child("Chats")
                                            .child(chatId)
                                            .child("messages").push().setValue(messageInfo);
                                    photoPath = null;
                                });
                    });
        }
    }

    public void sendMessage(String chatId, String message, String date, Uri photo) {
        if (chatId == null) {
            return;
        }

        HashMap<String, String> messageInfo = new HashMap<>();
        if (photo == null) {
            messageInfo.put("text", message);
            messageInfo.put("ownerId", Objects.requireNonNull(FirebaseAuth.getInstance().getCurrentUser()).getUid());
            messageInfo.put("date", date);
            messageInfo.put("photo", "");
        }

        FirebaseDatabase.getInstance().getReference().child("Chats")
                .child(chatId)
                .child("messages").push().setValue(messageInfo);
        Log.i(TAG, "sendMessage: FirebaseDatabase set value messageInfo");
    }

    private void uploadMessages(String chatId) {
        if (chatId == null) {
            return;
        }

        FirebaseDatabase.getInstance().getReference().child("Chats")
                .child(chatId).child("messages").addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        if (!snapshot.exists()) {
                            return;
                        }

                        ArrayList<Message> messages = new ArrayList<>();
                        for (DataSnapshot messageSnapshot : snapshot.getChildren()) {
                            String messageId = messageSnapshot.getKey();
                            Log.i(TAG, "onDataChange: get messageId");
                            String ownerId = Objects.requireNonNull(messageSnapshot.child("ownerId").getValue()).toString();
                            Log.i(TAG, "onDataChange: get ownerId");
                            String text = Objects.requireNonNull(messageSnapshot.child("text").getValue()).toString();
                            Log.i(TAG, "onDataChange: get text");
                            String date = Objects.requireNonNull(messageSnapshot.child("date").getValue()).toString();
                            Log.i(TAG, "onDataChange: get date");
                            String photo = Objects.requireNonNull(messageSnapshot.child("photo").getValue()).toString();
                            Log.i(TAG, "onDataChange: get photo");

                            messages.add(new Message(messageId, ownerId, text, date, photo));
                            Log.i(TAG, "onDataChange: messages add new message");
                        }
                        MessageAdapter adapter = new MessageAdapter(messages);
                        binding.messages.setLayoutManager(new LinearLayoutManager(getApplicationContext(),
                                LinearLayoutManager.VERTICAL, false));
                        Log.i(TAG, "onDataChange: binding.messages set layout manager");
                        binding.messages.setAdapter(adapter);
                        Log.i(TAG, "onDataChange: binding.messages set adapter");
                        binding.messages.scrollToPosition(adapter.getItemCount() - 1);
                        Log.i(TAG, "onDataChange: binding.messages scrollToPosition");
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });
    }

    private void uploadPartnerInfo() {
        String chatId = getIntent().getStringExtra("chatId");
        if (chatId == null) {
            return;
        }

        FirebaseDatabase.getInstance().getReference().child("Chats").child(chatId).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                String userId1 = Objects.requireNonNull(snapshot.child("user1").getValue()).toString();
                Log.i(TAG, "onDataChange: get userId1");
                String userId2 = Objects.requireNonNull(snapshot.child("user2").getValue()).toString();
                Log.i(TAG, "onDataChange: get userId2");

                DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference().child("Users");
                Log.i(TAG, "onDataChange: get databaseReference");

                if (Objects.requireNonNull(FirebaseAuth.getInstance().getCurrentUser()).getUid().equals(userId1)) {
                    databaseReference.child(userId2).addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                            binding.username.setText(Objects.requireNonNull(snapshot.child("username").getValue()).toString());
                            Log.i(TAG, "onDataChange: binding.username set text username");
                            String profile_image = Objects.requireNonNull(snapshot.child("profile_image").getValue()).toString();
                            Log.i(TAG, "onDataChange: get profile_image");
                            if (!profile_image.isEmpty()) {
                                Glide.with(getApplicationContext()).load(profile_image).into(binding.profileImage);
                                Log.i(TAG, "onDataChange: load profile_image to binding.profileImage");
                            } else {
                                binding.profileImage.setImageResource(R.drawable.anime_icon);
                                Log.i(TAG, "onDataChange: binding.profileImage set image anime_icon");
                            }
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError error) {

                        }
                    });
                } else {
                    databaseReference.child(userId1).addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                            binding.username.setText(Objects.requireNonNull(snapshot.child("username").getValue()).toString());
                            Log.i(TAG, "onDataChange: binding.username set text username");
                            String profile_image = Objects.requireNonNull(snapshot.child("profile_image").getValue()).toString();
                            Log.i(TAG, "onDataChange: get profile_image");
                            if (!profile_image.isEmpty()) {
                                Glide.with(getApplicationContext()).load(profile_image).into(binding.profileImage);
                                Log.i(TAG, "onDataChange: load profile_image to binding.profileImage");
                            } else {
                                binding.profileImage.setImageResource(R.drawable.anime_icon);
                                Log.i(TAG, "onDataChange: binding.profileImage set image anime_icon");
                            }
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError error) {

                        }
                    });
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
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