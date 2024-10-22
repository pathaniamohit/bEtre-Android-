package com.example.betre;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;

import com.bumptech.glide.Glide;
import com.example.betre.models.Post;
import com.google.android.libraries.places.api.Places;
import com.google.android.libraries.places.api.model.Place;
import com.google.android.libraries.places.api.net.PlacesClient;
import com.google.android.material.button.MaterialButton;
import com.google.android.libraries.places.widget.Autocomplete;
import com.google.android.libraries.places.widget.model.AutocompleteActivityMode;
import com.google.android.libraries.places.widget.AutocompleteActivity;
import com.google.android.gms.common.api.Status;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.appcheck.FirebaseAppCheck;
import com.google.firebase.appcheck.playintegrity.PlayIntegrityAppCheckProviderFactory;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageMetadata;
import com.google.firebase.storage.StorageReference;

import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Edit_Post_Fragment extends Fragment {

    private ImageView postImageView, backButton;
    private EditText contentEditText, locationEditText;
    private MaterialButton updateButton;
    private Uri selectedImageUri;
    private DatabaseReference postsRef;
    private StorageReference storageRef;

    private String postId;

    // Define ActivityResultLauncher for image picker
    private ActivityResultLauncher<Intent> imagePickerLauncher;

    // Define ActivityResultLauncher for location autocomplete
    private ActivityResultLauncher<Intent> locationPickerLauncher;

    public Edit_Post_Fragment() {
        // Required empty public constructor
    }

    public static Edit_Post_Fragment newInstance(String postId) {
        Edit_Post_Fragment fragment = new Edit_Post_Fragment();
        Bundle args = new Bundle();
        args.putString("postId", postId);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            postId = getArguments().getString("postId");
        }

        // Initialize Firebase references
        postsRef = FirebaseDatabase.getInstance().getReference("posts");
        storageRef = FirebaseStorage.getInstance().getReference("post_images");

        // Initialize Firebase App Check
        FirebaseAppCheck firebaseAppCheck = FirebaseAppCheck.getInstance();
        firebaseAppCheck.installAppCheckProviderFactory(
                PlayIntegrityAppCheckProviderFactory.getInstance());

        // Initialize Places API
        if (!Places.isInitialized()) {
            Places.initialize(requireContext(), "AIzaSyDCjCxf0f11NcCZVrR5XZLxT_xrNdmO7-8");
        }
        PlacesClient placesClient = Places.createClient(requireContext());

        // Set up the image picker launcher
        imagePickerLauncher = registerForActivityResult(new ActivityResultContracts.StartActivityForResult(), result -> {
            if (result.getResultCode() == Activity.RESULT_OK && result.getData() != null) {
                selectedImageUri = result.getData().getData();
                if (selectedImageUri != null) {
                    // Use Glide to load and resize the image
                    Glide.with(requireContext())
                            .load(selectedImageUri)
                            .override(800, 800)  // Resize to 800x800 pixels
                            .fitCenter()
                            .into(postImageView);  // Display the selected image
                }
            } else {
                Toast.makeText(getContext(), "Failed to pick image", Toast.LENGTH_SHORT).show();
            }
        });

        // Set up the location picker launcher
        locationPickerLauncher = registerForActivityResult(new ActivityResultContracts.StartActivityForResult(), result -> {
            if (result.getResultCode() == Activity.RESULT_OK && result.getData() != null) {
                Place place = Autocomplete.getPlaceFromIntent(result.getData());
                locationEditText.setText(place.getAddress());
            } else if (result.getResultCode() == AutocompleteActivity.RESULT_ERROR) {
                Status status = Autocomplete.getStatusFromIntent(result.getData());
                Toast.makeText(getContext(), "Error: " + status.getStatusMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_edit__post_, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        // Initialize views
        postImageView = view.findViewById(R.id.edit_post_image);
        contentEditText = view.findViewById(R.id.edit_post_content);
        locationEditText = view.findViewById(R.id.edit_post_location);
        updateButton = view.findViewById(R.id.button_update_post);
        backButton = view.findViewById(R.id.button_back);

        // Load post data into the views
        loadPostData();

        // Handle image selection
        postImageView.setOnClickListener(v -> openImagePicker());

        // Handle update post (upload the image only when "Update" is clicked)
        updateButton.setOnClickListener(v -> updatePostData());

        // Handle back button action
        backButton.setOnClickListener(v -> requireActivity().getSupportFragmentManager().popBackStack());

        // Handle location edit (open Google Places Autocomplete)
        locationEditText.setOnClickListener(v -> openLocationPicker());
    }

    private void loadPostData() {
        if (postId == null || postId.isEmpty()) {
            Toast.makeText(getContext(), "Invalid Post ID", Toast.LENGTH_SHORT).show();
            return;
        }

        postsRef.child(postId).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    Post post = snapshot.getValue(Post.class);
                    if (post != null) {
                        contentEditText.setText(post.getContent());
                        locationEditText.setText(post.getLocation());

                        // Load and resize the image using Glide
                        Glide.with(requireContext())
                                .load(post.getImageUrl())
                                .placeholder(R.drawable.sign1)
                                .override(800, 800)  // Resize image to prevent large bitmap error
                                .fitCenter()
                                .into(postImageView);
                    }
                } else {
                    Toast.makeText(getContext(), "Post not found", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(getContext(), "Error loading post data", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void openImagePicker() {
        Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
        intent.setType("image/*");
        imagePickerLauncher.launch(intent);
    }

    private void openLocationPicker() {
        // Set up the fields to return from the autocomplete intent
        List<Place.Field> fields = Arrays.asList(Place.Field.ID, Place.Field.NAME, Place.Field.ADDRESS);

        // Build the autocomplete intent
        Intent intent = new Autocomplete.IntentBuilder(AutocompleteActivityMode.FULLSCREEN, fields).build(requireContext());

        // Launch the autocomplete activity
        locationPickerLauncher.launch(intent);
    }

    private void updatePostData() {
        String newContent = contentEditText.getText().toString().trim();
        String newLocation = locationEditText.getText().toString().trim();

        if (TextUtils.isEmpty(newContent) || TextUtils.isEmpty(newLocation)) {
            Toast.makeText(getContext(), "Content or Location cannot be empty", Toast.LENGTH_SHORT).show();
            return;
        }

        // Create the post update map
        Map<String, Object> postUpdates = new HashMap<>();
        postUpdates.put("content", newContent);
        postUpdates.put("location", newLocation);

        if (selectedImageUri != null) {
            // Get the current user ID
            String userId = FirebaseAuth.getInstance().getCurrentUser().getUid();

            // Upload the image after resizing
            StorageReference fileRef = storageRef.child(postId + "_" + System.currentTimeMillis());

            // Add metadata to the upload to include the userId
            StorageMetadata metadata = new StorageMetadata.Builder()
                    .setCustomMetadata("userId", userId)
                    .build();

            try {
                InputStream imageStream = requireContext().getContentResolver().openInputStream(selectedImageUri);
                Bitmap originalBitmap = BitmapFactory.decodeStream(imageStream);

                // Compress bitmap to prevent large upload
                ByteArrayOutputStream baos = new ByteArrayOutputStream();
                originalBitmap.compress(Bitmap.CompressFormat.JPEG, 80, baos);  // Compress to 80% quality
                byte[] imageData = baos.toByteArray();

                fileRef.putBytes(imageData, metadata)  // Pass metadata during the upload
                        .addOnSuccessListener(taskSnapshot -> fileRef.getDownloadUrl().addOnSuccessListener(uri -> {
                            postUpdates.put("imageUrl", uri.toString());
                            savePostUpdates(postUpdates);
                        }))
                        .addOnFailureListener(e -> Toast.makeText(getContext(), "Failed to upload image", Toast.LENGTH_SHORT).show());
            } catch (Exception e) {
                e.printStackTrace();
                Toast.makeText(getContext(), "Error processing image", Toast.LENGTH_SHORT).show();
            }
        } else {
            // Save post updates without changing the image
            savePostUpdates(postUpdates);
        }
    }

    private void savePostUpdates(Map<String, Object> postUpdates) {
        postsRef.child(postId).updateChildren(postUpdates).addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                Toast.makeText(getContext(), "Post updated successfully", Toast.LENGTH_SHORT).show();
                requireActivity().getSupportFragmentManager().popBackStack();  // Navigate back
            } else {
                Toast.makeText(getContext(), "Failed to update post", Toast.LENGTH_SHORT).show();
            }
        });
    }


}
