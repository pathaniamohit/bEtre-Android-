package com.example.betre;

import android.os.Bundle;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

public class SignupPage extends AppCompatActivity {

    android.widget.EditText username, email, password, confirmPassword;
    android.widget.Button register;
    android.widget.TextView textView;
    com.google.firebase.auth.FirebaseAuth mAuth;
    com.google.firebase.database.DatabaseReference UsersDB;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_signup_page);
        username = findViewById(R.id.name);
        email = findViewById(R.id.email);
        password = findViewById(R.id.password);
        confirmPassword = findViewById(R.id.confirm_password);
        textView = findViewById(R.id.acc1);
        register = findViewById(R.id.register_button);

        mAuth = com.google.firebase.auth.FirebaseAuth.getInstance();
        UsersDB = com.google.firebase.database.FirebaseDatabase.getInstance().getReference("users");

        textView.setOnClickListener(new android.view.View.OnClickListener() {
            @Override
            public void onClick(android.view.View v) {
                android.content.Intent intent = new android.content.Intent(SignupPage.this, LoginActivity.class);
                startActivity(intent);
            }
        });

    }
}