package com.example.betre;

import android.content.Intent;
import android.os.Bundle;
import android.widget.ImageView;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;

public class ForgotPassword extends AppCompatActivity {

    private android.widget.EditText editText;
    private android.widget.Button resetButton;
    private FirebaseAuth mAuth;
    private ImageView backButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_forgot_password);

        editText = findViewById(R.id.emailEditText);
        resetButton = findViewById(R.id.resetPasswordButton);
        mAuth = FirebaseAuth.getInstance();
        backButton = findViewById(R.id.back_button);

        resetButton.setOnClickListener(v -> resetPassword());

        backButton.setOnClickListener(v -> {
            startActivity(new Intent(ForgotPassword.this, SignupPage.class));
            finish();
        });
    }

    private void resetPassword() {
        String email = editText.getText().toString().trim();

        if (android.text.TextUtils.isEmpty(email) || !android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            editText.setError("Valid email is required");
            return;
        }

        mAuth.sendPasswordResetEmail(email).addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                android.widget.Toast.makeText(ForgotPassword.this, "Check your mail", android.widget.Toast.LENGTH_SHORT).show();
                navigateToLogin();
            } else {
                android.widget.Toast.makeText(ForgotPassword.this, "Error sending password reset email", android.widget.Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void navigateToLogin() {
        Intent intent = new Intent(ForgotPassword.this, LoginActivity.class);
        startActivity(intent);
        finish();
    }
}
