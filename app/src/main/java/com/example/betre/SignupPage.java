package com.example.betre;

import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.AuthResult;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import androidx.annotation.NonNull;

public class SignupPage extends AppCompatActivity {

    android.widget.EditText username, email, password, confirmPassword;
    android.widget.Button register;
    android.widget.TextView textView;
    FirebaseAuth mAuth;
    DatabaseReference UsersDB;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup_page);

        username = findViewById(R.id.name);
        email = findViewById(R.id.email);
        password = findViewById(R.id.password);
        confirmPassword = findViewById(R.id.confirm_password);
        textView = findViewById(R.id.acc1);
        register = findViewById(R.id.register_button);

        mAuth = FirebaseAuth.getInstance();
        UsersDB = FirebaseDatabase.getInstance().getReference("users");

        textView.setOnClickListener(new android.view.View.OnClickListener() {
            @Override
            public void onClick(android.view.View v) {
                android.content.Intent intent = new android.content.Intent(SignupPage.this, LoginActivity.class);
                startActivity(intent);
            }
        });

        register.setOnClickListener(new android.view.View.OnClickListener() {
            @Override
            public void onClick(android.view.View v) {
                Signup();
            }
        });
    }

    private void Signup() {
        String name = username.getText().toString().trim();
        String email = this.email.getText().toString().trim();
        String password = this.password.getText().toString().trim();
        String confirmPassword = this.confirmPassword.getText().toString().trim();

        if (android.text.TextUtils.isEmpty(name)) {
            username.setError("Name is required");
            return;
        }

        if (android.text.TextUtils.isEmpty(email) || !android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            this.email.setError("Valid email is required");
            return;
        }

        if (android.text.TextUtils.isEmpty(password)) {
            this.password.setError("Password is required");
            return;
        }

        if (password.length() < 6) {
            this.password.setError("Password must be at least 6 characters");
            return;
        }

        if (android.text.TextUtils.isEmpty(confirmPassword)) {
            this.confirmPassword.setError("Confirm Password is required");
            return;
        }

        if (!password.equals(confirmPassword)) {
            this.confirmPassword.setError("Passwords do not match");
            return;
        }


        mAuth.createUserWithEmailAndPassword(email, password).addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if (task.isSuccessful()) {
                    // User registration success
                    com.google.firebase.auth.FirebaseUser user = mAuth.getCurrentUser();
                    String userId = user.getUid();
                    User newUser = new User(name, email);

                    // Save user data to database
                    UsersDB.child(userId).setValue(newUser).addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if (task.isSuccessful()) {
                                android.widget.Toast.makeText(SignupPage.this, "Registration successful!", android.widget.Toast.LENGTH_SHORT).show();
                                startActivity(new android.content.Intent(SignupPage.this, LoginActivity.class));
                                finish();
                            } else {
                                android.widget.Toast.makeText(SignupPage.this, "Failed to save user data.", android.widget.Toast.LENGTH_SHORT).show();
                            }
                        }
                    });
                } else {

                    String errorMessage = "Authentication failed.";
                    try {
                        throw task.getException();
                    } catch (com.google.firebase.auth.FirebaseAuthWeakPasswordException e) {
                        errorMessage = "Weak password.";
                    } catch (com.google.firebase.auth.FirebaseAuthInvalidCredentialsException e) {
                        errorMessage = "Invalid email.";
                    } catch (com.google.firebase.auth.FirebaseAuthUserCollisionException e) {
                        errorMessage = "User with this email already exists.";
                    } catch (Exception e) {
                        errorMessage = e.getMessage();
                    }
                    android.widget.Toast.makeText(SignupPage.this, errorMessage, android.widget.Toast.LENGTH_LONG).show();
                }
            }
        });
    }


}
