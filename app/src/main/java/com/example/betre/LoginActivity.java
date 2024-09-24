package com.example.betre;

import android.os.Bundle;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

public class LoginActivity extends AppCompatActivity {

    android.widget.EditText username, loginpassword;
    android.widget.Button loginbtn, forgotpassword;
    android.widget.TextView signuplink;
    com.google.firebase.auth.FirebaseAuth mAuth;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_login);

        username = findViewById(R.id.usernamelogin);
        loginpassword = findViewById(R.id.passwordlogin);
        forgotpassword = findViewById(R.id.forgot_password);
        loginbtn = findViewById(R.id.login);
        signuplink = findViewById(R.id.signuplink);
        mAuth = com.google.firebase.auth.FirebaseAuth.getInstance();

    }
}