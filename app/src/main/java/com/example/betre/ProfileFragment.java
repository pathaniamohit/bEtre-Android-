package com.example.betre;

import android.app.Activity;
import android.content.Intent;
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
import com.example.betre.adapters.ImageAdapter;
import com.example.betre.models.User;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.ArrayList;
import java.util.List;

public class ProfileFragment extends Fragment {
    private static final int PICK_IMAGE_REQUEST = 1;
    private static final String TAG = "ProfileFragment";

    private ImageView profileImage;
    private TextView profileName, profileEmail;
    private Uri imageUri;
    private FirebaseAuth mAuth;
    private StorageReference mStorageRef;
    private DatabaseReference mDatabase;
    private RecyclerView imagesRecyclerView;
    private ImageAdapter imageAdapter;
    private List<String> imageUrls = new ArrayList<>();
    private ImageView settings_button;

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
        settings_button = view.findViewById(R.id.settings_button);
        imagesRecyclerView = view.findViewById(R.id.images_grid);
        imagesRecyclerView.setLayoutManager(new GridLayoutManager(getContext(), 2));
        imageAdapter = new ImageAdapter(getContext(), imageUrls);
        imagesRecyclerView.setAdapter(imageAdapter);

        Log.d(TAG, "onViewCreated: Initializing Firebase components.");
        mAuth = FirebaseAuth.getInstance();
        mStorageRef = FirebaseStorage.getInstance().getReference("profile_pictures");
        mDatabase = FirebaseDatabase.getInstance().getReference();

        FirebaseUser user = mAuth.getCurrentUser();
        if (user != null) {
            Log.d(TAG, "onViewCreated: User is authenticated. Loading profile data.");
            loadUserProfile(user.getUid());
            getUserImages();
        } else {
            Log.w(TAG, "onViewCreated: No authenticated user found.");
        }

        profileImage.setOnClickListener(v -> openImagePicker());

        settings_button.setOnClickListener(v -> {
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
        startActivityForResult(Intent.createChooser(intent, "Select Picture"), PICK_IMAGE_REQUEST);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == PICK_IMAGE_REQUEST && resultCode == Activity.RESULT_OK && data != null && data.getData() != null) {
            imageUri = data.getData();
            Log.d(TAG, "onActivityResult: Image selected: " + imageUri);
            profileImage.setImageURI(imageUri);
            uploadImageToFirebaseStorage();
        } else {
            Log.w(TAG, "onActivityResult: No image selected or operation cancelled.");
        }
    }

    private void uploadImageToFirebaseStorage() {
        if (imageUri != null) {
            Log.d(TAG, "uploadImageToFirebaseStorage: Uploading image to Firebase Storage.");
            String userId = mAuth.getCurrentUser().getUid();
            StorageReference fileRef = mStorageRef.child(userId + "/profile.jpg");

            fileRef.putFile(imageUri)
                    .addOnSuccessListener(taskSnapshot -> {
                        Log.d(TAG, "uploadImageToFirebaseStorage: Image uploaded successfully.");
                        fileRef.getDownloadUrl().addOnSuccessListener(uri -> {
                            Log.d(TAG, "uploadImageToFirebaseStorage: Image download URL: " + uri.toString());
                            String imageUrl = uri.toString();
                            saveImageUrlToRealtimeDatabase(imageUrl);
                        });
                    })
                    .addOnFailureListener(e -> {
                        Log.e(TAG, "uploadImageToFirebaseStorage: Failed to upload image: " + e.getMessage(), e);
                        showToast("Failed to upload image: " + e.getMessage());
                    });
        } else {
            Log.w(TAG, "uploadImageToFirebaseStorage: No image URI to upload.");
            showToast("No image selected");
        }
    }

    private void saveImageUrlToRealtimeDatabase(String imageUrl) {
        Log.d(TAG, "saveImageUrlToRealtimeDatabase: Saving image URL to Realtime Database.");
        String userId = mAuth.getCurrentUser().getUid();
        mDatabase.child("users").child(userId).child("profileImageUrl").setValue(imageUrl)
                .addOnSuccessListener(aVoid -> {
                    Log.d(TAG, "saveImageUrlToRealtimeDatabase: Profile image updated successfully.");
                    showToast("Profile image updated");
                })
                .addOnFailureListener(e -> {
                    Log.e(TAG, "saveImageUrlToRealtimeDatabase: Failed to update profile image: " + e.getMessage(), e);
                    showToast("Failed to update profile image: " + e.getMessage());
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

    private void getUserImages() {
        Log.d(TAG, "getUserImages: Fetching user's images from Firestore.");
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        CollectionReference imagesRef = db.collection("user_images");

        imagesRef.get().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                Log.d(TAG, "getUserImages: Images fetched successfully.");
                QuerySnapshot documents = task.getResult();
                if (documents != null) {
                    for (DocumentSnapshot document : documents) {
                        String url = document.getString("url");
                        if (url != null) {
                            imageUrls.add(url);
                        }
                    }
                    Log.d(TAG, "getUserImages: Updating image adapter with new data.");
                    imageAdapter.notifyDataSetChanged();
                }
            } else {
                Log.e(TAG, "getUserImages: Error fetching images", task.getException());
            }
        });
    }

    private void showToast(String message) {
        Toast.makeText(getActivity(), message, Toast.LENGTH_SHORT).show();
    }
}
