package com.example.betre;

import android.content.Intent;
import android.os.Bundle;
import android.text.InputType;
import android.text.TextUtils;
import android.util.Log;
import android.util.Patterns;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.button.MaterialButton;
import com.google.firebase.FirebaseApp;
import com.google.firebase.appcheck.FirebaseAppCheck;
import com.google.firebase.appcheck.playintegrity.PlayIntegrityAppCheckProviderFactory;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException;
import com.google.firebase.auth.FirebaseAuthUserCollisionException;
import com.google.firebase.auth.FirebaseAuthWeakPasswordException;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class LoginActivity extends AppCompatActivity {

    private EditText emailLogin, passwordLogin;
    private MaterialButton loginButton;
    private TextView forgotPassword, signUpLink;
    private FirebaseAuth mAuth;
    private DatabaseReference UsersDB;
    private ImageView showPasswordButton;
    private boolean isPasswordVisible = false;

    private static final String TAG = "LoginActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        FirebaseApp.initializeApp(this);

        FirebaseAppCheck firebaseAppCheck = FirebaseAppCheck.getInstance();
        firebaseAppCheck.installAppCheckProviderFactory(PlayIntegrityAppCheckProviderFactory.getInstance());
        emailLogin = findViewById(R.id.emaillogin);
        passwordLogin = findViewById(R.id.passwordlogin);
        loginButton = findViewById(R.id.login);
        forgotPassword = findViewById(R.id.forgot_password);
        signUpLink = findViewById(R.id.signuplink);
        showPasswordButton = findViewById(R.id.show_password_button);

        mAuth = FirebaseAuth.getInstance();
        UsersDB = FirebaseDatabase.getInstance().getReference("users");

        signUpLink.setOnClickListener(v -> startActivity(new Intent(LoginActivity.this, SignupPage.class)));

        forgotPassword.setOnClickListener(v -> {
            startActivity(new Intent(LoginActivity.this, ForgotPassword.class));
            finish();
        });

        loginButton.setOnClickListener(v -> checkUserCredentials());

        showPasswordButton.setOnClickListener(v -> {
            if (isPasswordVisible) {
                passwordLogin.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);
                showPasswordButton.setImageResource(R.drawable.ic_hide_password);
            } else {
                passwordLogin.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD);
                showPasswordButton.setImageResource(R.drawable.ic_show_password);
            }
            passwordLogin.setSelection(passwordLogin.getText().length());
            isPasswordVisible = !isPasswordVisible;
        });
    }

    private void checkUserCredentials() {
        String email = emailLogin.getText().toString().trim();
        String password = passwordLogin.getText().toString().trim();

        if (TextUtils.isEmpty(email) || !Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            emailLogin.setError("Valid email is required");
            emailLogin.requestFocus();
            return;
        }

        if (TextUtils.isEmpty(password)) {
            passwordLogin.setError("Password is required");
            passwordLogin.requestFocus();
            return;
        }

        mAuth.signInWithEmailAndPassword(email, password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if (task.isSuccessful()) {
                    if (mAuth.getCurrentUser() != null) {
                        checkUserRole(mAuth.getCurrentUser().getUid());
                    }
                } else {
                    handleLoginError(task);
                }
            }
        }).addOnFailureListener(e -> Toast.makeText(LoginActivity.this, "Login failed!", Toast.LENGTH_SHORT).show());
    }

//    private void checkUserRole(String userId) {
//        UsersDB.child(userId).addListenerForSingleValueEvent(new ValueEventListener() {
//            @Override
//            public void onDataChange(@NonNull DataSnapshot snapshot) {
//                if (snapshot.exists()) {
//                    String role = snapshot.child("role").getValue(String.class);
//                    if (role != null) {
//                        Log.d(TAG, "User role found: " + role);
//                        navigateBasedOnRole(role);
//                    } else {
//                        Log.e(TAG, "Role not found for user");
//                        Toast.makeText(LoginActivity.this, "User role not found.", Toast.LENGTH_SHORT).show();
//                    }
//                } else {
//                    Log.e(TAG, "User not found in database");
//                    Toast.makeText(LoginActivity.this, "User not found.", Toast.LENGTH_SHORT).show();
//                }
//            }
//
//            @Override
//            public void onCancelled(@NonNull DatabaseError error) {
//                Log.e(TAG, "Error checking user role: " + error.getMessage());
//                Toast.makeText(LoginActivity.this, "Error checking user role.", Toast.LENGTH_SHORT).show();
//            }
//        });
//    }

    private void checkUserRole(String userId) {
        UsersDB.child(userId).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    String role = snapshot.child("role").getValue(String.class);
                    Boolean suspended = snapshot.child("suspended").getValue(Boolean.class);

                    if (suspended != null && suspended) {
                        Toast.makeText(LoginActivity.this, "Your account is suspended.", Toast.LENGTH_SHORT).show();
                        FirebaseAuth.getInstance().signOut(); // Sign out the user
                    } else if (role != null) {
                        navigateBasedOnRole(role);
                    } else {
                        Toast.makeText(LoginActivity.this, "User role not found.", Toast.LENGTH_SHORT).show();
                    }
                } else {
                    Toast.makeText(LoginActivity.this, "User not found.", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(LoginActivity.this, "Error checking user role.", Toast.LENGTH_SHORT).show();
            }
        });
    }


    private void navigateBasedOnRole(String role) {
        if (role.equals("admin")) {
            startActivity(new Intent(LoginActivity.this, AdminActivity.class));
        } else if (role.equals("user")) {
            startActivity(new Intent(LoginActivity.this, MainActivity.class));
        } else if (role.equals("moderator")) {
            startActivity(new Intent(LoginActivity.this, ModeratorActivity.class));
        } else {
            Toast.makeText(LoginActivity.this, "Unknown role.", Toast.LENGTH_SHORT).show();
        }
        finish();
    }

    private void handleLoginError(Task<AuthResult> task) {
        String errorMessage = "Authentication failed.";
        try {
            throw task.getException();
        } catch (FirebaseAuthWeakPasswordException e) {
            errorMessage = "Weak password.";
        } catch (FirebaseAuthInvalidCredentialsException e) {
            errorMessage = "Invalid email or password.";
        } catch (FirebaseAuthUserCollisionException e) {
            errorMessage = "User with this email already exists.";
        } catch (Exception e) {
            errorMessage = e.getMessage();
        }
        Log.e(TAG, "Login error: " + errorMessage);
        Toast.makeText(LoginActivity.this, errorMessage, Toast.LENGTH_LONG).show();
    }
}
