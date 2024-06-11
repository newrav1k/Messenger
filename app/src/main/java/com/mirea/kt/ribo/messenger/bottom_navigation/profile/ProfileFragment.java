package com.mirea.kt.ribo.messenger.bottom_navigation.profile;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;

import com.bumptech.glide.Glide;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.mirea.kt.ribo.messenger.LoginActivity;
import com.mirea.kt.ribo.messenger.R;
import com.mirea.kt.ribo.messenger.databinding.FragmentProfileBinding;
import com.mirea.kt.ribo.messenger.subscription.SubscriptionsFragment;

import java.io.IOException;
import java.util.Objects;

public class ProfileFragment extends Fragment {
    private final String TAG = "ProfileFragment";
    private FragmentProfileBinding binding;
    private Uri filePath;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = FragmentProfileBinding.inflate(inflater, container, false);
        Log.i(TAG, "onCreateView: initialization binding");

        updateView();
        Log.i(TAG, "onCreateView: call .updateView()");

        binding.profileImage.setOnClickListener(v -> {
            Log.i(TAG, "onCreateView: pressing binding.profileImage");
            selectImage();
            Log.i(TAG, "onCreateView: call .selectImage()");
        });
        binding.logoutButton.setOnClickListener(v -> {
            Log.i(TAG, "onCreateView: pressing binding.logoutButton");
            FirebaseAuth.getInstance().signOut();
            Log.i(TAG, "onCreateView: firebaseAuth call .signOut()");
            startActivity(new Intent(getContext(), LoginActivity.class));
            Log.i(TAG, "onCreateView: start LoginActivity");
            getActivity().finish();
            Log.i(TAG, "onCreateView: getActivity() call .finish()");
        });

        binding.friendsButton.setOnClickListener(v -> {
            Log.i(TAG, "onCreateView: pressing binding.friendsButton");
            getActivity().getSupportFragmentManager().beginTransaction()
                    .replace(R.id.fragment_container, new SubscriptionsFragment()).commit();
            Log.i(TAG, "onCreateView: fragment_container replace to SubscriptionsFragment()");
        });

        return binding.getRoot();
    }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        updateView();
        Log.i(TAG, "onCreateView: call .updateView()");
    }

    private void updateView() {
        loadUserInfo();
        Log.i(TAG, "onCreateView: call .loadUserInfo()");
        updateToolbar();
        Log.i(TAG, "onCreateView: call .updateToolbar()");
    }

    private void loadUserInfo() {
        FirebaseDatabase.getInstance().getReference().child("Users")
                .child(FirebaseAuth.getInstance().getCurrentUser().getUid())
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        String username = Objects.requireNonNull(snapshot.child("username").getValue()).toString();
                        Log.i(TAG, "onDataChange: get username");
                        String status = Objects.requireNonNull(snapshot.child("status").getValue()).toString();
                        Log.i(TAG, "onDataChange: get status");
                        String profile_image = Objects.requireNonNull(snapshot.child("profile_image").getValue()).toString();
                        Log.i(TAG, "onDataChange: get profile_image");

                        binding.username.setText(username);
                        Log.i(TAG, "onDataChange: binding.username set username");
                        binding.status.setText(status);
                        Log.i(TAG, "onDataChange: binding.status set status");

                        if (!profile_image.isEmpty()) {
                            Glide.with(getContext()).load(profile_image).into(binding.profileImage);
                            Log.i(TAG, "onDataChange: load profile_image to binding.profileImage");
                        } else {
                            binding.profileImage.setImageResource(R.drawable.anime_icon);
                            Log.i(TAG, "onDataChange: binding.profileImage set anime_icon");
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });
    }

    ActivityResultLauncher<Intent> activityResultLaunch = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            result -> {
                if (result.getResultCode() == Activity.RESULT_OK && result.getData() != null && result.getData().getData() != null) {
                    filePath = result.getData().getData();
                    Log.i(TAG, "ActivityResultLauncher: get result value");
                    try {
                        Bitmap bitmap = MediaStore.Images.Media
                                .getBitmap(
                                        requireContext().getContentResolver(),
                                        filePath
                                );
                        Log.i(TAG, "ActivityResultLauncher: create bitmap");
                        binding.profileImage.setImageBitmap(bitmap);
                        Log.i(TAG, "ActivityResultLauncher: binding.profileImage set bitmap");
                    } catch (IOException exception) {
                        Toast.makeText(getContext(), exception.getMessage(), Toast.LENGTH_LONG).show();
                    }
                    uploadImage();
                    Log.i(TAG, "ActivityResultLauncher: call uploadImage()");
                }
            });

    private void selectImage() {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        activityResultLaunch.launch(intent);
        Log.i(TAG, "selectImage: start choose image intent");
    }

    private void uploadImage() {
        if (filePath != null) {
            String uid = Objects.requireNonNull(FirebaseAuth.getInstance().getCurrentUser()).getUid();
            FirebaseStorage.getInstance().getReference().child("images/").child("profile_images").child(uid)
                    .putFile(filePath).addOnSuccessListener(taskSnapshot -> {
                        Toast.makeText(getContext(), R.string.photo_uploaded_successfully, Toast.LENGTH_LONG).show();

                        FirebaseStorage.getInstance().getReference().child("images/").child("profile_images").child(uid).getDownloadUrl()
                                .addOnSuccessListener(uri -> FirebaseDatabase.getInstance().getReference()
                                        .child("Users")
                                        .child(FirebaseAuth.getInstance().getCurrentUser().getUid())
                                        .child("profile_image").setValue(uri.toString()));
                    });
        }
    }

    private void updateToolbar() {
        Toolbar toolbar = getActivity().findViewById(R.id.toolbar);
        toolbar.setTitle(R.string.profile_title);
        Log.i(TAG, "updateView: toolbar change title profile_title");
    }
}