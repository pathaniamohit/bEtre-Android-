package com.example.betre;

import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;

public class ForgotPassword extends AppCompatActivity {

    android.widget.EditText editText;
    android.widget.Button resetbutton;
    com.google.firebase.auth.FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_forgot_password);

        editText = findViewById(com.example.betre.R.id.emailEditText);
        resetbutton = findViewById(R.id.resetPasswordButton);
        mAuth = com.google.firebase.auth.FirebaseAuth.getInstance();

        resetbutton.setOnClickListener(v -> resetPassword());
    }

    private void resetPassword() {
        String email = editText.getText().toString().trim();

        if (android.text.TextUtils.isEmpty(email) || !android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            editText.setError("Valid email is required");
            return;
        }

        mAuth.sendPasswordResetEmail(email).addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                android.widget.Toast.makeText(ForgotPassword.this, "Password reset email sent", android.widget.Toast.LENGTH_SHORT).show();
                navigateToLogin();
            } else {
                android.widget.Toast.makeText(ForgotPassword.this, "Error sending password reset email", android.widget.Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void navigateToLogin() {
        android.content.Intent intent = new android.content.Intent(ForgotPassword.this, LoginActivity.class);
        startActivity(intent);
        finish();
    }
}
