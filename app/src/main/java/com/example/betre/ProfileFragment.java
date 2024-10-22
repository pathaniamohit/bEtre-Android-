package com.example.betre;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.betre.adapters.ImageAdapter_profile;
import com.example.betre.models.Post;
import com.example.betre.models.User;
import com.google.firebase.appcheck.FirebaseAppCheck;
import com.google.firebase.appcheck.playintegrity.PlayIntegrityAppCheckProviderFactory;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ProfileFragment extends Fragment {
    private static final String TAG = "ProfileFragment";

    private ImageView profileImage;
    private TextView profileName, profileEmail, photosCount, followersCount, followsCount;
    private Uri imageUri;
    private FirebaseAuth mAuth;
    private StorageReference mStorageRef;
    private DatabaseReference mDatabase;
    private RecyclerView postsRecyclerView;
    private ImageAdapter_profile postAdapter;
    private List<Post> postList;
    private ImageView settingsButton;

    public ProfileFragment() {}

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_profile, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        Log.d(TAG, "onViewCreated: Initializing UI elements.");
        profileImage = view.findViewById(R.id.profile_picture);
        profileName = view.findViewById(R.id.profile_name);
        profileEmail = view.findViewById(R.id.profile_email);
        photosCount = view.findViewById(R.id.photos_count);
        followersCount = view.findViewById(R.id.followers_count);
        followsCount = view.findViewById(R.id.follows_count);
        postsRecyclerView = view.findViewById(R.id.images_grid);
        settingsButton = view.findViewById(R.id.settings_button);

        postsRecyclerView.setLayoutManager(new GridLayoutManager(getContext(), 1));
        postList = new ArrayList<>();
        postAdapter = new ImageAdapter_profile(getContext(), postList, true);
        postsRecyclerView.setAdapter(postAdapter);

        Log.d(TAG, "onViewCreated: Initializing Firebase components.");
        mAuth = FirebaseAuth.getInstance();
        mStorageRef = FirebaseStorage.getInstance().getReference("user_images");
        mDatabase = FirebaseDatabase.getInstance().getReference();
        FirebaseAppCheck.getInstance().installAppCheckProviderFactory(PlayIntegrityAppCheckProviderFactory.getInstance());

        FirebaseUser user = mAuth.getCurrentUser();
        if (user != null) {
            Log.d(TAG, "onViewCreated: User is authenticated. Loading profile data.");
            loadUserProfile(user.getUid());
            loadUserPosts(user.getUid());
            loadPhotosCount(user.getUid());
            loadFollowersCount(user.getUid());
            loadFollowsCount(user.getUid());
        } else {
            Log.w(TAG, "onViewCreated: No authenticated user found.");
        }

        profileImage.setOnClickListener(v -> openImagePicker());

        settingsButton.setOnClickListener(v -> {
            Log.d(TAG, "onViewCreated: Navigating to SettingsFragment.");
            getParentFragmentManager().beginTransaction()
                    .replace(R.id.home_content, new SettingFragment())
                    .addToBackStack(null)
                    .commit();
        });
    }

    private void openImagePicker() {
        Log.d(TAG, "openImagePicker: Opening image picker.");
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(Intent.createChooser(intent, "Select Picture"), 1);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 1 && resultCode == Activity.RESULT_OK && data != null && data.getData() != null) {
            imageUri = data.getData();
            Log.d(TAG, "onActivityResult: Image selected: " + imageUri);

            try {
                Bitmap resizedBitmap = getResizedBitmap(imageUri);
                profileImage.setImageBitmap(resizedBitmap);
                uploadImageToFirebaseStorage(resizedBitmap);
            } catch (IOException e) {
                Log.e(TAG, "Error resizing image: " + e.getMessage());
            }
        } else {
            Log.w(TAG, "onActivityResult: No image selected or operation cancelled.");
        }
    }

    private Bitmap getResizedBitmap(Uri imageUri) throws IOException {
        InputStream imageStream = getActivity().getContentResolver().openInputStream(imageUri);

        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = true;
        BitmapFactory.decodeStream(imageStream, null, options);
        imageStream.close();

        int desiredWidth = 1024;
        int desiredHeight = 1024;
        int scaleFactor = Math.min(options.outWidth / desiredWidth, options.outHeight / desiredHeight);

        options.inJustDecodeBounds = false;
        options.inSampleSize = scaleFactor;

        imageStream = getActivity().getContentResolver().openInputStream(imageUri);
        Bitmap resizedBitmap = BitmapFactory.decodeStream(imageStream, null, options);
        imageStream.close();

        return resizedBitmap;
    }

    private void uploadImageToFirebaseStorage(Bitmap resizedBitmap) {
        if (resizedBitmap != null) {
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            resizedBitmap.compress(Bitmap.CompressFormat.JPEG, 80, baos);
            byte[] data = baos.toByteArray();

            StorageReference storageRef = FirebaseStorage.getInstance().getReference()
                    .child("users/" + FirebaseAuth.getInstance().getCurrentUser().getUid() + "/profile.jpg");

            storageRef.putBytes(data)
                    .addOnSuccessListener(taskSnapshot -> storageRef.getDownloadUrl().addOnSuccessListener(uri -> {
                        String downloadUrl = uri.toString();
                        saveImageUrlToDatabase(downloadUrl);
                    }))
                    .addOnFailureListener(e -> {
                        Log.e("uploadImageToFirebaseStorage", "Failed to upload image: " + e.getMessage());
                        Toast.makeText(getContext(), "Image upload failed: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                    });
        }
    }

    private void saveImageUrlToDatabase(String downloadUrl) {
        String userId = FirebaseAuth.getInstance().getCurrentUser().getUid();
        DatabaseReference userRef = FirebaseDatabase.getInstance().getReference("users").child(userId);

        Map<String, Object> updates = new HashMap<>();
        updates.put("profileImageUrl", downloadUrl);

        userRef.updateChildren(updates).addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                Toast.makeText(getContext(), "Profile image updated", Toast.LENGTH_SHORT).show();
            } else {
                Log.e("saveImageUrlToDatabase", "Failed to update profile image URL");
            }
        });
    }

    private void loadUserProfile(String userId) {
        Log.d(TAG, "loadUserProfile: Fetching user profile data for userId: " + userId);
        mDatabase.child("users").child(userId).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    Log.d(TAG, "onDataChange: User data found.");
                    User user = dataSnapshot.getValue(User.class);
                    if (user != null) {
                        Log.d(TAG, "onDataChange: Setting user profile data.");
                        profileName.setText(user.getUsername() != null ? user.getUsername() : "Username");
                        profileEmail.setText(user.getEmail() != null ? user.getEmail() : "Email");

                        Glide.with(ProfileFragment.this)
                                .load(user.getProfileImageUrl())
                                .circleCrop()
                                .placeholder(R.drawable.ic_profile_placeholder)
                                .into(profileImage);

                    }
                } else {
                    Log.w(TAG, "onDataChange: User data not found.");
                    showToast("User data not found");
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Log.e(TAG, "onCancelled: Failed to fetch user data: " + databaseError.getMessage(), databaseError.toException());
                showToast("Failed to fetch user data");
            }
        });
    }

    private void loadPhotosCount(String userId) {
        DatabaseReference postsRef = FirebaseDatabase.getInstance().getReference("posts");

        postsRef.orderByChild("userId").equalTo(userId).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                int count = 0;

                for (DataSnapshot postSnapshot : snapshot.getChildren()) {
                    count++;
                }

                photosCount.setText(String.valueOf(count));
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Log.e(TAG, "Failed to load photos count: " + error.getMessage());
            }
        });
    }


    private void loadFollowersCount(String userId) {
        Log.d(TAG, "loadFollowersCount: Fetching followers count for userId: " + userId);
        mDatabase.child("followers").child(userId).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                int count = (int) snapshot.getChildrenCount();
                Log.d(TAG, "onDataChange: Followers count: " + count);
                followersCount.setText(String.valueOf(count));
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Log.e(TAG, "onCancelled: Failed to fetch followers count: " + error.getMessage(), error.toException());
            }
        });
    }

    private void loadFollowsCount(String userId) {
        Log.d(TAG, "loadFollowsCount: Fetching follows count for userId: " + userId);
        mDatabase.child("following").child(userId).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                int count = (int) snapshot.getChildrenCount();
                Log.d(TAG, "onDataChange: Follows count: " + count);
                followsCount.setText(String.valueOf(count));
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Log.e(TAG, "onCancelled: Failed to fetch follows count: " + error.getMessage(), error.toException());
            }
        });
    }

    private void loadUserPosts(String userId) {
        DatabaseReference postsRef = FirebaseDatabase.getInstance().getReference("posts");

        postsRef.orderByChild("userId").equalTo(userId).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                postList.clear();
                for (DataSnapshot postSnapshot : snapshot.getChildren()) {
                    Post post = postSnapshot.getValue(Post.class);
                    if (post != null) {
                        post.setPostId(postSnapshot.getKey());
                        postList.add(post);
                    }
                }
                postAdapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(getContext(), "Error loading posts: " + error.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void showToast(String message) {
        Toast.makeText(getActivity(), message, Toast.LENGTH_SHORT).show();
    }
}
