package com.example.betre;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.OpenableColumns;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.bumptech.glide.Glide;
import com.google.android.gms.common.api.Status;
import com.google.android.libraries.places.api.Places;
import com.google.android.libraries.places.api.model.Place;
import com.google.android.libraries.places.api.net.PlacesClient;
import com.google.android.libraries.places.widget.Autocomplete;
import com.google.android.libraries.places.widget.AutocompleteActivity;
import com.google.android.libraries.places.widget.model.AutocompleteActivityMode;
import com.google.firebase.FirebaseApp;
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
import com.google.firebase.storage.StorageMetadata;
import com.google.firebase.storage.StorageReference;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link CreateFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class CreateFragment extends Fragment {

    private static final int PICK_IMAGE_REQUEST = 1;

    private ImageView userProfileImage;
    private TextView userName, selectImage, addLocation;
    private EditText postContent;
    private Button buttonDiscard, buttonPost;

    private FirebaseAuth mAuth;
    private DatabaseReference mDatabase;
    private StorageReference mStorageRef;
    private Uri selectedImageUri;
    private String selectedLocation = "";

    private ActivityResultLauncher<Intent> locationPickerLauncher;

    public CreateFragment() {
        // Required empty public constructor
    }

    public static CreateFragment newInstance(String param1, String param2) {
        CreateFragment fragment = new CreateFragment();
        Bundle args = new Bundle();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Initialize Firebase and App Check
        FirebaseApp.initializeApp(requireContext());
        FirebaseAppCheck.getInstance().installAppCheckProviderFactory(PlayIntegrityAppCheckProviderFactory.getInstance());

        mAuth = FirebaseAuth.getInstance();
        mDatabase = FirebaseDatabase.getInstance().getReference("posts");
        mStorageRef = FirebaseStorage.getInstance().getReference("post_images");

        // Initialize Places API
        if (!Places.isInitialized()) {
            Places.initialize(requireContext(), "AIzaSyDCjCxf0f11NcCZVrR5XZLxT_xrNdmO7-8");
        }
        PlacesClient placesClient = Places.createClient(requireContext());

        // Set up the location picker launcher
        locationPickerLauncher = registerForActivityResult(new ActivityResultContracts.StartActivityForResult(), result -> {
            if (result.getResultCode() == Activity.RESULT_OK && result.getData() != null) {
                Place place = Autocomplete.getPlaceFromIntent(result.getData());
                selectedLocation = place.getAddress();
                addLocation.setText(selectedLocation);
            } else if (result.getResultCode() == AutocompleteActivity.RESULT_ERROR) {
                Status status = Autocomplete.getStatusFromIntent(result.getData());
                Toast.makeText(getContext(), "Error: " + status.getStatusMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_create, container, false);

        userProfileImage = view.findViewById(R.id.user_profile_image);
        userName = view.findViewById(R.id.user_name);
        selectImage = view.findViewById(R.id.select_image);
        addLocation = view.findViewById(R.id.add_location);
        postContent = view.findViewById(R.id.post_content);
        buttonDiscard = view.findViewById(R.id.button_discard);
        buttonPost = view.findViewById(R.id.button_post);

        FirebaseUser user = mAuth.getCurrentUser();
        if (user != null) {
            loadUserProfile(user.getUid());
        }

        selectImage.setOnClickListener(v -> openImagePicker());

//        addLocation.setOnClickListener(v -> showLocationDialog());
        addLocation.setOnClickListener(v -> openLocationPicker());


        buttonDiscard.setOnClickListener(v -> resetFields());

        buttonPost.setOnClickListener(v -> createPost());

        return view;
    }

    private void openImagePicker() {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(Intent.createChooser(intent, "Select Picture"), PICK_IMAGE_REQUEST);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == PICK_IMAGE_REQUEST && resultCode == Activity.RESULT_OK && data != null && data.getData() != null) {
            selectedImageUri = data.getData();
            displaySelectedImage();
        }
    }

    private void displaySelectedImage() {
        if (selectedImageUri != null) {
            selectImage.setText("Image Selected: " + getFileName(selectedImageUri));
        }
    }

    private String getFileName(Uri uri) {
        String result = null;

        if (uri.getScheme().equals("content")) {
            try (Cursor cursor = getContext().getContentResolver().query(uri, null, null, null, null)) {
                if (cursor != null && cursor.moveToFirst()) {
                    int nameIndex = cursor.getColumnIndex(OpenableColumns.DISPLAY_NAME);
                    if (nameIndex >= 0) {
                        result = cursor.getString(nameIndex);
                    }
                }
            }
        }

        if (result == null && uri.getPath() != null) {
            result = uri.getPath().substring(uri.getPath().lastIndexOf('/') + 1);
        }

        return result;
    }


//    private void showLocationDialog() {
//        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
//        View dialogView = getLayoutInflater().inflate(R.layout.dialog_add_location, null);
//        builder.setView(dialogView);
//
//        EditText locationInput = dialogView.findViewById(R.id.location_input);
//        Button buttonContinue = dialogView.findViewById(R.id.button_continue);
//
//        AlertDialog dialog = builder.create();
//
//        buttonContinue.setOnClickListener(v -> {
//            selectedLocation = locationInput.getText().toString().trim();
//            if (!TextUtils.isEmpty(selectedLocation)) {
//                addLocation.setText(selectedLocation);
//            }
//            dialog.dismiss();
//        });
//
//        dialog.show();
//    }

    private void openLocationPicker() {
        // Set up the fields to return from the autocomplete intent
        List<Place.Field> fields = Arrays.asList(Place.Field.ID, Place.Field.NAME, Place.Field.ADDRESS);

        // Build the autocomplete intent
        Intent intent = new Autocomplete.IntentBuilder(AutocompleteActivityMode.FULLSCREEN, fields).build(requireContext());

        // Launch the autocomplete activity
        locationPickerLauncher.launch(intent);
    }

    private void resetFields() {
        postContent.setText("");
        selectImage.setText("Select Image");
        addLocation.setText("Add Location");
        selectedImageUri = null;
        selectedLocation = "";
        Toast.makeText(getActivity(), "Fields reset", Toast.LENGTH_SHORT).show();
    }

    private void createPost() {
        String content = postContent.getText().toString().trim();

        if (TextUtils.isEmpty(content)) {
            Toast.makeText(getActivity(), "Content cannot be empty", Toast.LENGTH_SHORT).show();
            return;
        }

        if (selectedImageUri == null) {
            Toast.makeText(getActivity(), "Please select an image", Toast.LENGTH_SHORT).show();
            return;
        }

        if (TextUtils.isEmpty(selectedLocation)) {
            Toast.makeText(getActivity(), "Please add a location", Toast.LENGTH_SHORT).show();
            return;
        }

        uploadImageAndCreatePost(content);
    }

    private void uploadImageAndCreatePost(String content) {
        String userId = mAuth.getCurrentUser().getUid();
        String fileName = userId + "_" + System.currentTimeMillis();
        StorageReference fileRef = mStorageRef.child(fileName);

        StorageMetadata metadata = new StorageMetadata.Builder()
                .setCustomMetadata("userId", userId)
                .build();

        fileRef.putFile(selectedImageUri, metadata)
                .addOnSuccessListener(taskSnapshot -> fileRef.getDownloadUrl().addOnSuccessListener(uri -> {
                    createPostInDatabase(content, uri.toString());
                }))
                .addOnFailureListener(e -> {
                    Toast.makeText(getActivity(), "Failed to upload image: " + e.getMessage(), Toast.LENGTH_LONG).show();
                    e.printStackTrace();
                });
    }

    private void createPostInDatabase(String content, @Nullable String imageUrl) {
        String userId = mAuth.getCurrentUser().getUid();
        String postId = mDatabase.push().getKey();

        if (postId == null) {
            Toast.makeText(getActivity(), "Error: Could not generate post ID. Try again.", Toast.LENGTH_LONG).show();
            return;
        }

        Map<String, Object> postMap = new HashMap<>();
        postMap.put("userId", userId);
        postMap.put("content", content);
        postMap.put("location", selectedLocation);
        postMap.put("timestamp", System.currentTimeMillis());

        if (imageUrl != null) {
            postMap.put("imageUrl", imageUrl);
        }

        postMap.put("count_like", 0);
        postMap.put("count_comment", 0);
        postMap.put("is_reported", false);

        mDatabase.child(postId).setValue(postMap)
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        Toast.makeText(getActivity(), "Post created successfully", Toast.LENGTH_SHORT).show();
                        resetFields();
                    } else {
                        Toast.makeText(getActivity(), "Failed to create post", Toast.LENGTH_SHORT).show();
                    }
                })
                .addOnFailureListener(e -> {
                    Toast.makeText(getActivity(), "Database error: " + e.getMessage(), Toast.LENGTH_LONG).show();
                });
    }


    private void loadUserProfile(String userId) {
        DatabaseReference userRef = FirebaseDatabase.getInstance().getReference("users").child(userId);

        userRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    String username = dataSnapshot.child("username").getValue(String.class);
                    String profileImageUrl = dataSnapshot.child("profileImageUrl").getValue(String.class);

                    userName.setText(username != null ? username : "Unknown User");
                    if (profileImageUrl != null && !profileImageUrl.isEmpty()) {
                        Glide.with(CreateFragment.this)
                                .load(profileImageUrl)
                                .circleCrop()
                                .placeholder(R.drawable.ic_profile_placeholder)
                                .into(userProfileImage);
                    } else {
                        userProfileImage.setImageResource(R.drawable.ic_profile_placeholder);
                    }
                } else {
                    Toast.makeText(getActivity(), "User data not found", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Toast.makeText(getActivity(), "Error loading user data", Toast.LENGTH_SHORT).show();
            }
        });
    }

}
