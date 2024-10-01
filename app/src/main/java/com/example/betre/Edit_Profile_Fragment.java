package com.example.betre;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.bumptech.glide.Glide;
import com.google.android.material.button.MaterialButton;
import com.google.firebase.appcheck.FirebaseAppCheck;
import com.google.firebase.appcheck.playintegrity.PlayIntegrityAppCheckProviderFactory;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.EmailAuthProvider;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

public class Edit_Profile_Fragment extends Fragment {

    private static final String TAG = "EditProfileFragment";
    private static final int PICK_IMAGE_REQUEST = 1;

    private ImageView back_button;
    private ImageView profileImage;
    private EditText usernameInput, emailInput, phoneInput;
    private Button updateButton, changePasswordButton;

    private FirebaseAuth mAuth;
    private DatabaseReference mDatabase;
    private StorageReference mStorageRef;
    private String userId;

    private String initialUsername;
    private String initialPhone;
    private String profileImageUrl;

    public Edit_Profile_Fragment() {
    }

    public static Edit_Profile_Fragment newInstance(String param1, String param2) {
        Edit_Profile_Fragment fragment = new Edit_Profile_Fragment();
        Bundle args = new Bundle();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_edit__profile_, container, false);

        back_button = view.findViewById(R.id.back_button);
        profileImage = view.findViewById(R.id.profile_image);
        usernameInput = view.findViewById(R.id.username_input);
        emailInput = view.findViewById(R.id.email_input);
        phoneInput = view.findViewById(R.id.phone_input);
        updateButton = view.findViewById(R.id.button_update);
        changePasswordButton = view.findViewById(R.id.button_change_password);

        mAuth = FirebaseAuth.getInstance();
        mDatabase = FirebaseDatabase.getInstance().getReference();
        mStorageRef = FirebaseStorage.getInstance().getReference("profile_pictures");
        FirebaseUser user = mAuth.getCurrentUser();
        FirebaseAppCheck.getInstance().installAppCheckProviderFactory(PlayIntegrityAppCheckProviderFactory.getInstance());

        if (user != null) {
            userId = user.getUid();
            Log.d(TAG, "User ID: " + userId);
            fetchUserData();
        } else {
            Log.w(TAG, "User is not authenticated");
        }

        profileImage.setOnClickListener(v -> openImagePicker());

        back_button.setOnClickListener(v -> {
            getParentFragmentManager().beginTransaction()
                    .replace(R.id.home_content, new SettingFragment())
                    .addToBackStack(null)
                    .commit();
        });

        updateButton.setOnClickListener(v -> updateUserProfile());

        changePasswordButton.setOnClickListener(v -> {
            showChangePasswordDialog();
        });

