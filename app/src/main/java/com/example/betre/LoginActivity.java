package com.example.betre;

import android.content.Intent;
import android.os.Bundle;
import android.text.InputType;
import android.text.TextUtils;
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
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException;
import com.google.firebase.auth.FirebaseAuthUserCollisionException;
import com.google.firebase.auth.FirebaseAuthWeakPasswordException;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

public class LoginActivity extends AppCompatActivity {

    private EditText emailLogin, passwordLogin;
    private MaterialButton loginButton;
    private TextView forgotPassword, signUpLink;
    private FirebaseAuth mAuth;
    private FirebaseFirestore db;
    private ImageView showPasswordButton;
    private boolean isPasswordVisible = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        emailLogin = findViewById(R.id.emaillogin);
        passwordLogin = findViewById(R.id.passwordlogin);
        loginButton = findViewById(R.id.login);
        forgotPassword = findViewById(R.id.forgot_password);
        signUpLink = findViewById(R.id.signuplink);
        showPasswordButton = findViewById(R.id.show_password_button);

        mAuth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();

        signUpLink.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(LoginActivity.this, SignupPage.class));
            }
        });

        forgotPassword.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(LoginActivity.this, ForgotPassword.class));
                finish();
            }
        });

        loginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                checkUserCredentials();
            }
        });

        showPasswordButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (isPasswordVisible) {
                    passwordLogin.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);
                    showPasswordButton.setImageResource(R.drawable.ic_hide_password);
                } else {
                    passwordLogin.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD);
                    showPasswordButton.setImageResource(R.drawable.ic_show_password);
                }
                passwordLogin.setSelection(passwordLogin.getText().length());
                isPasswordVisible = !isPasswordVisible;
            }
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
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(LoginActivity.this, "Login failed!", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void checkUserRole(String userId) {
        db.collection("users").document(userId).get()
                .addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                        if (task.isSuccessful()) {
                            DocumentSnapshot document = task.getResult();
                            if (document.exists()) {
                                String role = document.getString("role");
                                navigateBasedOnRole(role);
                            } else {
                                Toast.makeText(LoginActivity.this, "User role not found.", Toast.LENGTH_SHORT).show();
                            }
                        } else {
                            Toast.makeText(LoginActivity.this, "Error checking user role.", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }

    private void navigateBasedOnRole(String role) {
        if (role != null) {
            if (role.equals("admin")) {
                startActivity(new Intent(LoginActivity.this, AdminActivity.class));
            } else {
                startActivity(new Intent(LoginActivity.this, MainActivity.class));
            }
        } else {
            Toast.makeText(LoginActivity.this, "Unable to determine role. Please try again.", Toast.LENGTH_SHORT).show();
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
        Toast.makeText(LoginActivity.this, errorMessage, Toast.LENGTH_LONG).show();
    }
}
