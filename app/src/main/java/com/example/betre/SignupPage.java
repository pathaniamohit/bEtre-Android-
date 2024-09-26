package com.example.betre;

import android.os.Bundle;
import android.text.TextUtils;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class SignupPage extends AppCompatActivity {

    private EditText usernameEditText, emailEditText, phoneEditText, passwordEditText, confirmPasswordEditText;
    private RadioGroup genderRadioGroup;
    private Button signUpButton;
    private TextView signInTextView;
    private FirebaseAuth mAuth;
    private DatabaseReference UsersDB;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup_page);

        // Initialize Firebase Auth
        mAuth = FirebaseAuth.getInstance();
        UsersDB = FirebaseDatabase.getInstance().getReference("users");

        // Initialize UI components
        usernameEditText = findViewById(R.id.name);
        emailEditText = findViewById(R.id.email);
        phoneEditText = findViewById(R.id.phone); // Added phoneEditText
        passwordEditText = findViewById(R.id.password);
        confirmPasswordEditText = findViewById(R.id.confirm_password);
        genderRadioGroup = findViewById(R.id.genderRadioGroup);
        signUpButton = findViewById(R.id.register_button);
        signInTextView = findViewById(R.id.acc1);

        // Set click listener for the Sign Up button
        signUpButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                signup();
            }
        });

        // Set click listener for Sign In text view
        signInTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                android.content.Intent intent = new android.content.Intent(SignupPage.this, LoginActivity.class);
                startActivity(intent);
            }
        });
    }

    private void signup() {
        String name = usernameEditText.getText().toString().trim();
        String email = emailEditText.getText().toString().trim();
        String phone = phoneEditText.getText().toString().trim(); // Get phone number
        String password = passwordEditText.getText().toString().trim();
        String confirmPassword = confirmPasswordEditText.getText().toString().trim();

        // Validate input fields
        if (TextUtils.isEmpty(name)) {
            usernameEditText.setError("Name is required");
            return;
        }

        if (TextUtils.isEmpty(email) || !Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            emailEditText.setError("Valid email is required");
            return;
        }

        if (TextUtils.isEmpty(phone)) {
            phoneEditText.setError("Phone number is required");
            return;
        }

        if (!Patterns.PHONE.matcher(phone).matches()) {
            phoneEditText.setError("Enter a valid phone number");
            return;
        }

        if (TextUtils.isEmpty(password)) {
            passwordEditText.setError("Password is required");
            return;
        }

        if (password.length() < 6) {
            passwordEditText.setError("Password must be at least 6 characters");
            return;
        }

        if (TextUtils.isEmpty(confirmPassword)) {
            confirmPasswordEditText.setError("Confirm Password is required");
            return;
        }

        if (!password.equals(confirmPassword)) {
            confirmPasswordEditText.setError("Passwords do not match");
            return;
        }

        // Check for gender selection
        int selectedGenderId = genderRadioGroup.getCheckedRadioButtonId();
        if (selectedGenderId == -1) {
            Toast.makeText(this, "Please select your gender", Toast.LENGTH_SHORT).show();
            return;
        }

        // Get the selected RadioButton and extract the text
        RadioButton selectedGenderButton = findViewById(selectedGenderId);
        String gender = selectedGenderButton.getText().toString();

        // Create user with Firebase Authentication
        mAuth.createUserWithEmailAndPassword(email, password).addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if (task.isSuccessful()) {
                    // User registration successful
                    FirebaseUser user = mAuth.getCurrentUser();
                    String userId = user.getUid();
                    User newUser = new User(name, email, phone, gender); // Pass phone number

                    // Save user data to the database
                    UsersDB.child(userId).setValue(newUser).addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if (task.isSuccessful()) {
                                Toast.makeText(SignupPage.this, "Registration successful!", Toast.LENGTH_SHORT).show();
                                startActivity(new android.content.Intent(SignupPage.this, LoginActivity.class));
                                finish();
                            } else {
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
                    Toast.makeText(SignupPage.this, errorMessage, Toast.LENGTH_LONG).show();
                }
            }
        });
    }
}