        return view;
    }

    private void fetchUserData() {
        Log.d(TAG, "Fetching user data from Firebase");

        mDatabase.child("users").child(userId).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    initialUsername = dataSnapshot.child("username").getValue(String.class);
                    initialPhone = dataSnapshot.child("phone").getValue(String.class);
                    String email = dataSnapshot.child("email").getValue(String.class);
                    profileImageUrl = dataSnapshot.child("profileImageUrl").getValue(String.class);

                    Log.d(TAG, "User data retrieved: Username: " + initialUsername + ", Phone: " + initialPhone + ", Email: " + email);

                    usernameInput.setText(initialUsername);
                    phoneInput.setText(initialPhone);
                    emailInput.setText(email);

                    emailInput.setEnabled(false);

                    if (profileImageUrl != null && !profileImageUrl.isEmpty()) {
                        Glide.with(Edit_Profile_Fragment.this)
                                .load(profileImageUrl)
                                .circleCrop()
                                .placeholder(R.drawable.ic_profile_placeholder)
                                .into(profileImage);
                    }
                } else {
                    Log.w(TAG, "User data does not exist");
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Log.e(TAG, "Failed to fetch user data: " + databaseError.getMessage(), databaseError.toException());
            }
        });
    }

    private void openImagePicker() {
        Log.d(TAG, "Opening image picker");
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(Intent.createChooser(intent, "Select Picture"), PICK_IMAGE_REQUEST);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == PICK_IMAGE_REQUEST && resultCode == Activity.RESULT_OK && data != null && data.getData() != null) {
            Uri imageUri = data.getData();
            Log.d(TAG, "Image selected: " + imageUri);
            profileImage.setImageURI(imageUri);
            uploadImageToFirebaseStorage(imageUri);
        } else {
            Log.w(TAG, "No image selected or operation cancelled.");
        }
    }

    private void uploadImageToFirebaseStorage(Uri imageUri) {
        if (imageUri != null) {
            Log.d(TAG, "Uploading image to Firebase Storage");
            String fileName = userId + "_" + System.currentTimeMillis() + ".jpg";
            StorageReference fileRef = mStorageRef.child(fileName);

            fileRef.putFile(imageUri)
                    .addOnSuccessListener(taskSnapshot -> {
                        Log.d(TAG, "Image uploaded successfully");
                        fileRef.getDownloadUrl().addOnSuccessListener(uri -> {
                            String imageUrl = uri.toString();
                            Log.d(TAG, "Image download URL: " + imageUrl);
                            saveProfileImageUrlToDatabase(imageUrl);
                        });
                    })
                    .addOnFailureListener(e -> {
                        Log.e(TAG, "Failed to upload image: " + e.getMessage(), e);
                        showToast("Failed to upload image: " + e.getMessage());
                    });
        } else {
            Log.w(TAG, "No image URI to upload");
            showToast("No image selected");
        }
    }

    private void saveProfileImageUrlToDatabase(String imageUrl) {
        Log.d(TAG, "Saving profile image URL to Realtime Database");
        mDatabase.child("users").child(userId).child("profileImageUrl").setValue(imageUrl)
                .addOnSuccessListener(aVoid -> {
                    Log.d(TAG, "Profile image URL saved successfully");
                    showToast("Profile picture updated");
                })
                .addOnFailureListener(e -> {
                    Log.e(TAG, "Failed to save profile image URL: " + e.getMessage(), e);
                    showToast("Failed to save profile image URL: " + e.getMessage());
                });
    }

    private void updateUserProfile() {
        Log.d(TAG, "Updating user profile");

        String newUsername = usernameInput.getText().toString().trim();
        String newPhone = phoneInput.getText().toString().trim();

        if (newUsername.isEmpty() || newUsername.length() < 8) {
            usernameInput.setError("Username must be at least 8 characters");
            Log.e(TAG, "Invalid username: " + newUsername);
            return;
        }

        if (newPhone.isEmpty() || newPhone.length() != 10 || !android.util.Patterns.PHONE.matcher(newPhone).matches()) {
            phoneInput.setError("Phone number must be 10 digits");
            Log.e(TAG, "Invalid phone number: " + newPhone);
            return;
        }

        boolean isUsernameChanged = !newUsername.equals(initialUsername);
        boolean isPhoneChanged = !newPhone.equals(initialPhone);

        if (isUsernameChanged || isPhoneChanged) {
            Log.d(TAG, "Changes detected. Username changed: " + isUsernameChanged + ", Phone changed: " + isPhoneChanged);

            DatabaseReference userRef = mDatabase.child("users").child(userId);
            if (isUsernameChanged) {
                userRef.child("username").setValue(newUsername)
                        .addOnSuccessListener(aVoid -> Log.d(TAG, "Username updated successfully"))
                        .addOnFailureListener(e -> Log.e(TAG, "Failed to update username: " + e.getMessage(), e));
            }

            if (isPhoneChanged) {
                userRef.child("phone").setValue(newPhone)
                        .addOnSuccessListener(aVoid -> Log.d(TAG, "Phone number updated successfully"))
                        .addOnFailureListener(e -> Log.e(TAG, "Failed to update phone number: " + e.getMessage(), e));
            }

            showToast("Profile updated successfully");

            initialUsername = newUsername;
            initialPhone = newPhone;
        } else {
            Log.d(TAG, "No changes detected");
            showToast("No changes made to update");
        }
    }

    private void showChangePasswordDialog() {
        LayoutInflater inflater = getLayoutInflater();
        View dialogView = inflater.inflate(R.layout.dialog_change_password, null);

        EditText currentPasswordInput = dialogView.findViewById(R.id.current_password_input);
        EditText newPasswordInput = dialogView.findViewById(R.id.new_password_input);
        EditText reenterPasswordInput = dialogView.findViewById(R.id.reenter_password_input);
        MaterialButton cancelButton = dialogView.findViewById(R.id.button_cancel_password);
        MaterialButton updateButton = dialogView.findViewById(R.id.button_update_password);

        AlertDialog alertDialog = new AlertDialog.Builder(getActivity())
                .setView(dialogView)
                .setCancelable(false)
                .create();

        cancelButton.setOnClickListener(v -> {
            alertDialog.dismiss();
        });

        updateButton.setOnClickListener(v -> {
            String currentPassword = currentPasswordInput.getText().toString().trim();
            String newPassword = newPasswordInput.getText().toString().trim();
            String reenterPassword = reenterPasswordInput.getText().toString().trim();

            if (currentPassword.isEmpty() || newPassword.isEmpty() || reenterPassword.isEmpty()) {
                showToast("All fields are required.");
                return;
            }

            if (newPassword.length() < 8) {
                showToast("New password must be at least 8 characters long.");
                return;
            }

            if (!newPassword.equals(reenterPassword)) {
                showToast("New password and Re-entered password do not match.");
                return;
            }

            updatePassword(currentPassword, newPassword, alertDialog);
        });

        alertDialog.show();
    }

    private void updatePassword(String currentPassword, String newPassword, AlertDialog alertDialog) {
        FirebaseUser user = mAuth.getCurrentUser();

        if (user != null) {
            AuthCredential credential = EmailAuthProvider.getCredential(user.getEmail(), currentPassword);
            user.reauthenticate(credential).addOnCompleteListener(task -> {
                if (task.isSuccessful()) {
                    Log.d(TAG, "User re-authenticated.");

                    // Now update the password
                    user.updatePassword(newPassword).addOnCompleteListener(passwordUpdateTask -> {
                        if (passwordUpdateTask.isSuccessful()) {
                            Log.d(TAG, "Password updated successfully.");
                            showToast("Password updated successfully.");
                            alertDialog.dismiss();
                        } else {
                            Log.e(TAG, "Failed to update password: " + passwordUpdateTask.getException().getMessage());
                            showToast("Failed to update password: " + passwordUpdateTask.getException().getMessage());
                        }
                    });
                } else {
                    Log.e(TAG, "Re-authentication failed: " + task.getException().getMessage());
                    showToast("Re-authentication failed. Please check your current password.");
                }
            });
        } else {
            Log.w(TAG, "No authenticated user found.");
            showToast("No authenticated user found. Please sign in again.");
        }
    }


    private void showToast(String message) {
        Toast.makeText(getActivity(), message, Toast.LENGTH_SHORT).show();
    }
}
