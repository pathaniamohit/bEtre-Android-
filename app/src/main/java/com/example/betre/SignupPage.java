package com.example.betre;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.example.betre.models.User;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.FirebaseApp;
import com.google.firebase.appcheck.FirebaseAppCheck;
import com.google.firebase.appcheck.playintegrity.PlayIntegrityAppCheckProviderFactory;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.HashMap;
import java.util.Map;

public class SignupPage extends AppCompatActivity {

    private static final String TAG = "SignupPage";

    private EditText usernameEditText, emailEditText, phoneEditText, passwordEditText, confirmPasswordEditText;
    private RadioGroup genderRadioGroup;
    private Button signUpButton;
    private TextView signInTextView;
    private ImageView showPasswordButton, showConfirmPasswordButton;
    private FirebaseAuth mAuth;
    private DatabaseReference UsersDB;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup_page);
        FirebaseApp.initializeApp(this);

        FirebaseAppCheck firebaseAppCheck = FirebaseAppCheck.getInstance();
        firebaseAppCheck.installAppCheckProviderFactory(PlayIntegrityAppCheckProviderFactory.getInstance());

        mAuth = FirebaseAuth.getInstance();
        UsersDB = FirebaseDatabase.getInstance().getReference("users");

        usernameEditText = findViewById(R.id.name);
        emailEditText = findViewById(R.id.email);
        phoneEditText = findViewById(R.id.phone);
        passwordEditText = findViewById(R.id.password);
        confirmPasswordEditText = findViewById(R.id.confirm_password);
        genderRadioGroup = findViewById(R.id.genderRadioGroup);
        signUpButton = findViewById(R.id.register_button);
        signInTextView = findViewById(R.id.signin);
        showPasswordButton = findViewById(R.id.show_password_button);
        showConfirmPasswordButton = findViewById(R.id.show_confirm_password_button);

        showPasswordButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                togglePasswordVisibility(passwordEditText, showPasswordButton);
            }
        });

        showConfirmPasswordButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                togglePasswordVisibility(confirmPasswordEditText, showConfirmPasswordButton);
            }
        });

        signUpButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d(TAG, "Sign Up button clicked");
                signup();
            }
        });

        signInTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d(TAG, "Sign In text clicked, redirecting to LoginActivity");
                startActivity(new Intent(SignupPage.this, LoginActivity.class));
            }
        });
    }

    private void signup() {
        String name = usernameEditText.getText().toString().trim();
        String email = emailEditText.getText().toString().trim();
        String phone = phoneEditText.getText().toString().trim();
        String password = passwordEditText.getText().toString().trim();
        String confirmPassword = confirmPasswordEditText.getText().toString().trim();

        if (TextUtils.isEmpty(name) || name.length() < 8) {
            usernameEditText.setError("Username must be at least 8 characters");
            Log.e(TAG, "Invalid username: " + name);
            return;
        }

        if (TextUtils.isEmpty(email) || !Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            emailEditText.setError("Valid email is required");
            Log.e(TAG, "Invalid email: " + email);
            return;
        }

        if (TextUtils.isEmpty(phone) || phone.length() != 10 || !Patterns.PHONE.matcher(phone).matches()) {
            phoneEditText.setError("Phone number must be 10 digits");
            Log.e(TAG, "Invalid phone number: " + phone);
            return;
        }

        if (TextUtils.isEmpty(password) || password.length() < 8) {
            passwordEditText.setError("Password must be at least 8 characters");
            Log.e(TAG, "Invalid password: " + password);
            return;
        }

        if (TextUtils.isEmpty(confirmPassword) || !password.equals(confirmPassword)) {
            confirmPasswordEditText.setError("Passwords do not match");
            Log.e(TAG, "Password and confirm password do not match");
            return;
        }

        int selectedGenderId = genderRadioGroup.getCheckedRadioButtonId();
        if (selectedGenderId == -1) {
            Toast.makeText(this, "Please select your gender", Toast.LENGTH_SHORT).show();
            Log.e(TAG, "Gender not selected");
            return;
        }

        RadioButton selectedGenderButton = findViewById(selectedGenderId);
        String gender = selectedGenderButton.getText().toString();
        Log.d(TAG, "User details - Name: " + name + ", Email: " + email + ", Phone: " + phone + ", Gender: " + gender);

        checkEmailUnique(email, name, phone, gender, password);
    }

    private void checkEmailUnique(String email, String name, String phone, String gender, String password) {
        Log.d(TAG, "Checking if email is unique");
        UsersDB.orderByChild("email").equalTo(email).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    emailEditText.setError("Email already exists");
                    Log.e(TAG, "Email already exists: " + email);
                } else {
                    createUserWithFirebase(email, password, name, phone, gender);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Log.e(TAG, "Error checking email uniqueness: " + error.getMessage());
                Toast.makeText(SignupPage.this, "Error: " + error.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void createUserWithFirebase(String email, String password, String name, String phone, String gender) {
        Log.d(TAG, "Creating user with Firebase Authentication");
        mAuth.createUserWithEmailAndPassword(email, password).addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if (task.isSuccessful()) {
                    FirebaseUser user = mAuth.getCurrentUser();
                    String userId = user.getUid();

                    User newUser = new User(name, email, phone, gender, "user");
                    newUser.setSuspended(false);
                    Log.d(TAG, "User created successfully with ID: " + userId);

                    UsersDB.child(userId).setValue(newUser).addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if (task.isSuccessful()) {
                                Log.d(TAG, "User data saved successfully in the database");
                                Toast.makeText(SignupPage.this, "Registration successful!", Toast.LENGTH_SHORT).show();
                                startActivity(new Intent(SignupPage.this, LoginActivity.class)); // Redirect to login activity
                                finish();
                            } else {
                                Log.e(TAG, "Failed to save user data: " + task.getException().getMessage());
                                Toast.makeText(SignupPage.this, "Failed to save user data: " + task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                            }
                        }
                    });
                } else {
                    String errorMessage = "Authentication failed.";
                    try {
                        throw task.getException();
                    } catch (Exception e) {
                        errorMessage = e.getMessage();
                    }
                    Log.e(TAG, "User creation failed: " + errorMessage);
                    Toast.makeText(SignupPage.this, errorMessage, Toast.LENGTH_LONG).show();
                }
            }
        });
    }

    private void togglePasswordVisibility(EditText passwordEditText, ImageView toggleButton) {
        if (passwordEditText.getInputType() == 144) {
            passwordEditText.setInputType(129);
            toggleButton.setImageResource(R.drawable.ic_hide_password);
            Log.d(TAG, "Password visibility hidden");
        } else {
            passwordEditText.setInputType(144);
            toggleButton.setImageResource(R.drawable.ic_show_password);
            Log.d(TAG, "Password visibility shown");
        }
        passwordEditText.setSelection(passwordEditText.getText().length());
    }
}
