package com.example.betre;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.example.betre.adapters.ImageAdapter_profile;
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
import java.util.Map;;



/**
 * A simple {@link Fragment} subclass.
 * Use the {@link ProfileModFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class ProfileModFragment extends Fragment {

    LinearLayout edit_profile_layout,privacy_layout,about_layout,logout_layout;

    private static final String TAG = "ProfileFragment";

    private ImageView profileImage;
    private TextView profileName, profileEmail;
    private Uri imageUri;
    private FirebaseAuth mAuth;
    private StorageReference mStorageRef;
    private DatabaseReference mDatabase;

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    public ProfileModFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment ProfileAdminFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static ProfileAdminFragment newInstance(String param1, String param2) {
        ProfileAdminFragment fragment = new ProfileAdminFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_profile_mod, container, false);

        edit_profile_layout = view.findViewById(R.id.edit_profile_layout);
        privacy_layout = view.findViewById(R.id.privacy_layout);
        about_layout = view.findViewById(R.id.about_layout);
        logout_layout = view.findViewById(R.id.logout_layout);

        Log.d(TAG, "onViewCreated: Initializing UI elements.");
        profileImage = view.findViewById(R.id.profile_picture);
        profileName = view.findViewById(R.id.profile_name);
        profileEmail = view.findViewById(R.id.profile_email);
        Log.d(TAG, "onViewCreated: Initializing Firebase components.");
        mAuth = FirebaseAuth.getInstance();
        mStorageRef = FirebaseStorage.getInstance().getReference("user_images");
        mDatabase = FirebaseDatabase.getInstance().getReference();
        FirebaseAppCheck.getInstance().installAppCheckProviderFactory(PlayIntegrityAppCheckProviderFactory.getInstance());

        FirebaseUser user = mAuth.getCurrentUser();
        if (user != null) {
            Log.d(TAG, "onViewCreated: User is authenticated. Loading profile data.");
            loadUserProfile(user.getUid());
        } else {
            Log.w(TAG, "onViewCreated: No authenticated user found.");
        }

        profileImage.setOnClickListener(v -> openImagePicker());

        edit_profile_layout.setOnClickListener(v -> {
            getParentFragmentManager().beginTransaction()
                    .replace(R.id.home_content, new Edit_Profile_Fragment())
                    .addToBackStack(null)
                    .commit();
        });

        about_layout.setOnClickListener(v -> {
            getParentFragmentManager().beginTransaction()
                    .replace(R.id.home_content, new AboutFragment())
                    .addToBackStack(null)
                    .commit();
        });

        privacy_layout.setOnClickListener(v -> {
            getParentFragmentManager().beginTransaction()
                    .replace(R.id.home_content, new PrivacyFragment())
                    .addToBackStack(null)
                    .commit();
        });

        logout_layout.setOnClickListener(v -> {
            new AlertDialog.Builder(getActivity())
                    .setTitle("Logout")
                    .setMessage("Are you sure you want to logout?")
                    .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            mAuth.signOut();

                            Intent intent = new Intent(getActivity(), LoginActivity.class);
                            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
                            startActivity(intent);

                            if (getActivity() != null) {
                                getActivity().finish();
                            }
                        }
                    })
                    .setNegativeButton("No", null)
                    .show();
        });

        return view;
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

                        Glide.with(ProfileModFragment.this)
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
    private void showToast(String message) {
        Toast.makeText(getActivity(), message, Toast.LENGTH_SHORT).show();
    }

}